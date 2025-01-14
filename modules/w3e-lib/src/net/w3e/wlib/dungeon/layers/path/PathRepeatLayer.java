package net.w3e.wlib.dungeon.layers.path;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.Vec3I;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonPos;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonLayer.IPathLayer;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.json.ILayerDeserializerAdapter;
import net.w3e.wlib.log.LogUtil;

@DefaultJsonCodec(PathRepeatLayer.PathRepeatJsonAdapter.class)
public class PathRepeatLayer<T extends DungeonLayer & IPathLayer> extends DungeonLayer implements IPathLayer {

	public static final String TYPE = "path/repeat";

	private final DungeonLayer layer;
	private final float minumumPercent;
	private final int countPerStep;

	public PathRepeatLayer(DungeonGenerator generator, T layer, float minumumPercent, int countPerStep) {
		super(TYPE, generator);
		this.layer = layer;
		this.minumumPercent = minumumPercent;
		this.countPerStep = countPerStep;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final PathRepeatLayer<T> withDungeon(DungeonGenerator generator) {
		return new PathRepeatLayer<>(generator, (T)this.layer.withDungeon(generator), this.minumumPercent, this.countPerStep);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void add(Vec3I pos, Direction direction) throws DungeonException {
		((T)this.layer).add(pos, direction);
	}

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		this.layer.regenerate(composite);
	}

	@Override
	public final int generate() throws DungeonException {
		int value = this.layer.generate();
		if (value >= 100) {
			List<DungeonRoomInfo> rooms = new ArrayList<>();
			this.forEach(room -> {
				if (!room.room().isWall()) {
					rooms.add(room.room());
				}
			});
			Vec3I size = this.dungeonSize().addI(Vec3I.SINGLE);
			int s = size.xi() * size.yi() * size.zi();
			float p = rooms.size() * 100f / s;
			if (p <= this.minumumPercent) {
				int i = this.countPerStep;
				while (i > 0 && !rooms.isEmpty()) {
					DungeonRoomInfo room = rooms.remove(this.random().nextInt(rooms.size()));
					this.add(room.pos(), new DungeonPos().getDirection(this.random()));
				}
				return Math.min(FastMath.round(p / this.minumumPercent * 100), 99);
			}
		}
		return value;
	}

	public static class PathRepeatLayerAdapter<T extends DungeonLayer & IPathLayer> implements ILayerDeserializerAdapter<PathRepeatLayer<T>, PathRepeatLayer<T>>, JsonSerializer<PathRepeatLayer<T>> {

		@Override
		public final PathRepeatLayer<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			PathRepeatLayerData<T> data = new PathRepeatLayerData<>();

			JsonElement layerJson = jsonObject.get("layer");
			if (layerJson != null) {
				data.layer = context.deserialize(layerJson, DungeonLayer.class);
			}
			JsonElement minumumPercentJson = jsonObject.get("minumumPercent");
			if (minumumPercentJson != null) {
				data.minumumPercent = context.deserialize(minumumPercentJson, float.class);
			}
			JsonElement countPerStepJson = jsonObject.get("countPerStep");
			if (countPerStepJson != null) {
				data.countPerStep = context.deserialize(countPerStepJson, int.class);
			}

			this.nonNull("layer", data.layer);
			if (data.layer instanceof IPathLayer && !(data.layer instanceof PathRepeatLayer<?>)) {
				this.lessThan("minimumPercent", data.minumumPercent);
				this.lessThan("countPerStep", data.countPerStep);
			} else {
				throw new JsonSyntaxException(LogUtil.EXPECTED.createMsg("layer", "path layer", data.layer.getClass().getSimpleName()));
			}

			return data.withDungeon(null);
		}

		@Override
		public JsonElement serialize(PathRepeatLayer<T> src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject json = new JsonObject();
			json.add("layer", context.serialize(src.layer, DungeonLayer.class));
			json.addProperty("minumumPercent", src.minumumPercent);
			json.addProperty("countPerStep", src.countPerStep);
			return json;
		}
	}

	private static class PathRepeatLayerData<T extends DungeonLayer & IPathLayer> implements ILayerData<PathRepeatLayer<T>> {
		private ILayerData<?> layer;
		private float minumumPercent = 0;
		private int countPerStep = 1;

		@Override
		@SuppressWarnings("unchecked")
		public final PathRepeatLayer<T> withDungeon(DungeonGenerator generator) {
			return new PathRepeatLayer<>(generator, (T)this.layer.withDungeon(generator), this.minumumPercent, this.countPerStep);
		}
	}

	static class PathRepeatJsonAdapter extends JsonReflectiveBuilderCodec<PathRepeatLayerData<?>> {

		public PathRepeatJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, PathRepeatLayerData.class, registry);
		}

		private static class PathRepeatLayerData<T extends DungeonLayer & IPathLayer> implements ILayerData<PathRepeatLayer<T>> {
			private DungeonLayer layer;
			private float minumumPercent = 0;
			private int countPerStep = 1;
	
			@Override
			@SuppressWarnings("unchecked")
			public final PathRepeatLayer<T> withDungeon(DungeonGenerator generator) {
				this.nonNull("layer", this.layer);
				if (this.layer instanceof IPathLayer && !(this.layer instanceof PathRepeatLayer<?>)) {
					this.lessThan("minimumPercent", this.minumumPercent);
					this.lessThan("countPerStep", this.countPerStep);
				} else {
					throw new JsonSyntaxException(LogUtil.EXPECTED.createMsg("layer", "path layer", this.layer.getClass().getSimpleName()));
				}
				return new PathRepeatLayer<>(generator, (T)this.layer.withDungeon(generator), this.minumumPercent, this.countPerStep);
			}
		}
	}

	public static final PathRepeatLayer<WormLayer> example(DungeonGenerator generator, int size) {
		size -= 1;
		size *= 2;
		size += 1;
		size *= size;
		float min = 2205 / size;
		return new PathRepeatLayer<>(generator, WormLayer.example(generator), min, 1);
	}

}
