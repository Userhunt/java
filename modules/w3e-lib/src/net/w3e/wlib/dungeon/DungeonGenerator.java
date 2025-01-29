package net.w3e.wlib.dungeon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;

import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.elements.JsonArray;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.Vec3I;
import net.skds.lib2.utils.Holders.BooleanHolder;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.lib2.utils.logger.SKDSLoggerFactory;
import net.w3e.wlib.collection.CollectionBuilder;
import net.w3e.wlib.collection.CollectionBuilder.SimpleCollectionBuilder;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;
import net.w3e.wlib.dungeon.json.DungeonJsonAdaptersString;
import net.w3e.wlib.dungeon.json.DungeonJsonAdaptersString.DungeonGeneratorJsonAdapter;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.DungeonLayerFactory;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.ISetupLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.RotateLayer;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilterValues;
import net.w3e.wlib.dungeon.layers.path.PathRepeatLayer;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.json.CompareJsonUtil;
import net.w3e.wlib.mat.VecUtil;
import net.w3e.wlib.mat.WBoxI;

@DefaultJsonCodec(DungeonGeneratorJsonAdapter.class)
public class DungeonGenerator {

	public static final SKDSLogger LOGGER = SKDSLoggerFactory.getLogger();

	private final transient Map<Vec3I, Map<Vec3I, DungeonRoomInfo>> map = new HashMap<>();

	private final long seed;
	private final WBoxI dimension;
	private final MapTString dataFactory;
	private final List<DungeonLayerFactory> layers = new ArrayList<>();

	private transient Random random;
	private final transient List<DungeonLayer> queue = new LinkedList<>();
	private final transient List<ISetupLayer> setup = new LinkedList<>();
	private transient boolean regenerate = true;

	public DungeonGenerator(long seed, WBoxI dimension, MapTString dataFactory, List<DungeonLayerFactory> layers) {
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

	public final DungeonRoomCreateInfo putOrGet(Vec3I pos) {
		if (!this.testDimension(pos)) {
			return this.createFailRoom(pos, false);
		}
		Vec3I chunk = VecUtil.pos2Chunk(pos);
		BooleanHolder exists = new BooleanHolder(true);
		DungeonRoomInfo room = map.computeIfAbsent(chunk, key -> new HashMap<>()).computeIfAbsent(pos, key -> {
			exists.setValue(false);
			return DungeonRoomInfo.create(pos, chunk, this.dataFactory);
		});
		if (!exists.isValue()) {
			for (ISetupLayer setup : this.setup) {
				setup.setup(room);
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
		if (this.testDimension(pos)) {
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
		if (this.testDimension(pos)) {
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
			return this.room.isentrance();
		}
		public final boolean isWall() {
			return this.room.isWall();
		}
	}

	public final boolean testDimension(Vec3I pos) {
		return this.dimension.contains(pos);
	}

	public final DungeonGenerator copy(Long seed, WBoxI dimension, boolean data) {
		DungeonGenerator dungeon = new DungeonGenerator(seed == null ? this.seed : seed, dimension == null ? this.dimension : dimension, this.dataFactory, this.layers);
		if (data) {
			for (Map<Vec3I, DungeonRoomInfo> chunk : this.map.values()) {
				for (Entry<Vec3I, DungeonRoomInfo> entry : chunk.entrySet()) {
					DungeonRoomCreateInfo info = dungeon.putOrGet(entry.getKey());
					if (info.isInside) {
						info.room.copyFrom(entry.getValue());
					}
				}
			}
		}

		return dungeon;
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

		return FastMath.round((prev + i) / this.layers.size());
	}

	public final DungeonLayer getFirst() {
		return this.queue.isEmpty() ? null : this.queue.getFirst();
	}

	public final List<DungeonLayer> layers() {
		Stream<DungeonLayer> stream = this.layers.stream().map(e -> e.create(this));
		return stream.toList();
	}

	public static final SimpleCollectionBuilder<DungeonLayerFactory, ArrayList<DungeonLayerFactory>> factoryCollectionBuilder() {
		return CollectionBuilder.list(DungeonLayerFactory.class);
	}

	public static final DungeonGenerator example(long seed, Direction direction, boolean debug) {
		int size = 8;
		SimpleCollectionBuilder<DungeonLayerFactory, ArrayList<DungeonLayerFactory>> layers = factoryCollectionBuilder().add(
			// path
			gen -> PathRepeatLayer.example(gen, size),
			//WormLayer::example,
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
			ClearLayer::example
		);
		if (direction != Direction.SOUTH) {
			layers.add(gen -> new RotateLayer(gen, direction));
		}
		DungeonGenerator generator = new DungeonGenerator(seed, new WBoxI(-size, 0, -size, size, 0, size), new MapTString(), layers.build());
		return exampleSave(generator, debug);
	}

	public static final DungeonGenerator exampleSave(DungeonGenerator generator, boolean debug) {
		DungeonGenerator data;

		/*data = exampleGson(jsonObject, debug);
		if (data != null) {
			generator = data;
		}*/

		data = exampleSasaiGson(generator, debug);
		if (data != null) {
			generator = data;
		}

		return generator;
	}

	/*private static DungeonGenerator exampleGson(DungeonGenerator generator, boolean debug) {
		System.out.println("gson start");
		Gson gson = JsonUtils.getGSON_COMPACT();

		final JsonObject jsonObject = JsonParser.parseString(JsonUtils.getGSON_COMPACT().toJson(generator)).getAsJsonObject();
		JsonUtils.saveConfig(new File(String.format("dungeon/example_%s.json", generator.seed)), jsonObject);

		DungeonGenerator data = gson.fromJson(jsonObject, DungeonGenerator.class);

		JsonObject result = JsonParser.parseString(gson.toJson(data)).getAsJsonObject();

		if (!jsonObject.equals(result)) {
			JsonArray jsonSave = jsonObject.remove("layers").getAsJsonArray();
			JsonArray jsonRead = result.remove("layers").getAsJsonArray();
			if (!jsonObject.equals(result)) {
				System.out.println(jsonObject);
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
							System.err.println(String.format("layer[%s]", i));
							System.err.println(js);
							System.err.println(jr);
						}
					}
				}
			}
			return null;
		} else {
			return data;
		}
	}*/

	private static DungeonGenerator exampleSasaiGson(DungeonGenerator generator, boolean debug) {
		System.out.println("sasai start");
		final JsonObject jsonObject = JsonUtils.parseJson(JsonUtils.toJson(generator), JsonObject.class);
		JsonUtils.saveJson(new File(String.format("dungeon/example_%s_sasai.json", generator.seed)), jsonObject);

		if (!debug) {
			return generator;
		}

		DungeonGenerator data = JsonUtils.parseJson(jsonObject, DungeonGenerator.class);

		JsonObject result = JsonUtils.parseJson(JsonUtils.toJson(data), JsonObject.class);

		CompareJsonUtil.CompareResult compare = CompareJsonUtil.compare(jsonObject, result);
		System.out.println(compare.print(true));

		if (!jsonObject.equals(result)) {
			JsonArray jsonSave = jsonObject.remove("layers").getAsJsonArray();
			JsonArray jsonRead = result.remove("layers").getAsJsonArray();
			if (!jsonObject.equals(result)) {
				System.out.println(jsonObject);
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
							System.err.println(String.format("layer[%s]", i));
							System.err.println(js);
							System.err.println(jr);
						}
					}
				}
			}
			return data;
		}

		return null;
	}

	public static void main(String[] args) {
		SKDSLogger.replaceOuts();

		DungeonJsonAdaptersString.initString();

		//InnerDungeonGenerator inner = new InnerDungeonGenerator();

		example(0, Direction.SOUTH, true);
		System.out.println("done dungeon generator");
	}

	/*private static class InnerDungeonGenerator {
	
		transient List<String> list = new ArrayList<>();
		transient Set<String> set = new HashSet<>();
		Set<Object2BooleanArrayMap<Direction>> directionVariants = new HashSet<>();
		Object2BooleanArrayMap<Direction> map = new Object2BooleanArrayMap<>();

		public InnerDungeonGenerator() {
			list.add("null");
			list.add("1");

			set.addAll(list);

			Object2BooleanArrayMap<Direction> map1 = new Object2BooleanArrayMap<>();
			map1.put(Direction.DOWN, false);
			map1.put(null, true);
			map1.put(Direction.UP, true);
			
			directionVariants.add(null);
			directionVariants.add(map1);

			this.map.putAll(map1);

			System.out.println(JsonUtils.toJson(this));
			System.out.println(JsonUtils.getFancyRegistry().getSerializer(map1.getClass()));
		}
	}*/
}
