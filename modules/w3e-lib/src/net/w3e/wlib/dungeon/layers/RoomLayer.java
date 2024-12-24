package net.w3e.wlib.dungeon.layers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;
import net.skds.lib2.mat.Direction.Axis;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.json.ILayerDeserializerAdapter;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.wlib.log.LogUtil;

public class RoomLayer<T> extends ListLayer<RoomLayer.RoomPoint<T>> implements ISetupLayer {

	public static final String TYPE = "room";

	public static final String KEY = "room";

	private final int softChance;
	private final List<RoomVariant<T>> rooms = new ArrayList<>();
	private final transient List<RoomVariant<T>> workList = new ArrayList<>();
	private final transient List<RoomSoftData<T>> softList = new ArrayList<>();
	private transient Progress progress;

	@SafeVarargs
	public RoomLayer(DungeonGenerator generator, int softChance, RoomVariant<T>... rooms) {
		this(generator, softChance, Arrays.asList(rooms));
	}

	public RoomLayer(DungeonGenerator generator, int softChance, Collection<RoomVariant<T>> rooms) {
		super(generator);
		this.softChance = softChance;
		this.rooms.addAll(rooms);
		this.rooms.removeIf(RoomVariant::notValid);
	}

	@Override
	protected String keyName() {
		return TYPE;
	}

	@Override
	public final RoomLayer<T> withDungeon(DungeonGenerator generator) {
		return new RoomLayer<>(generator, this.softChance, this.rooms);
	}

	@Override
	public final void setup(DungeonRoomInfo room) {
		room.data().put(KEY, null);
	}

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		this.list.clear();
		this.filled = -1;

		this.progress = Progress.createArray;

		this.workList.clear();
		this.rooms.stream().map(RoomVariant::copy).forEach(this.workList::add);
		if (this.workList.isEmpty()) {
			throw new DungeonException(LogUtil.IS_EMPTY.createMsg("Room layer"));
		}
		this.softList.clear();
	}

	@Override
	public final int generate() throws DungeonException {
		Progress prevProgress = this.progress;
		float i = 100;

		switch (this.progress) {
			case createArray -> {
				this.forEach(room -> {
					if (room.exists() && !room.isWall()) {
						this.list.add(new RoomPoint<>(room.room()));
					}
				});
				this.filled = this.list.size();
			}
			case fillRooms -> {
				for (RoomPoint<T> point : this.list) {
					for (RoomVariant<T> info : this.workList) {
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
				Iterator<RoomPoint<T>> iterator = this.list.iterator();
				boolean remove = false;
				while (iterator.hasNext()) {
					RoomPoint<T> point = iterator.next();
					if (point.variants.size() == 1) {
						RoomVariant<T> variant = point.variants.getFirst();
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
				List<RoomPoint<T>> limited = new ArrayList<>(this.list.size());
				for (RoomPoint<T> point : this.list) {
					if (point.variants.stream().anyMatch(e -> !e.isUnlimitedCount())) {
						limited.add(point);
					}
				}
				if (!limited.isEmpty()) {
					boolean remove = false;
					for (int j = 0; j < 10 && !limited.isEmpty(); j++) {
						RoomPoint<T> point = limited.remove(random().nextInt(limited.size()));
						if (point.initRoom(this)) {
							remove = true;
						}
						this.list.remove(point);
					}
					if (remove) {
						this.removeLimitReachedFromVariants();
					}
					float size = this.filled;
					i = (size - limited.size()) * 100 / size;
					if (remove) {
						i = Math.min(0, i - 1);
					}
				}
			}
			case fillNormalRooms -> {
				for (RoomPoint<T> point : this.list) {
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
					Collections.shuffle(this.softList);
					Map<Vec3I, Direction> poses = new HashMap<>(6);

					while (!this.softList.isEmpty()) {
						RoomSoftData<T> first = this.softList.removeFirst();
						poses.clear();
						for (Entry<Direction> entry : first.data.variant.object2BooleanEntrySet()) {
							if (!entry.getBooleanValue()) {
								Direction key = entry.getKey();
								poses.put(first.room.pos().addI(key), key);
							}
						}
						for (RoomSoftData<T> other : this.softList) {
							Direction direction = poses.get(other.room.pos());
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

	private final void throwRoomIsEmtpy(RoomPoint<T> point) throws DungeonException {
		throw new DungeonException(String.format("Cannot find room with existed params\n%s", point.room));
	}

	private final void removeLimitReachedFromVariants() throws DungeonException {
		for (RoomPoint<T> point : this.list) {
			if (point.variants.removeIf(IDungeonLimitedCount::isLimitReachedCount)) {
				if (point.variants.isEmpty()) {
					this.throwRoomIsEmtpy(point);
				}
			}
		}
	}

	private final void saveIfhasSoftConnections(DungeonRoomInfo room, RoomData<T> data) {
		boolean found = false;
		for (Entry<Direction> entry : data.variant.object2BooleanEntrySet()) {
			if (!entry.getBooleanValue()) {
				Direction direction = entry.getKey();
				if (room.isConnect(direction, false)) {
					DungeonRoomCreateInfo target = this.get(room.pos().addI(direction));
					if (target.exists()) {
						DungeonRoomInfo targetRoom = target.room();
						if (!targetRoom.isWall()) {
							Direction opposite = direction.getOpposite();
							if (!targetRoom.isConnect(opposite, true) && targetRoom.isConnect(opposite, false)) {
								found = true;
							}
						}
					}
					continue;
				}
			}
		}
		if (found) {
			this.softList.add(new RoomSoftData<>(room, data));
		}
	}

	@Override
	public final void rotate(Direction rotation, DungeonRoomInfo room, Map<Direction, Direction> wrapRotation) throws DungeonException {
		RoomData<T> data = room.data().getT(KEY);
		if (data != null) {
			Object2BooleanArrayMap<Direction> variant = new Object2BooleanArrayMap<>(data.variant);
			List<Direction> soft = new ArrayList<>(data.soft);
			data = new RoomData<>(data.value, new Object2BooleanArrayMap<>());
			for (Entry<Direction> entry : variant.object2BooleanEntrySet()) {
				Direction key = entry.getKey();
				if (key.isHorizontal()) {
					key = wrapRotation.get(key);
				}
				data.variant.put(key, entry.getBooleanValue());
			}
			for (Direction s : soft) {
				if (s.isHorizontal()) {
					s = wrapRotation.get(s);
				}
				data.soft.add(s);
			}
			room.data().put(KEY, data);
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

	public record RoomPoint<T>(DungeonRoomInfo room, List<RoomVariant<T>> variants) {
		public RoomPoint(DungeonRoomInfo room) {
			this(room, new ArrayList<>());
		}

		public final boolean initRoom(RoomLayer<T> layer) {
			RoomVariant<T> variant;
			if (this.variants.size() == 1) {
				variant = this.variants.removeFirst();
			} else {
				variant = this.variants.get(layer.random().nextInt(this.variants.size()));
				this.variants.clear();
			}
			variant.substractCount();

			List<Object2BooleanArrayMap<Direction>> directions = new ArrayList<>();

			map_block: for (Object2BooleanArrayMap<Direction> map : variant.directionVariants) {
				for (Entry<Direction> entry : map.object2BooleanEntrySet()) {
					if (entry.getBooleanValue()) {
						if (!this.room.isConnect(entry.getKey(), true)) {
							continue map_block;
						}
					}
				}
				directions.add(map);
			}

			RoomData<T> data = new RoomData<T>(variant.value, directions.get(layer.random().nextInt(directions.size())));

			layer.saveIfhasSoftConnections(this.room, data);
			this.room.data().put(KEY, data);

			return variant.isLimitReachedCount();
		}
	}

	public record RoomVariant<T>(Set<Object2BooleanArrayMap<Direction>> directionVariants, BaseLayerRoomRange layerRange, boolean enterance, T value, DungeonInfoCountHolder count) implements IDungeonLimitedCount {

		public RoomVariant(Set<Object2BooleanArrayMap<Direction>> directionVariants, BaseLayerRoomRange layerRange, boolean enterance, T value, int count) {
			this(directionVariants, layerRange, enterance, value, new DungeonInfoCountHolder(count));
		}

		public RoomVariant(Object2BooleanArrayMap<Direction> directionVariants, BaseLayerRoomRange layerRange, boolean enterance, T value, int count) {
			this(new LinkedHashSet<>(), layerRange, enterance, value, count);
			if (!directionVariants.isEmpty()) {
				Object2BooleanArrayMap<Direction> baseVariants = new Object2BooleanArrayMap<>();
				if (directionVariants.containsKey(Direction.UP)) {
					baseVariants.put(Direction.UP, directionVariants.removeBoolean(Direction.UP));
				}
				if (directionVariants.containsKey(Direction.DOWN)) {
					baseVariants.put(Direction.DOWN, directionVariants.removeBoolean(Direction.DOWN));
				}
				if (!directionVariants.isEmpty()) {
					for (int i = 0; i < 4; i++) {
						Object2BooleanArrayMap<Direction> directions = new Object2BooleanArrayMap<>(baseVariants);
						for (Entry<Direction> entry : directionVariants.object2BooleanEntrySet()) {
							directions.put(entry.getKey().rotateClockwise(Axis.Y), entry.getBooleanValue());
						}
						directionVariants = directions;
						this.directionVariants.add(directions);
					}
				}
			}
		}

		public final boolean notValid() {
			return this.directionVariants.isEmpty() || this.layerRange.notValid();
		}

		public final boolean test(RoomLayer<T> layer, DungeonRoomInfo room) {
			if (this.enterance == room.isEnterance() && layer.getRoomValues(room).test(layer.random(), this.layerRange)) {
				if (this.directionVariants.iterator().next().values().stream().filter(e -> e).count() != room.connectCount(true)) {
					return false;
				}
				block_a: for (Object2BooleanArrayMap<Direction> directions : this.directionVariants) {
					for (Entry<Direction> entry : directions.object2BooleanEntrySet()) {
						if (entry.getBooleanValue()) {
							if (!room.isConnect(entry.getKey(), true)) {
								continue block_a;
							}
						}
					}
					return true;
				}
			}
			return false;
		}

		public final RoomVariant<T> copy() {
			return new RoomVariant<T>(this.directionVariants, this.layerRange, this.enterance, this.value, this.count.copy());
		}

		@Override
		public final String toString() {
			StringBuilder builder = new StringBuilder("{");
			builder.append(String.format("directions:%s", this.directionVariants.stream().map(m -> m.object2BooleanEntrySet().stream().map(RoomLayer::directionToString).toList()).toList()));
			if (!this.layerRange.isNull()) {
				builder.append(String.format(",baseLayerRange:%s", this.layerRange));
			}
			if (this.enterance) {
				builder.append(",enterance");
			}
			builder.append(String.format(",value:%s", this.value));
			if (!this.isUnlimitedCount()) {
				builder.append(String.format(",count:%s", this.count.getValue()));
			}
			builder.append("}");

			return builder.toString();
		}
	}

	public record RoomData<T>(T value, Object2BooleanArrayMap<Direction> variant, List<Direction> soft) {
		public RoomData(T value, Object2BooleanArrayMap<Direction> variant) {
			this(value, variant, new ArrayList<>());
		}

		@Override
		public final String toString() {
			return String.format("{value:%s,variant:%s,soft:%s}", this.value, this.variant.object2BooleanEntrySet().stream().map(RoomLayer::directionToString).toList(), this.soft.stream().map(e -> e.name().substring(0, 1)).toList());
		}
	}

	private record RoomSoftData<T>(DungeonRoomInfo room, RoomData<T> data) {}

	private static final String directionToString(Entry<Direction> e) {
		String name = e.getKey().name().substring(0, 1);
		if (!e.getBooleanValue()) {
			name = name.toLowerCase();
		}
		return name;
	}

	@AllArgsConstructor
	public abstract static class RoomLayerAdapter<D> implements ILayerDeserializerAdapter<RoomLayerData<D>, RoomLayer<D>> {

		private final Class<? extends RoomLayerData<D>> dataClass;
		
		@SuppressWarnings("unchecked")
		@Override
		public final RoomLayer<D> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			RoomLayerData<D> data = context.deserialize(json, this.dataClass);
			this.isEmpty("room variants", data.rooms);
			this.lessThan("softChance", data.softChance, -1);
			data = deserialize(data, context);
			data.roomsVariants = Stream.of(data.rooms).map(e -> new RoomVariant<>(e.connections.map(), e.getLayerRange(), e.enterance, e.getValue(), e.count)).toArray(RoomVariant[]::new);
			return data.withDungeon(null);
		}
	}

	public abstract static class RoomLayerData<T> implements ILayerData<RoomLayer<T>> {
		private int softChance = 0;

		private RoomVariantData<T>[] rooms;

		private transient RoomVariant<T>[] roomsVariants;

		@Override
		public final RoomLayer<T> withDungeon(DungeonGenerator generator) {
			return new RoomLayer<T>(generator, this.softChance, this.roomsVariants);
		}
	}

	@AllArgsConstructor
	public static class RoomVariantAdapter<D> implements ILayerDeserializerAdapter<RoomVariantData<D>, RoomVariantData<D>> {

		private final Class<? extends RoomVariantData<D>> dataClass;

		@Override
		public final RoomVariantData<D> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			RoomVariantData<D> data = context.deserialize(json, this.dataClass);
			BaseLayerRoomRange layerRange = data.getLayerRange();
			this.nonNull("layerRange", layerRange);
			if (layerRange.notValid()) {
				throw new IllegalStateException(LogUtil.ILLEGAL.createMsg("layerRange"));
			}
			if (data.count != -1) {
				this.lessThan("count", data.count);
			}
			D value = data.getValue();
			this.nonNull("value", value);
			this.nonNull("conntections", data.connections);
			Object2BooleanArrayMap<Direction> map = data.connections.map();
			if (map.isEmpty()) {
				this.isEmpty("connections");
			}
			return deserialize(data, context);
		}
	}

	public abstract static class RoomVariantData<T> {

		public ConnectionsData connections = new ConnectionsData();
		public boolean enterance = false;
		public int count = -1;

		protected abstract T getValue();
		protected abstract BaseLayerRoomRange getLayerRange();
	}

	@Getter
	@NoArgsConstructor
	public static class ConnectionsData {
		private Boolean north = null;
		private Boolean south = null;
		private Boolean west = null;
		private Boolean east = null;
		private Boolean up = null;
		private Boolean down = null;

		public ConnectionsData(Object2BooleanArrayMap<Direction> map) {
			this.north = this.get(map, Direction.NORTH);
			this.south = this.get(map, Direction.SOUTH);
			this.west = this.get(map, Direction.WEST);
			this.east = this.get(map, Direction.EAST);
			this.up = this.get(map, Direction.UP);
			this.down = this.get(map, Direction.DOWN);
		}

		private final Boolean get(Object2BooleanArrayMap<Direction> map, Direction direction) {
			if (map.containsKey(direction)) {
				return map.getBoolean(direction);
			} else {
				return null;
			}
		}

		public ConnectionsData(DungeonRoomInfo room) {
			this.north = this.get(room, Direction.NORTH);
			this.south = this.get(room, Direction.SOUTH);
			this.west = this.get(room, Direction.WEST);
			this.east = this.get(room, Direction.EAST);
			this.up = this.get(room, Direction.UP);
			this.down = this.get(room, Direction.DOWN);
		}

		private final Boolean get(DungeonRoomInfo room, Direction direction) {
			if (room.isConnect(direction, true)) {
				return true;
			} else if (room.isConnect(direction, false)) {
				return false;
			} else {
				return null;
			}
		}

		public final Object2BooleanArrayMap<Direction> map() {
			Object2BooleanArrayMap<Direction> map = new Object2BooleanArrayMap<>();
			this.put(map, Direction.NORTH, this.north);
			this.put(map, Direction.SOUTH, this.south);
			this.put(map, Direction.WEST, this.west);
			this.put(map, Direction.EAST, this.east);
			this.put(map, Direction.UP, this.up);
			this.put(map, Direction.DOWN, this.down);
			return map;
		}

		private final void put(Object2BooleanArrayMap<Direction> map, Direction direction, Boolean bl) {
			if (bl != null) {
				map.put(direction, bl.booleanValue());
			}
		}
	}

	public static final RoomLayer<String> example(DungeonGenerator generator) {
		Random random = new Random(0);
		List<RoomVariant<String>> rooms = new ArrayList<>();
		List<Direction> dir = new ArrayList<>();
		BaseLayerRoomRange bossBase = new BaseLayerRoomRange(null, null, null, new LayerRange(5, Integer.MAX_VALUE), null);
		for (Direction direction : Direction.values()) {
			if (direction != Direction.UP && direction != Direction.DOWN) {
				dir.add(direction);
			}
		}
		for (int i = 0; i < 150; i++) {
			Object2BooleanArrayMap<Direction> directions = new Object2BooleanArrayMap<>();
			{
				int count = random.nextInt(4) + 1;
				if (count == 4) {
					for (Direction wDirection : dir) {
						directions.put(wDirection, true);
					}
				} else {
					List<Direction> dirCopy = new ArrayList<>(dir);
					while (count > 0) {
						directions.put(dirCopy.remove(random.nextInt(dirCopy.size())), random.nextInt(100) + 1 <= 75);
						count--;
					}
				}
			}
			BaseLayerRoomRange baseLayerRange = BaseLayerRoomRange.NULL;
			int count;
			String name = String.valueOf(i + 1);
			if (random.nextInt(100) + 1 <= 5) {
				name += "_boss";
				count = 1;
				baseLayerRange = bossBase;
			} else {
				count = random.nextInt(100) + 1 <= 20 ? random.nextInt(5) + 1 : -1;
			}

			rooms.add(new RoomVariant<String>(directions, baseLayerRange, false, name, count));
		}

		boolean print = false;
		if (print) {
			DungeonGenerator.LOGGER.debug("===============");
			for (RoomVariant<String> info : rooms) {
				DungeonGenerator.LOGGER.debug(info);
			}
			DungeonGenerator.LOGGER.debug("");
		}

		Set<Set<Direction>> filter = new HashSet<>();

		for (Direction d1 : dir) {
			Set<Direction> s1 = new HashSet<>();
			s1.add(d1);
			if (filter.add(s1)) {
				rooms.add(exampleEnterance(s1));
				for (Direction d2 : dir) {
					Set<Direction> s2 = new HashSet<>(s1);
					s2.add(d2);
					if (filter.add(s2)) {
						rooms.add(exampleEnterance(s2));
						for (Direction d3 : dir) {
							Set<Direction> s3 = new HashSet<>(s2);
							s3.add(d3);
							if (filter.add(s3)) {
								rooms.add(exampleEnterance(s3));
								for (Direction d4 : dir) {
									Set<Direction> s4 = new HashSet<>(s3);
									s4.add(d4);
									if (filter.add(s4)) {
										rooms.add(exampleEnterance(s4));
									}
								}
							}
						}
					}
				}
			}
		}

		if (print) {
			for (int i = 1; i <= 4; i++) {
				StringBuilder builder = new StringBuilder();
				for (Set<Direction> set : filter) {
					if (set.size() == i) {
						builder.append(set);
					}
				}
				DungeonGenerator.LOGGER.debug(builder);
			}
			DungeonGenerator.LOGGER.debug("===============");
		}

		return new RoomLayer<>(generator, 50, rooms);
	}

	private static final RoomVariant<String> exampleEnterance(Set<Direction> direction) {
		Object2BooleanArrayMap<Direction> directions = new Object2BooleanArrayMap<>();
		for (Direction d : direction) {
			directions.put(d, true);
		}
		return new RoomVariant<String>(directions, BaseLayerRoomRange.NULL, true, "center", -1);
	}

}
