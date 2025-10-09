package net.w3e.wlib.dungeon.layers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import lombok.ToString;
import net.skds.lib2.io.codec.BuiltinCodecFactory;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.SerializeOnlyCodec;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.SerializationAlias;
import net.skds.lib2.io.codec.annotation.SkipSerialization;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.mat.vec3.Direction.Axis;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.direction.ConnectionState;
import net.w3e.wlib.dungeon.filters.RoomFilters;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonNamed;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.registry.DungeonRegistryObject;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.json.WJsonBuilder;
import net.w3e.wlib.log.LogUtil;

@DefaultCodec(RoomLayer.RoomLayerJsonAdapter.class)
public class RoomLayer extends ListRegistryLayer<RoomLayer.RoomPoint, RoomLayer.RoomVariant> implements ISetupRoomLayer {

	public static final String TYPE = "room";

	public static final String KEY = "room";

	private final int softChance;

	private final transient List<RoomSoftData> softList = new ArrayList<>();
	private transient Progress progress = Progress.createArray;

	/**
	 * for json
	 */
	@SuppressWarnings("all")
	@DefaultCodec(RoomLayer.ValuesCodec.class)
	private final List<DungeonRegistryObject> values;
	
	public RoomLayer(DungeonGenerator generator, Collection<DungeonRegistryObject> values, int softChance) {
		super(JSON_MAP.ROOM, generator, values);
		this.softChance = softChance;
		this.values = super.registryList;
	}

	@Override
	protected final RoomVariant getData(DungeonRegistryContext registryContext, DungeonKeySupplier keyName) {
		return registryContext.getTyped(keyName, RoomVariant.class);
	}

	@Override
	public final RoomLayer createGenerator(DungeonGenerator generator) {
		return new RoomLayer(generator, this.registryList, this.softChance);
	}

	@Override
	public final void setupRoom(DungeonRoomInfo room) {
		room.getData().put(KEY, null);
	}

	@Override
	public final float generate() throws DungeonException {
		Progress prevProgress = this.progress;
		float i = 1;

		switch (this.progress) {
			case createArray -> {
				this.forEach(room -> {
					if (room.exists() && !room.isWall()) {
						this.list.add(new RoomPoint(room.room()));
					}
				});
				this.filled = this.list.size();
			}
			case fillRooms -> {
				for (RoomPoint point : this.list) {
					for (RoomVariant info : this.workDataList) {
						if (info.test(this, point.room)) {
							point.variants.add(info);
						}
					}
					if (point.variants.isEmpty()) {
						this.throwRoomIsEmtpy(point);
					}
				}
			}
			case initDone -> {
				Collections.shuffle(this.list, this.random());
				Iterator<RoomPoint> iterator = this.list.iterator();
				boolean remove = false;
				while (iterator.hasNext()) {
					RoomPoint point = iterator.next();
					if (point.variants.size() == 1) {
						RoomVariant variant = point.variants.getFirst();
						boolean done = variant.isUnlimitedCount();
						if (!done) {
							done = variant.substractCount();
							remove = done || remove;
						}
						if (done) {
							point.initRoom(this);
							iterator.remove();
						} else {
							throw new DungeonException("Limit count of room is reached");
						}
					}
				}
				if (remove) {
					this.removeLimitReachedFromVariants();
				}
			}
			case initLimited -> {
				List<RoomPoint> limited = new ArrayList<>(this.list.size());
				for (RoomPoint point : this.list) {
					if (point.variants.stream().anyMatch(e -> !e.isUnlimitedCount())) {
						limited.add(point);
					}
				}
				if (!limited.isEmpty()) {
					boolean remove = false;
					for (int j = 0; j < 10 && !limited.isEmpty(); j++) {
						RoomPoint point = limited.remove(random().nextInt(limited.size()));
						if (point.initRoom(this)) {
							remove = true;
						}
						this.list.remove(point);
					}
					if (remove) {
						this.removeLimitReachedFromVariants();
					}
					float size = this.filled;
					i = (size - limited.size()) * 1f / size;
					if (remove) {
						i = Math.min(0, i - 1);
					}
				}
			}
			case fillNormalRooms -> {
				for (RoomPoint point : this.list) {
					point.initRoom(this);
				}
				if (this.list.isEmpty()) {
					this.filled = this.softList.size();
					if (this.filled == 0) {
						//return 100;
					}
				}
			}
			case postInit -> {
				if (!this.softList.isEmpty()) {
					Collections.shuffle(this.softList, random());
					Map<Vec3I, Direction> poses = new HashMap<>(6);

					while (!this.softList.isEmpty()) {
						RoomSoftData first = this.softList.removeFirst();
						poses.clear();
						for (Direction direction : first.data.soft) {
							if (first.data.hard.contains(direction)) {
								continue;
							}
							poses.put(first.room.getPos().addI(direction), direction);
						}
						for (RoomSoftData other : this.softList) {
							Direction direction = poses.get(other.room.getPos());
							if (direction != null) {
								Direction opposite = direction.getOpposite();
								boolean soft = other.data.soft.contains(opposite);
								if (soft && this.random100() <= this.softChance) {
									first.data.soft.add(direction);
									other.data.soft.add(direction.getOpposite());
								}
							}
						}
					}
				}
			}
		}
		this.progress = this.progress.next(i);

		return this.progress.progress(prevProgress, i);
	}

	private final void throwRoomIsEmtpy(RoomPoint point) throws DungeonException {
		throw new DungeonException(String.format("Cannot find room with existed params\n%s", point.room));
	}

	private final void removeLimitReachedFromVariants() throws DungeonException {
		for (RoomPoint point : this.list) {
			if (point.variants.removeIf(IDungeonLimitedCount::isLimitReachedCount)) {
				if (point.variants.isEmpty()) {
					this.throwRoomIsEmtpy(point);
				}
			}
		}
	}

	@Deprecated
	private final void saveIfHasSoftConnections(DungeonRoomInfo room, RoomData data) {
		boolean found = false;
		for (Direction direction : data.soft) {
			if (data.hard.contains(direction)) {
				continue;
			}
			if (room.isSoftConnect(direction)) {
				DungeonRoomCreateInfo target = this.get(room.getPos().addI(direction));
				if (target.exists()) {
					DungeonRoomInfo targetRoom = target.room();
					if (!targetRoom.isWall()) {
						Direction opposite = direction.getOpposite();
						if (!targetRoom.isHardConnect(opposite) && targetRoom.isSoftConnect(opposite)) {
							found = true;
						}
					}
				}
				continue;
			}
		}
		if (found) {
			this.softList.add(new RoomSoftData(room, data));
		}
	}

	@Override
	public final void rotate(Direction rotation, DungeonRoomInfo room, Map<Direction, Direction> wrapRotation) throws DungeonException {
		RoomData data = room.getData().getT(KEY);
		if (data != null) {
			data = new RoomData(data.value);

			for (Direction direction : data.hard) {
				if (direction.isHorizontal()) {
					direction = wrapRotation.get(direction);
				}
				data.hard.add(direction);
			}

			for (Direction direction : data.soft) {
				if (direction.isHorizontal()) {
					direction = wrapRotation.get(direction);
				}
				data.soft.add(direction);
			}

			room.getData().put(KEY, data);
		}
	}

	private enum Progress implements IDungeonLayerProgress<Progress> {
		createArray,
		fillRooms,
		initDone,
		initLimited,
		fillNormalRooms,
		postInit
		;

		@Override
		public final Progress[] getValues() {
			return Progress.values();
		}
	}

	public record RoomPoint(DungeonRoomInfo room, List<RoomVariant> variants) {
		public RoomPoint(DungeonRoomInfo room) {
			this(room, new ArrayList<>());
		}

		public final boolean initRoom(RoomLayer layer) {
			RoomVariant variant;
			if (this.variants.size() == 1) {
				variant = this.variants.removeFirst();
			} else {
				variant = this.variants.get(layer.random().nextInt(this.variants.size()));
				this.variants.clear();
			}
			variant.substractCount();

			List<Map<Direction, ConnectionState>> directions = new ArrayList<>();

			map_block:
			for (Map<Direction, ConnectionState> map : variant.directionVariants) {
				for (Map.Entry<Direction, ConnectionState> entry : map.entrySet()) {
					if (entry.getValue().isHard()) {
						if (!this.room.isHardConnect(entry.getKey())) {
							continue map_block;
						}
					}
				}
				directions.add(map);
			}

			RoomData data = new RoomData(variant.keyName(), directions.get(layer.random().nextInt(directions.size())));

			layer.saveIfHasSoftConnections(this.room, data);
			this.room.getData().put(KEY, data);

			return variant.isLimitReachedCount();
		}
	}

	@DefaultCodec(RoomLayer.RoomVariantJsonAdapter.class)
	public record RoomVariant(
		DungeonKeySupplier keyName,
		@DefaultCodec(RoomVariantFieldJsonAdapter.class) @SerializationAlias("connections") Set<LinkedHashMap<Direction, ConnectionState>> directionVariants,
		@SkipSerialization(predicate = RoomFilters.RoomFiltersNullPredicate.class) RoomFilters filter,
		@SkipSerialization boolean entrance, 
		@SkipSerialization(predicate = DungeonInfoCountHolder.DungeonInfoCountHolderNullPredicate.class) DungeonInfoCountHolder count
	) implements IDungeonLimitedCount, IDungeonNamed {

		public RoomVariant(DungeonKeySupplier keyName, LinkedHashMap<Direction, ConnectionState> directionVariants, RoomFilters layerRange, boolean entrance, DungeonInfoCountHolder count) {
			this(keyName, new LinkedHashSet<>(), layerRange, entrance, count);
			if (!directionVariants.isEmpty()) {
				this.directionVariants.add(new LinkedHashMap<>(directionVariants));
				LinkedHashMap<Direction, ConnectionState> baseVariants = new LinkedHashMap<>();
				if (directionVariants.containsKey(Direction.UP)) {
					baseVariants.put(Direction.UP, directionVariants.remove(Direction.UP));
				}
				if (directionVariants.containsKey(Direction.DOWN)) {
					baseVariants.put(Direction.DOWN, directionVariants.remove(Direction.DOWN));
				}
				if (!directionVariants.isEmpty()) {
					for (int i = 0; i < 3; i++) {
						LinkedHashMap<Direction, ConnectionState> directions = new LinkedHashMap<>(baseVariants);
						for (Map.Entry<Direction, ConnectionState> entry : directionVariants.entrySet()) {
							directions.put(entry.getKey().rotateClockwise(Axis.Y), entry.getValue());
						}
						directionVariants = directions;
						this.directionVariants.add(directions);
					}
				}
			}
		}

		public boolean notValid() {
			return this.directionVariants.isEmpty() || this.filter.notValid();
		}

		public boolean test(RoomLayer layer, DungeonRoomInfo room) {
			if (this.entrance == room.isEntrance() && this.filter.test(layer.random(), room)) {
				if (this.directionVariants.iterator().next().values().stream().filter(ConnectionState::isHard).count() != room.connectHardCount()) {
					return false;
				}
				block_a: for (Map<Direction, ConnectionState> directions : this.directionVariants) {
					for (Map.Entry<Direction, ConnectionState> entry : directions.entrySet()) {
						if (entry.getValue().isHard()) {
							if (!room.isHardConnect(entry.getKey())) {
								continue block_a;
							}
						}
					}
					return true;
				}
			}
			return false;
		}

		@Override
		public RoomVariant clone() {
			return new RoomVariant(this.keyName(), this.directionVariants, this.filter, this.entrance, this.count.copy());
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("{");
			builder.append(String.format("directions:%s", this.directionVariants.stream().map(m -> m.entrySet().stream().map(RoomLayer::directionToString).toList()).toList()));
			if (!this.filter.isEmpty()) {
				builder.append(String.format(",filter:%s", this.filter));
			}
			if (this.entrance) {
				builder.append(",entrance");
			}
			builder.append(String.format(",keyName:%s", this.keyName().getRaw()));
			if (!this.isUnlimitedCount()) {
				builder.append(String.format(",count:%s", this.count.getValue()));
			}
			builder.append("}");

			return builder.toString();
		}
	}

	public record RoomData(DungeonKeySupplier value, Set<Direction> hard, Set<Direction> soft) {

		public RoomData(DungeonKeySupplier value) {
			this(value, EnumSet.noneOf(Direction.class), EnumSet.noneOf(Direction.class));
		}
		@Deprecated
		public RoomData(DungeonKeySupplier value, Map<Direction, ConnectionState> variant) {
			this(value, variant.entrySet().stream().filter(e -> e.getValue().isHard()).map(Map.Entry::getKey).collect(Collectors.toSet()), variant.entrySet().stream().filter(e -> e.getValue().isSoft()).map(Map.Entry::getKey).collect(Collectors.toSet()));
		}

		@Override
		public String toString() {
			return String.format("{value:%s,hard:%s,soft:%s}", this.value.getRaw(), this.hard, this.soft);
		}
	}

	private record RoomSoftData(DungeonRoomInfo room, RoomData data) {}

	private static String directionToString(Map.Entry<Direction, ConnectionState> e) {
		String name = e.getKey().name().substring(0, 1);
		if (e.getValue().isBoth()) {
			name = name + "|" + name.toLowerCase();
		} else if (e.getValue().isSoft()) {
			name = name.toLowerCase();
		}
		return name;
	}

	static class RoomLayerJsonAdapter extends ReflectiveBuilderCodec<RoomLayer> {

		public RoomLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, RoomLayerData.class, registry);
		}

		private static class RoomLayerData extends RegistryLayerDataBuilder<RoomLayer, RoomLayer.RoomVariant> {

			@SuppressWarnings("all")
			private int softChance = 0;

			@Override
			public RoomLayer build() {
				this.lessThan(this.softChance, -1, "softChance");
				return new RoomLayer(null, this.values, this.softChance);
			}
		}

	}

	static class RoomVariantJsonAdapter extends ReflectiveBuilderCodec<RoomVariant> {
	
		public RoomVariantJsonAdapter(Type type, CodecRegistry registry) {
			super(type, RoomVariantJsonData.class, registry);
		}

		private static class RoomVariantJsonData implements WJsonBuilder<RoomVariant> {
			public DungeonKeySupplier keyName;
			public ConnectionsData connections = new ConnectionsData();
			public boolean entrance = false;
			public DungeonInfoCountHolder count = DungeonInfoCountHolder.NULL;

			public RoomFilters filter = RoomFilters.NULL;

			@Override
			public final RoomVariant build() {
				this.nonNull(this.keyName, "keyName");

				this.nonNull(this.filter, "filter");
				if (filter.notValid()) {
					throw new IllegalStateException(LogUtil.ILLEGAL.createMsg("filter"));
				}
				this.nonNull(this.count, "count");
				if (this.count.getValue() > -1) {
					this.lessThan(this.count.getValue(), "count");
				}

				this.nonNull(this.connections, "connections");
				LinkedHashMap<Direction, ConnectionState> map = this.connections.map();
				if (map.isEmpty()) {
					this.isEmpty("connections");
				}

				return new RoomVariant(this.keyName, map, this.filter, this.entrance, this.count);
			}
		}
	}

	static class RoomVariantFieldJsonAdapter extends SerializeOnlyCodec<Set<LinkedHashMap<Direction, ConnectionState>>> {
		public RoomVariantFieldJsonAdapter(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Set<LinkedHashMap<Direction, ConnectionState>> value, UniversalWriter writer) throws IOException {
			LinkedHashMap<Direction, ConnectionState> data;
			if (value.isEmpty()) {
				data = new LinkedHashMap<>();
			} else {
				data = value.iterator().next();
			}
			if (!data.isEmpty()) {
				writer.beginObject();
				for (Map.Entry<Direction, ConnectionState> entry : data.entrySet()) {
					writer.writeName(entry.getKey().getName());
					writer.writeString(entry.getValue().name());
				}
				writer.endObject();
			} else {
				writer.writeNull();
			}
		}
	}

	@ToString
	@NoArgsConstructor
	public static class ConnectionsData {
		private ConnectionState north = null;
		private ConnectionState south = null;
		private ConnectionState west = null;
		private ConnectionState east = null;
		private ConnectionState up = null;
		private ConnectionState down = null;

		public ConnectionsData(DungeonRoomInfo room) {
			this.north = getState(room, Direction.NORTH);
			this.south = getState(room, Direction.SOUTH);
			this.west = getState(room, Direction.WEST);
			this.east = getState(room, Direction.EAST);
			this.up = getState(room, Direction.UP);
			this.down = getState(room, Direction.DOWN);
		}

		private static ConnectionState getState(DungeonRoomInfo room, Direction direction) {
			boolean hard = room.isHardConnect(direction);
			boolean soft = room.isSoftConnect(direction);
			if (hard && soft) {
				return ConnectionState.BOTH;
			}
			if (hard) {
				return ConnectionState.HARD;
			}
			if (soft) {
				return ConnectionState.SOFT;
			}
			return null;
		}

		public boolean isEmpty() {
			return this.north == null && this.south == null && this.west == null && this.east == null && this.up == null && this.down == null;
		}

		public final LinkedHashMap<Direction, ConnectionState> map() {
			LinkedHashMap<Direction, ConnectionState> map = new LinkedHashMap<>();
			this.put(map, Direction.NORTH, this.north);
			this.put(map, Direction.SOUTH, this.south);
			this.put(map, Direction.WEST, this.west);
			this.put(map, Direction.EAST, this.east);
			this.put(map, Direction.UP, this.up);
			this.put(map, Direction.DOWN, this.down);
			return map;
		}

		private void put(Map<Direction, ConnectionState> map, Direction direction, ConnectionState state) {
			if (state != null) {
				map.put(direction, state);
			}
		}
	}

	static class ValuesCodec extends BuiltinCodecFactory.CollectionCodec {

		public ValuesCodec(Type tClass, CodecRegistry registry) {
			super(List.class, new DungeonRegistryObject.JCodec(RoomLayer.RoomVariant.class, registry), registry, ArrayList::new);
		}

	}

}
