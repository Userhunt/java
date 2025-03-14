package net.w3e.wlib.dungeon.layers.path;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.io.json.exception.JsonIllegalStateException;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonPos;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonLayer.IPathLayer;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.log.LogUtil;

@DefaultJsonCodec(PathRepeatLayer.PathRepeatJsonAdapter.class)
public class PathRepeatLayer<T extends DungeonLayer & IPathLayer> extends DungeonLayer implements IPathLayer {

	public static final String TYPE = "path/repeat";

	private final DungeonLayer layer;
	private final float minumumPercent;
	private final int countPerStep;

	public PathRepeatLayer(DungeonGenerator generator, T layer, float minumumPercent, int countPerStep) {
		super(JSON_MAP.PATH_REPEAT, generator);
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
	public final void setupLayer(boolean composite) throws DungeonException {
		this.layer.setupLayer(composite);
	}

	@Override
	public final float generate() throws DungeonException {
		float value = this.layer.generate();
		if (value >= 1) {
			List<DungeonRoomInfo> rooms = new ArrayList<>();
			this.forEach(room -> {
				if (!room.room().isWall()) {
					rooms.add(room.room());
				}
			});
			Vec3I size = this.dungeonSize().addI(Vec3I.SINGLE);
			int s = size.xi() * size.yi() * size.zi();
			final float p = rooms.size() * 100f / s;
			if (p <= this.minumumPercent) {
				int i = this.countPerStep;
				boolean complete = false;
				while (i > 0 && !rooms.isEmpty()) {
					DungeonRoomInfo room = rooms.remove(this.random().nextInt(rooms.size()));
					Direction direction = DungeonPos.getDirectionOrRandom(null, this.random());
					DungeonRoomCreateInfo next = this.get(room.pos().addI(direction));
					if (!next.exists()) {
						this.add(room.pos(), direction);
						complete = true;
						i--;
						continue;
					}
				}
				if (!complete) {
					throw new DungeonException("Path repeat layer cant create new way");
				}
				return Math.min(p / this.minumumPercent * 1f, 0.999f);
			}
		}
		return value;
	}

	static class PathRepeatJsonAdapter extends JsonReflectiveBuilderCodec<PathRepeatLayer<?>> {

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
					throw new JsonIllegalStateException(LogUtil.EXPECTED.createMsg("layer", "path layer", this.layer.getClass().getSimpleName()));
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
