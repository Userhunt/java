package net.w3e.base.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.w3e.base.collection.CollectionBuilder;
import net.w3e.base.collection.CollectionBuilder.SimpleCollectionBuilder;
import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.dungeon.layers.DistanceLayer;
import net.w3e.base.dungeon.layers.FeatureLayer;
import net.w3e.base.dungeon.layers.ISetupLayer;
import net.w3e.base.dungeon.layers.RoomLayer;
import net.w3e.base.dungeon.layers.path.PathRepeatLayer;
import net.w3e.base.dungeon.layers.terra.BiomeLayer;
import net.w3e.base.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.base.holders.BoolHolder;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.i.WBoxI;
import net.w3e.base.math.vector.i.WVector3I;

public class DungeonGenerator {

	private final Map<WVector3I, Map<WVector3I, DungeonRoomInfo>> map = new HashMap<>();

	private final long seed;
	private final WBoxI dimension;
	private final Supplier<MapTString> dataFactory;
	private final List<Factory> layers = new ArrayList<>();

	private Random random;
	private final List<DungeonLayer> queue = new LinkedList<>();
	private final List<ISetupLayer> setup = new LinkedList<>();
	private boolean regenerate = true;

	public DungeonGenerator(long seed, WBoxI dimension, Supplier<MapTString> dataFactory, List<Factory> layers) {
		this.seed = seed;
		this.dimension = dimension;
		this.dataFactory = dataFactory;
		this.layers.addAll(layers);
	}

	public final Random random() {
		return this.random;
	}

	public final WBoxI dimension() {
		return this.dimension;
	}

	public final void regenerate() {
		this.map.clear();
		this.queue.clear();
		this.setup.clear();
		this.random = new Random(this.seed);
		this.queue.addAll(this.layers.stream().map(e -> e.create(this)).toList());
		this.regenerate = true;
		this.queue.stream().filter(e -> e instanceof ISetupLayer).map(e -> (ISetupLayer)e).forEach(this.setup::add);
	}

	public final DungeonRoomCreateInfo putOrGet(WVector3I pos) {
		if (!this.testDimension(pos)) {
			return this.createFailRoom(pos, false);
		}
		WVector3I chunk = pos.pos2Chunk();
		BoolHolder exists = new BoolHolder(true);
		DungeonRoomInfo room = map.computeIfAbsent(chunk, key -> new HashMap<>()).computeIfAbsent(pos, key -> {
			exists.setFalse();
			return DungeonRoomInfo.create(pos, chunk, this.dataFactory);
		});
		if (!exists.getBool()) {
			for (ISetupLayer setup : this.setup) {
				setup.setup(room);
			}
		}
		return DungeonRoomCreateInfo.success(room, exists.getBool());
	}

	public final DungeonRoomCreateInfo get(WVector3I pos) {
		if (this.testDimension(pos)) {
			WVector3I chunk = pos.pos2Chunk();

			Map<WVector3I, DungeonRoomInfo> m = this.map.get(chunk);
			if (m != null) {
				DungeonRoomInfo room = m.get(pos);
				if (room != null) {
					return DungeonRoomCreateInfo.success(room, true);
				}
			}
			return this.createFailRoom(pos, chunk, true);
		}
		return this.createFailRoom(pos, false);
	}
	
	public final void forEach(Consumer<DungeonRoomCreateInfo> function, boolean createIfNotExists) {
		WVector3I min = this.dimension.min();
		WVector3I max = this.dimension.max();
		for (int x = min.getXI(); x <= max.getXI(); x++) {
			for (int y = min.getYI(); y <= max.getYI(); y++) {
				for (int z = min.getZI(); z <= max.getZI(); z++) {
					WVector3I pos = new WVector3I(x, y, z);
					DungeonRoomCreateInfo room = this.get(pos);
					if (room.exists) {
						function.accept(room);
						continue;
					}
					if (createIfNotExists) {
						room = this.putOrGet(pos);
						if (!room.isInside) {
							throw new IllegalStateException("room is not inside dungeon, but must " + pos);
						}
						function.accept(room);
					}
				}
			}
		}
	}

	private final DungeonRoomCreateInfo createFailRoom(WVector3I pos, boolean isInside) {
		return createFailRoom(pos, pos.pos2Chunk(), isInside);
	}

	private final DungeonRoomCreateInfo createFailRoom(WVector3I pos, WVector3I chunk, boolean isInside) {
		return DungeonRoomCreateInfo.fail(DungeonRoomInfo.create(pos, this.dataFactory));
	}

	public record DungeonRoomCreateInfo(DungeonRoomInfo room, boolean isInside, boolean exists) {
		private static final DungeonRoomCreateInfo fail(DungeonRoomInfo room) {
			return new DungeonRoomCreateInfo(room, false, false);
		}
		private static final <T> DungeonRoomCreateInfo success(DungeonRoomInfo room, boolean exists) {
			return new DungeonRoomCreateInfo(room, true, exists);
		}
		public final DungeonRoomCreateInfo setWall(boolean value) {
			this.room.setWall(value);
			return this;
		}
		public final DungeonRoomCreateInfo setWall() {
			return this.setWall(true);
		}
		public final WVector3I pos() {
			return this.room.pos();
		}
		public final WVector3I chunk() {
			return this.room.chunk();
		}
		public final MapTString data() {
			return this.room.data();
		}
		public final boolean notExistsOrWall() {
			return !this.exists || this.room.isWall();
		}
		public final boolean isEnterance() {
			return this.room.isEnterance();
		}
		public final boolean isWall() {
			return this.room.isWall();
		}
	}

	private final boolean testDimension(WVector3I pos) {
		return this.dimension.contains(pos);
	}

	public final DungeonGenerator copy(Long seed, WBoxI dimension, boolean data) {
		DungeonGenerator dungeon = new DungeonGenerator(seed == null ? this.seed : seed, dimension == null ? this.dimension : dimension, this.dataFactory, this.layers);
		if (data) {
			for (Map<WVector3I, DungeonRoomInfo> chunk : this.map.values()) {
				for (Entry<WVector3I, DungeonRoomInfo> entry : chunk.entrySet()) {
					DungeonRoomCreateInfo info = dungeon.putOrGet(entry.getKey());
					if (info.isInside) {
						info.room.copyFrom(entry.getValue());
					}
				}
			}
		}

		return dungeon;
	}

	public final Map<WVector3I, Map<WVector3I, DungeonRoomInfo>> getRooms() {
		return this.map;
	}

	public final int generate() {
		int i = 100;
		if (!queue.isEmpty()) {
			DungeonLayer generator = this.queue.getFirst();
			if (this.regenerate) {
				generator.regenerate(false);
				this.regenerate = false;
				i = 1;
			} else {
				i = generator.generate();
				if (i == 100) {
					i = 0;
					this.queue.removeFirst();
					this.regenerate = true;
				}
			}
		}

		float prev = (this.layers.size() - this.queue.size()) * 100f;

		return BMatUtil.round((prev + i) / this.layers.size());
	}

	public final DungeonLayer getFirst() {
		return this.queue.isEmpty() ? null : this.queue.getFirst();
	}

	public static interface Factory {
		DungeonLayer create(DungeonGenerator generator);
	}

	public static final SimpleCollectionBuilder<Factory, ArrayList<Factory>> factoryCollectionBuilder() {
		return CollectionBuilder.list(Factory.class);
	}

	public static DungeonGenerator example(long seed) {
		int size = 10;
		return new DungeonGenerator(seed, new WBoxI(-size, 0, -size, size, 0, size), MapTString::new, factoryCollectionBuilder().add(
			// path
			PathRepeatLayer::example,
			// distance
			DistanceLayer::new,
			// temperature, wet, difficulty
			CompositeTerraLayer::example,
			// biomes
			BiomeLayer::example,
			// rooms
			RoomLayer::example,
			// features - spawners, chests, ?
			FeatureLayer::example
			//clear
			
		).build());
	}
}
