package net.w3e.base.dungeon.layers.path;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.w3e.base.dungeon.DungeonException;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonPos;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.dungeon.DungeonLayer.IPathLayer;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;
import net.w3e.base.message.MessageUtil;

public class PathRepeatLayer<T extends DungeonLayer & IPathLayer> extends DungeonLayer implements IPathLayer {

	public static final String TYPE = "path/repeat";

	private final T layer;
	private final float minumumPercent;
	private final int countPerStep;

	public PathRepeatLayer(DungeonGenerator generator, T layer, float minumumPercent, int countPerStep) {
		super(generator);
		this.layer = layer;
		this.minumumPercent = minumumPercent;
		this.countPerStep = countPerStep;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final PathRepeatLayer<T> withDungeonImpl(DungeonGenerator generator) {
		return new PathRepeatLayer<>(generator, (T)this.layer.withDungeon(generator), this.minumumPercent, this.countPerStep);
	}

	@Override
	public final void add(WVector3I pos, WDirection direction) throws DungeonException {
		this.layer.add(pos, direction);
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
			WVector3I size = this.dungeonSize().add(new WVector3I(1, 1, 1));
			int s = size.getXI() * size.getYI() * size.getZI();
			float p = rooms.size() * 100f / s;
			if (p <= this.minumumPercent) {
				int i = this.countPerStep;
				while (i > 0 && !rooms.isEmpty()) {
					DungeonRoomInfo room = rooms.remove(this.random().nextInt(rooms.size()));
					this.add(room.pos(), new DungeonPos().getDirection(this.random()));
				}
				return Math.min(BMatUtil.round(p / this.minumumPercent * 100), 99);
			}
		}
		return value;
	}

	public static class PathRepeatLayerData<T extends DungeonLayer & IPathLayer> implements ILayerAdapter<PathRepeatLayer<?>>, JsonDeserializer<PathRepeatLayerData<?>> {

		public static final PathRepeatLayerData<?> INSTANCE = new PathRepeatLayerData<>();

		private ILayerAdapter<?> layer;
		private float minumumPercent = 0;
		private int countPerStep = 1;

		@Override
		public final PathRepeatLayerData<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			PathRepeatLayerData<?> path = new PathRepeatLayerData<>();

			JsonElement layerJson = jsonObject.get("layer");
			if (layerJson != null) {
				path.layer = context.deserialize(layerJson, DungeonLayer.class);
			}
			JsonElement minumumPercentJson = jsonObject.get("minumumPercent");
			if (minumumPercentJson != null) {
				path.minumumPercent = context.deserialize(minumumPercentJson, float.class);
			}
			JsonElement countPerStepJson = jsonObject.get("countPerStep");
			if (countPerStepJson != null) {
				path.countPerStep = context.deserialize(countPerStepJson, int.class);
			}

			return path;
		}

		@Override
		@SuppressWarnings("unchecked")
		public final PathRepeatLayer<?> withDungeon(DungeonGenerator generator) {
			this.nonNull("layer", this.layer);
			if (this.layer instanceof IPathLayer && !(this.layer instanceof PathRepeatLayer<?>)) {
				this.lessThan("minimumPercent", this.minumumPercent);
				this.lessThan("countPerStep", this.countPerStep);
				return new PathRepeatLayer<>(generator, (T)this.layer.withDungeon(generator), this.minumumPercent, this.countPerStep);
			} else {
				throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg("layer", "path layer", this.layer.getClass().getSimpleName()));
			}
		}

	}

	public static final PathRepeatLayer<WormLayer> example(DungeonGenerator generator, int size) {
		size -= 1;
		size *= 2;
		size += 1;
		size *= size;
		float min = 2205 / size;
		return new PathRepeatLayer<>(generator, WormLayer.example(generator), min, 1).setTypeKey(TYPE);
	}

}
