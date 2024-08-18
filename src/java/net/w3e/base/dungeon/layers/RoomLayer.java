package net.w3e.base.dungeon.layers;

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

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry;
import lombok.NoArgsConstructor;
import net.w3e.base.dungeon.DungeonException;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.base.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;
import net.w3e.base.message.MessageUtil;

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
	public final RoomLayer<T> withDungeonImpl(DungeonGenerator generator) {
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
			throw new DungeonException(MessageUtil.IS_EMPTY.createMsg("Room layer"));
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
				}
			}
			case postInit -> {
				if (!this.softList.isEmpty()) {
					Collections.shuffle(this.softList);
					Map<WVector3I, WDirection> poses = new HashMap<>(6);

					while (!this.softList.isEmpty()) {
						RoomSoftData<T> first = this.softList.removeFirst();
						poses.clear();
						for (Entry<WDirection> entry : first.data.variant.object2BooleanEntrySet()) {
							if (!entry.getBooleanValue()) {
								WDirection key = entry.getKey();
								poses.put(first.room.pos().add(key.getRelative()), key);
							}
						}
						for (RoomSoftData<T> other : this.softList) {
							WDirection direction = poses.get(other.room.pos());
							if (direction != null) {
								WDirection opposite = direction.opposite();
								boolean soft = other.data.soft.contains(opposite);
								if (soft && this.random100() <= this.softChance) {
									first.data.soft.add(direction);
									other.data.soft.add(direction.opposite());
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
		for (Entry<WDirection> entry : data.variant.object2BooleanEntrySet()) {
			if (!entry.getBooleanValue()) {
				WDirection direction = entry.getKey();
				if (room.isConnect(direction, false)) {
					DungeonRoomCreateInfo target = this.get(room.pos().add(direction));
					if (target.exists()) {
						DungeonRoomInfo targetRoom = target.room();
						if (!targetRoom.isWall()) {
							WDirection opposite = direction.opposite();
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

			List<Object2BooleanArrayMap<WDirection>> directions = new ArrayList<>();

			for (Object2BooleanArrayMap<WDirection> map : variant.directionVariants) {
				for (Entry<WDirection> entry : map.object2BooleanEntrySet()) {
					if (entry.getBooleanValue()) {
						if (this.room.isConnect(entry.getKey(), true)) {
							directions.add(map);
							break;
						}
					}
				}
			}

			RoomData<T> data = new RoomData<T>(variant.value, directions.get(layer.random().nextInt(directions.size())));

			layer.saveIfhasSoftConnections(this.room, data);
			this.room.data().put(KEY, data);

			return variant.isLimitReachedCount();
		}
	}

	public record RoomVariant<T>(Set<Object2BooleanArrayMap<WDirection>> directionVariants, BaseLayerRoomRange layerRange, boolean enterance, T value, DungeonInfoCountHolder count) implements IDungeonLimitedCount {

		public RoomVariant(Set<Object2BooleanArrayMap<WDirection>> directionVariants, BaseLayerRoomRange layerRange, boolean enterance, T value, int count) {
			this(directionVariants, layerRange, enterance, value, new DungeonInfoCountHolder(count));
		}

		public RoomVariant(Object2BooleanArrayMap<WDirection> directionVariants, BaseLayerRoomRange layerRange, boolean enterance, T value, int count) {
			this(new LinkedHashSet<>(), layerRange, enterance, value, count);
			if (!directionVariants.isEmpty()) {
				Object2BooleanArrayMap<WDirection> baseVariants = new Object2BooleanArrayMap<>();
				if (directionVariants.containsKey(WDirection.UP)) {
					baseVariants.put(WDirection.UP, directionVariants.removeBoolean(WDirection.UP));
				}
				if (directionVariants.containsKey(WDirection.DOWN)) {
					baseVariants.put(WDirection.DOWN, directionVariants.removeBoolean(WDirection.DOWN));
				}
				if (!directionVariants.isEmpty()) {
					for (int i = 0; i < 4; i++) {
						Object2BooleanArrayMap<WDirection> directions = new Object2BooleanArrayMap<>(baseVariants);
						for (Entry<WDirection> entry : directionVariants.object2BooleanEntrySet()) {
							directions.put(entry.getKey().right(), entry.getBooleanValue());
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
				block_a: for (Object2BooleanArrayMap<WDirection> directions : this.directionVariants) {
					for (Entry<WDirection> entry : directions.object2BooleanEntrySet()) {
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
				builder.append(String.format(",count:%s", this.count.get()));
			}
			builder.append("}");

			return builder.toString();
		}
	}

	public record RoomData<T>(T value, Object2BooleanArrayMap<WDirection> variant, List<WDirection> soft) {
		public RoomData(T value, Object2BooleanArrayMap<WDirection> variant) {
			this(value, variant, new ArrayList<>());
		}

		@Override
		public final String toString() {
			return String.format("{value:%s,variant:%s,soft:%s}", this.value, this.variant.object2BooleanEntrySet().stream().map(RoomLayer::directionToString).toList(), this.soft.stream().map(e -> e.name().substring(0, 1)).toList());
		}
	}

	private record RoomSoftData<T>(DungeonRoomInfo room, RoomData<T> data) {}

	private static final String directionToString(Entry<WDirection> e) {
		String name = e.getKey().name().substring(0, 1);
		if (!e.getBooleanValue()) {
			name = name.toLowerCase();
		}
		return name;
	}

	public abstract static class RoomLayerData<T> implements ILayerAdapter<RoomLayer<T>> {
		private int softChance = 0;

		protected abstract RoomVariant<T>[] getRooms();

		@Override
		public final RoomLayer<T> withDungeon(DungeonGenerator generator) {
			RoomVariant<T>[] variants = this.getRooms();
			this.isEmpty("room variants", variants);
			this.lessThan("softChance", this.softChance, -1);

			return new RoomLayer<T>(generator, this.softChance, variants);
		}
	}

	public abstract static class RoomVariantData<T> implements ILayerAdapter<RoomVariant<T>> {

		private Connections connections = new Connections();
		private boolean enterance = false;
		private int count = -1;
	
		protected abstract T getValue();
		protected abstract BaseLayerRoomRange getLayerRange();

		@Override
		public final RoomVariant<T> withDungeon(DungeonGenerator generator) {
			BaseLayerRoomRange layerRange = this.getLayerRange();
			this.nonNull("layerRange", layerRange);
			if (layerRange.notValid()) {
				throw new IllegalStateException(MessageUtil.ILLEGAL.createMsg("layerRange"));
			}
				if (this.count != -1) {
				this.lessThan("count", this.count);
			}
			T value = this.getValue();
			this.nonNull("value", value);
			this.nonNull("conntections", this.connections);
			Object2BooleanArrayMap<WDirection> map = this.connections.map();
			if (map.isEmpty()) {
				this.isEmpty("connections");
			}
			return new RoomVariant<T>(map, layerRange, this.enterance, value, count);
		}

		@NoArgsConstructor
		public static class Connections {
			private Boolean north = null;
			private Boolean south = null;
			private Boolean west = null;
			private Boolean east = null;
			private Boolean up = null;
			private Boolean down = null;

			public Connections(Object2BooleanArrayMap<WDirection> map) {
				this.north = this.get(map, WDirection.NORTH);
				this.south = this.get(map, WDirection.SOUTH);
				this.west = this.get(map, WDirection.WEST);
				this.east = this.get(map, WDirection.EAST);
				this.up = this.get(map, WDirection.UP);
				this.down = this.get(map, WDirection.DOWN);
			}

			private final Boolean get(Object2BooleanArrayMap<WDirection> map, WDirection direction) {
				if (map.containsKey(direction)) {
					return map.getBoolean(direction);
				} else {
					return null;
				}
			}

			public final Object2BooleanArrayMap<WDirection> map() {
				Object2BooleanArrayMap<WDirection> map = new Object2BooleanArrayMap<>();
				this.put(map, WDirection.NORTH, this.north);
				this.put(map, WDirection.SOUTH, this.south);
				this.put(map, WDirection.WEST, this.west);
				this.put(map, WDirection.EAST, this.east);
				this.put(map, WDirection.UP, this.up);
				this.put(map, WDirection.DOWN, this.down);
				return map;
			}

			private final void put(Object2BooleanArrayMap<WDirection> map, WDirection direction, Boolean bl) {
				if (bl != null) {
					map.put(direction, bl.booleanValue());
				}
			}
		}
	}

	public static final RoomLayer<String> example(DungeonGenerator generator) {
		Random random = new Random(0);
		List<RoomVariant<String>> rooms = new ArrayList<>();
		List<WDirection> dir = new ArrayList<>();
		BaseLayerRoomRange bossBase = new BaseLayerRoomRange(null, null, null, new LayerRange(5, Integer.MAX_VALUE), null);
		for (WDirection direction : WDirection.values()) {
			if (direction != WDirection.UP && direction != WDirection.DOWN) {
				dir.add(direction);
			}
		}
		for (int i = 0; i < 150; i++) {
			Object2BooleanArrayMap<WDirection> directions = new Object2BooleanArrayMap<>();
			{
				int count = random.nextInt(4) + 1;
				if (count == 4) {
					for (WDirection wDirection : dir) {
						directions.put(wDirection, true);
					}
				} else {
					List<WDirection> dirCopy = new ArrayList<>(dir);
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

		Set<Set<WDirection>> filter = new HashSet<>();

		for (WDirection d1 : dir) {
			Set<WDirection> s1 = new HashSet<>();
			s1.add(d1);
			if (filter.add(s1)) {
				rooms.add(exampleEnterance(s1));
				for (WDirection d2 : dir) {
					Set<WDirection> s2 = new HashSet<>(s1);
					s2.add(d2);
					if (filter.add(s2)) {
						rooms.add(exampleEnterance(s2));
						for (WDirection d3 : dir) {
							Set<WDirection> s3 = new HashSet<>(s2);
							s3.add(d3);
							if (filter.add(s3)) {
								rooms.add(exampleEnterance(s3));
								for (WDirection d4 : dir) {
									Set<WDirection> s4 = new HashSet<>(s3);
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
				for (Set<WDirection> set : filter) {
					if (set.size() == i) {
						builder.append(set);
					}
				}
				DungeonGenerator.LOGGER.debug(builder);
			}
			DungeonGenerator.LOGGER.debug("===============");
		}

		return new RoomLayer<>(generator, 50, rooms).setTypeKey(TYPE);
	}

	private static final RoomVariant<String> exampleEnterance(Set<WDirection> direction) {
		Object2BooleanArrayMap<WDirection> directions = new Object2BooleanArrayMap<>();
		for (WDirection d : direction) {
			directions.put(d, true);
		}
		return new RoomVariant<String>(directions, BaseLayerRoomRange.NULL, true, "center", -1);
	}

}
