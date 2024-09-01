package net.w3e.base.dungeon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.w3e.base.collection.CollectionBuilder;
import net.w3e.base.collection.CollectionBuilder.SimpleCollectionBuilder;
import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.dungeon.json.DungeonExampleAdapter;
import net.w3e.base.dungeon.json.DungeonExampleAdapter.EDungeon;
import net.w3e.base.dungeon.layers.ClearLayer;
import net.w3e.base.dungeon.layers.DistanceLayer;
import net.w3e.base.dungeon.layers.FeatureLayer;
import net.w3e.base.dungeon.layers.ISetupLayer;
import net.w3e.base.dungeon.layers.RoomLayer;
import net.w3e.base.dungeon.layers.RotateLayer;
import net.w3e.base.dungeon.layers.path.PathRepeatLayer;
import net.w3e.base.dungeon.layers.path.WormLayer;
import net.w3e.base.dungeon.layers.roomvalues.AbstractLayerRoomValues;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomValues;
import net.w3e.base.dungeon.layers.terra.BiomeLayer;
import net.w3e.base.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.base.holders.BoolHolder;
import net.w3e.base.json.FileUtil;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WBoxI;
import net.w3e.base.math.vector.i.WVector3I;

public class DungeonGenerator {

	public static final Logger LOGGER = LogManager.getLogger();

	private final transient Map<WVector3I, Map<WVector3I, DungeonRoomInfo>> map = new HashMap<>();

	private final long seed;
	private final WBoxI dimension;
	private final Supplier<MapTString> dataFactory;
	private final List<LayerFactory> layers = new ArrayList<>();

	private transient Random random;
	private final transient List<DungeonLayer> queue = new LinkedList<>();
	private final transient List<ISetupLayer> setup = new LinkedList<>();
	private transient boolean regenerate = true;

	public DungeonGenerator(long seed, WBoxI dimension, Supplier<MapTString> dataFactory, List<LayerFactory> layers) {
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

	public final DungeonRoomCreateInfo put(DungeonRoomInfo room) {
		DungeonRoomCreateInfo info = this.putOrGet(room.pos());
		if (info.isInside) {
			this.map.get(info.room.chunk()).put(room.pos(), room);
			return DungeonRoomCreateInfo.success(room, info.exists);
		} else {
			return info;
		}
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

	public final DungeonRoomCreateInfo removeRoom(WVector3I pos) {
		if (this.testDimension(pos)) {
			WVector3I chunk = pos.pos2Chunk();
			Map<WVector3I, DungeonRoomInfo> m = this.map.get(chunk);
			if (m != null) {
				return DungeonRoomCreateInfo.success(m.remove(pos), true);
			}
			return this.createFailRoom(pos, chunk, true);
		}
		return this.createFailRoom(pos, false);
	}

	public AbstractLayerRoomValues<BaseLayerRoomRange> getRoomValues(DungeonRoomInfo room) {
		return new BaseLayerRoomValues(room);
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

	public final boolean testDimension(WVector3I pos) {
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

	public final Map<WVector3I, Map<WVector3I, DungeonRoomInfo>> getChunks() {
		return this.map;
	}

	public final Map<WVector3I, DungeonRoomInfo> getRooms() {
		Map<WVector3I, DungeonRoomInfo> map = new HashMap<>();
		for (Map<WVector3I, DungeonRoomInfo> chunk : this.map.values()) {
			for (Entry<WVector3I, DungeonRoomInfo> entry : chunk.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}

	public final int generate() throws DungeonException {
		if (this.layers.isEmpty()) {
			return 100;
		}
		int i = 100;
		if (!queue.isEmpty()) {
			DungeonLayer layer = this.queue.getFirst();
			if (this.regenerate) {
				layer.regenerate(false);
				this.regenerate = false;
				i = 1;
			} else {
				i = layer.generate();
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

	public final List<DungeonLayer> layers() {
		return this.layers.stream().map(e -> e.create(this)).toList();
	}

	public static interface LayerFactory {
		DungeonLayer create(DungeonGenerator generator);
	}

	public static final SimpleCollectionBuilder<LayerFactory, ArrayList<LayerFactory>> factoryCollectionBuilder() {
		return CollectionBuilder.list(LayerFactory.class);
	}

	public static final DungeonGenerator example(long seed, WDirection direction, boolean debug) {
		int size = 9;
		DungeonGenerator generator = new DungeonGenerator(seed, new WBoxI(-size, 0, -size, size, 0, size), MapTString::new, factoryCollectionBuilder().add(
			// path
			gen -> PathRepeatLayer.example(gen, size),
			// distance
			DistanceLayer::example,
			// temperature, wet, difficulty
			CompositeTerraLayer::example,
			// biomes
			BiomeLayer::example,
			// rooms
			RoomLayer::example,
			// features - spawners, chests, ?
			FeatureLayer::example,
			// clear for save
			ClearLayer::example,
			// rotation
			gen -> new RotateLayer(gen, direction)
		).build());
		return exampleSave(generator, debug);
	}

	public static final DungeonGenerator example1(long seed, WDirection direction, boolean debug) {
		int size = 4;
		DungeonGenerator generator = new DungeonGenerator(seed, new WBoxI(-size, 0, -size, size, 0, size), MapTString::new, factoryCollectionBuilder().add(
			// path
			WormLayer::example,
			// distance
			DistanceLayer::example,
			// temperature, wet, difficulty
			CompositeTerraLayer::example,
			// biomes
			BiomeLayer::example,
			// rooms
			RoomLayer::example,

			// clear for save
			ClearLayer::example,
			// rotation
			gen -> new RotateLayer(gen, direction)
		).build());
		return exampleSave(generator, debug);
	}

	public static final DungeonGenerator exampleSave(DungeonGenerator generator, boolean debug) {
		JsonObject json = JsonParser.parseString(DungeonExampleAdapter.GSON.toJson(generator)).getAsJsonObject();
		FileUtil.write(new File(String.format("dungeon/example_%s.json", generator.seed)), json);

		if (debug) {
			DungeonGenerator data = DungeonExampleAdapter.GSON.fromJson(json, EDungeon.class).createInstance();

			JsonObject result = JsonParser.parseString(DungeonExampleAdapter.GSON.toJson(data)).getAsJsonObject();

			if (!json.equals(result)) {
				JsonArray jsonSave = json.remove("layers").getAsJsonArray();
				JsonArray jsonRead = result.remove("layers").getAsJsonArray();
				if (!json.equals(result)) {
					System.out.println(json);
					System.out.println(result);
				}
				if (!jsonSave.equals(jsonRead)) {
					if (jsonSave.size() != jsonRead.size()) {
						System.out.println("wrong size");
					} else {
						for (int i = 0; i < jsonSave.size(); i++) {
							JsonElement js = jsonSave.get(i);
							JsonElement jr = jsonRead.get(i);
							if (!js.equals(jr)) {
								System.out.println(String.format("layer[%s]", i));
								System.out.println(js);
								System.out.println(jr);
							}
						}
					}
				}
			} else {
				return data;
			}
		}
		return generator;
	}

	public static void main(String[] args) {
		example(0, WDirection.SOUTH, true);
	}
}
