package net.w3e.base.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.w3e.base.collection.CollectionBuilder;
import net.w3e.base.collection.CollectionBuilder.SimpleCollectionBuilder;
import net.w3e.base.collection.MapT;
import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.dungeon.layers.BiomeLayer;
import net.w3e.base.dungeon.layers.FeatureLayer;
import net.w3e.base.dungeon.layers.RoomLayer;
import net.w3e.base.dungeon.layers.TemperatureLayer;
import net.w3e.base.dungeon.layers.WormLayer;
import net.w3e.base.holders.BoolHolder;
import net.w3e.base.math.vector.WBox;
import net.w3e.base.math.vector.WVector2;
import net.w3e.base.math.vector.WVector3;

public class DungeonGenerator<T> {

	private final Map<WVector2, Map<WVector3, DungeonRoomInfo<T>>> map = new HashMap<>();

	private final long seed;
	private final WBox dimension;
	private final Supplier<MapT<T>> dataFactory;
	private final List<Factory<T>> layers = new ArrayList<>();

	private Random random;
	private final List<DungeonLayer<T>> queue = new LinkedList<>();

	public DungeonGenerator(long seed, WBox dimension, Supplier<MapT<T>> dataFactory, List<Factory<T>> layers) {
		this.seed = seed;
		this.dimension = dimension;
		this.dataFactory = dataFactory;
		this.layers.addAll(layers);
	}

	public final Random random() {
		return this.random;
	}

	public final WBox dimension() {
		return this.dimension;
	}

	public final void regenerate() {
		this.map.clear();
		this.random = new Random(this.seed);
		this.queue.clear();
		this.queue.addAll(this.layers.stream().map(e -> e.create(this)).toList());
		this.queue.forEach(DungeonLayer::regenerate);
	}

	public final DungeonRoomCreateInfo<T> put(WVector3 pos) {
		if (!this.testDimension(pos)) {
			return this.createFailRoom(pos);
		}
		WVector2 chunk = pos.toChunk();
		BoolHolder exists = new BoolHolder(true);
		DungeonRoomInfo<T> room = map.computeIfAbsent(chunk, key -> new HashMap<>()).computeIfAbsent(pos, key -> {
			exists.setFalse();
			return DungeonRoomInfo.create(pos, chunk, this.dataFactory);
		});
		return DungeonRoomCreateInfo.success(room, exists.getBool());
	}

	public final DungeonRoomCreateInfo<T> get(WVector3 pos) {
		if (this.testDimension(pos)) {
			WVector2 chunk = pos.toChunk();

			Map<WVector3, DungeonRoomInfo<T>> m = map.get(chunk);
			if (m != null) {
				DungeonRoomInfo<T> room = m.get(pos);
				if (room != null) {
					return DungeonRoomCreateInfo.success(room, true);
				}
			}
			return this.createFailRoom(pos, chunk);
		}
		return this.createFailRoom(pos);
	}

	private final DungeonRoomCreateInfo<T> createFailRoom(WVector3 pos) {
		return createFailRoom(pos, pos.toChunk());
	}

	private final DungeonRoomCreateInfo<T> createFailRoom(WVector3 pos, WVector2 chunk) {
		return DungeonRoomCreateInfo.fail(DungeonRoomInfo.create(pos, this.dataFactory));
	}

	public record DungeonRoomCreateInfo<T>(DungeonRoomInfo<T> room, boolean success, boolean exists) {
		protected static final <T> DungeonRoomCreateInfo<T> fail(DungeonRoomInfo<T> room) {
			return new DungeonRoomCreateInfo<>(room, false, false);
		}
		protected static final <T> DungeonRoomCreateInfo<T> success(DungeonRoomInfo<T> room, boolean exists) {
			return new DungeonRoomCreateInfo<>(room, true, exists);
		}
	}

	private final boolean testDimension(WVector3 pos) {
		return this.dimension.contains(pos);
	}

	public final DungeonGenerator<T> copy(Long seed, WBox dimension, boolean data) {
		DungeonGenerator<T> dungeon = new DungeonGenerator<>(seed == null ? this.seed : seed, dimension == null ? this.dimension : dimension, this.dataFactory, this.layers);
		if (data) {
			for (Map<WVector3, DungeonRoomInfo<T>> chunk : this.map.values()) {
				for (Entry<WVector3, DungeonRoomInfo<T>> entry : chunk.entrySet()) {
					DungeonRoomCreateInfo<T> info = dungeon.put(entry.getKey());
					if (info.success) {
						info.room.copyFrom(entry.getValue());
					}
				}
			}
		}

		return dungeon;
	}

	public final Map<WVector2, Map<WVector3, DungeonRoomInfo<T>>> getRooms() {
		return this.map;
	}

	public final int generate() {
		int i = 100;
		if (!queue.isEmpty()) {
			DungeonLayer<T> generator = this.queue.getFirst();
			i = generator.generate();
			if (i == 100) {
				i = 0;
				this.queue.removeFirst();
			}
		}

		return (this.layers.size() - this.queue.size()) * 100 / this.layers.size() + i / this.layers.size();
	}

	public final DungeonLayer<T> getFirst() {
		return this.queue.isEmpty() ? null : this.queue.getFirst();
	}

	public static interface Factory<T> {
		DungeonLayer<T> create(DungeonGenerator<T> generator);
	}

	@SuppressWarnings("unchecked")
	public static final SimpleCollectionBuilder<Factory<String>, ArrayList<Factory<String>>> factoryCollectionBuilder() {
		return CollectionBuilder.list((Class<Factory<String>>)(Object)Factory.class);
	}

	public static DungeonGenerator<String> example(long seed) {
		int size = 12;
		return new DungeonGenerator<>(seed, new WBox(-size - 1, 0, -size - 1, size, 0, size), MapTString::new, factoryCollectionBuilder().add(
			// path
			WormLayer::example,
			// temperature
			TemperatureLayer::example,
			// biomes
			BiomeLayer::example,
			// rooms
			RoomLayer::example,
			// features
			FeatureLayer::example
		).build());
	}
}
