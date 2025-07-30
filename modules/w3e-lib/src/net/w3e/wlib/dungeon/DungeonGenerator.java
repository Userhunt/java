package net.w3e.wlib.dungeon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializeBuilder;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.Holders.BooleanHolder;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.lib2.utils.logger.SKDSLoggerFactory;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;
import net.w3e.wlib.dungeon.layers.DungeonLayerFactory;
import net.w3e.wlib.dungeon.layers.ISetupRoomLayer;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilterValues;
import net.w3e.wlib.mat.VecUtil;
import net.w3e.wlib.mat.WBoxI;

public class DungeonGenerator {

	public static final SKDSLogger LOGGER = SKDSLoggerFactory.getLogger();

	private final transient Map<Vec3I, Map<Vec3I, DungeonRoomInfo>> map = new HashMap<>();

	private final Random random;
	private final WBoxI dimension;
	private final MapTString dataFactory;

	private final int layerCount;
	private final transient List<DungeonLayer> queue = new LinkedList<>();
	private final transient List<ISetupRoomLayer> roomSetup = new LinkedList<>();
	private transient boolean setupLayer = true;

	public DungeonGenerator(long seed, WBoxI dimension, MapTString dataFactory, List<DungeonLayerFactory> layers) {
		this.random = new Random(seed);
		this.dimension = dimension;
		this.dataFactory = dataFactory;
		this.layerCount = layers.size();
		layers.stream().map(e -> e.create(this)).forEach(l -> {
			this.queue.add(l);
			if (l instanceof ISetupRoomLayer s) {
				this.roomSetup.add(s);
			}
		});
	}

	public final Random random() {
		return this.random;
	}

	public final WBoxI dimension() {
		return this.dimension;
	}

	public final DungeonRoomCreateInfo putOrGet(Vec3I pos) {
		if (!this.testPosIsInside(pos)) {
			return this.createFailRoom(pos, false);
		}
		Vec3I chunk = VecUtil.pos2Chunk(pos);
		BooleanHolder exists = new BooleanHolder(true);
		DungeonRoomInfo room = map.computeIfAbsent(chunk, _ -> new HashMap<>()).computeIfAbsent(pos, _ -> {
			exists.setValue(false);
			return DungeonRoomInfo.create(pos, chunk, this.dataFactory);
		});
		if (!exists.isValue()) {
			for (ISetupRoomLayer setup : this.roomSetup) {
				setup.setupRoom(room);
			}
		}
		return DungeonRoomCreateInfo.success(room, exists.isValue());
	}

	public final DungeonRoomCreateInfo put(DungeonRoomInfo room) {
		DungeonRoomCreateInfo info = this.putOrGet(room.pos());
		if (info.isInside) {
			this.map.get(info.room.chunk()).put(room.pos(), room);
			return DungeonRoomCreateInfo.success(room, info.exists);
		} else {
			return info;
		}
	}

	public final DungeonRoomCreateInfo get(Vec3I pos) {
		if (this.testPosIsInside(pos)) {
			Vec3I chunk = VecUtil.pos2Chunk(pos);

			Map<Vec3I, DungeonRoomInfo> m = this.map.get(chunk);
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

	public final DungeonRoomCreateInfo removeRoom(Vec3I pos) {
		if (this.testPosIsInside(pos)) {
			Vec3I chunk = VecUtil.pos2Chunk(pos);
			Map<Vec3I, DungeonRoomInfo> m = this.map.get(chunk);
			if (m != null) {
				return DungeonRoomCreateInfo.success(m.remove(pos), true);
			}
			return this.createFailRoom(pos, chunk, true);
		}
		return this.createFailRoom(pos, false);
	}

	public final RoomLayerFilterValues getRoomValues(DungeonRoomInfo room) {
		return DungeonJsonAdapters.INSTANCE.roomFilterAdapters.createFilters(room);
	}

	public final void forEach(Consumer<DungeonRoomCreateInfo> function, boolean createIfNotExists) {
		Vec3I min = this.dimension.min();
		Vec3I max = this.dimension.max();
		for (int x = min.xi(); x <= max.xi(); x++) {
			for (int y = min.yi(); y <= max.yi(); y++) {
				for (int z = min.zi(); z <= max.zi(); z++) {
					Vec3I pos = new Vec3I(x, y, z);
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

	private final DungeonRoomCreateInfo createFailRoom(Vec3I pos, boolean isInside) {
		return createFailRoom(pos, VecUtil.pos2Chunk(pos), isInside);
	}

	private final DungeonRoomCreateInfo createFailRoom(Vec3I pos, Vec3I chunk, boolean isInside) {
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
		public final Vec3I pos() {
			return this.room.pos();
		}
		public final Vec3I chunk() {
			return this.room.chunk();
		}
		public final MapTString data() {
			return this.room.data();
		}
		public final boolean notExistsOrWall() {
			return !this.exists || this.room.isWall();
		}
		public final boolean isentrance() {
			return this.room.isEntrance();
		}
		public final boolean isWall() {
			return this.room.isWall();
		}
	}

	public final boolean testPosIsInside(Vec3I pos) {
		return this.dimension.contains(pos);
	}

	public final Map<Vec3I, Map<Vec3I, DungeonRoomInfo>> getChunks() {
		return this.map;
	}

	public final Map<Vec3I, DungeonRoomInfo> getRooms() {
		Map<Vec3I, DungeonRoomInfo> map = new HashMap<>();
		for (Map<Vec3I, DungeonRoomInfo> chunk : this.map.values()) {
			for (Entry<Vec3I, DungeonRoomInfo> entry : chunk.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

	public final CompletableFuture<DungeonGeneratorResult> generateAsync(DungeonGenerationCallback callback) {
		return CompletableFuture.supplyAsync(() -> {
			return generate(callback);
		});
	}

	public DungeonGeneratorResult generate(DungeonGenerationCallback callback) {
		DungeonGeneratorResult result = null;
		while (!queue.isEmpty()) {
			float progress = 1;
			DungeonLayer layer = this.queue.getFirst();
			if (this.setupLayer) {
				layer.setupLayer(false);
				this.setupLayer = false;
			}
			progress = layer.generate();
			if (progress >= 1f) {
				progress = 0;
				this.queue.removeFirst();
				this.setupLayer = true;
			}

			float prev = (this.layerCount - this.queue.size());
			progress = (prev + progress) / this.layerCount;
			progress = Math.max(0.001f, progress);

			result = new DungeonGeneratorResult(this.dimension, this.map, progress, layer);

			CompletableFuture<Boolean> next;
			try {
				next = callback.callback(result);
			} catch (InterruptedException e) {
				return result.cancel();
			}
			if (!next.join()) {
				return result.cancel();
			}
		}
		return result;
	}

	public static interface DungeonGenerationCallback {
		CompletableFuture<Boolean> callback(DungeonGeneratorResult result) throws InterruptedException;

		CompletableFuture<Boolean> DONE = CompletableFuture.completedFuture(true);
		DungeonGenerationCallback CONTINUE = (_) -> DONE;
	}

	public final DungeonLayer getFirst() {
		return this.queue.isEmpty() ? null : this.queue.getFirst();
	}

	public final List<DungeonLayer> layers() {
		Stream<DungeonLayer> stream = this.queue.stream().map(e -> e);
		return stream.toList();
	}

	public static class DungeonGeneratorJsonAdapter extends JsonReflectiveBuilderCodec<DungeonGenerator> {
		public DungeonGeneratorJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, EDungeon.class, registry);
		}

		private static class EDungeon implements JsonDeserializeBuilder<DungeonGenerator> {
			private long seed = 0;
			private WBoxI dimension = new WBoxI(0, 0, 0, 0, 0, 0).expand(4, 0, 4);
			private MapTString data = new MapTString();
			private DungeonLayer[] layers = new DungeonLayer[0];
			private final transient List<DungeonLayerFactory> layerFactories = new ArrayList<>();

			@Override
			public final DungeonGenerator build() {
				if (this.layerFactories.size() != this.layers.length) {
					this.layerFactories.clear();
					for (DungeonLayer layer : this.layers) {
						if (layer == null) {
							throw new NullPointerException();
						}
						this.layerFactories.add(generator -> layer.withDungeon(generator));
					}
				}
				MapTString map = this.data != null ? new MapTString(this.data) : new MapTString();
				return new DungeonGenerator(this.seed, this.dimension, map, this.layerFactories);
			}
		}
	}
}
