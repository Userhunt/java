package net.w3e.wlib.dungeon.layers.terra;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.annotation.SkipSerialization;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.Holders.ObjectHolder;
import net.skds.lib2.utils.collection.WeightedPool;
import net.skds.lib2.utils.linkiges.Obj2FloatPair;
import net.skds.lib2.utils.linkiges.Obj2FloatPairRecord;
import net.w3e.wlib.collection.CollectionBuilder;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.layers.ISetupRoomLayer;
import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.ListLayer;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilter;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilterValues;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilters;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilters.RoomLayerFiltersNullPredicate;
import net.w3e.wlib.dungeon.layers.filter.types.TempRoomFilter;
import net.w3e.wlib.dungeon.layers.filter.types.WetRoomFilter;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder.DungeonInfoCountHolderNullPredicate;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;

@DefaultJsonCodec(BiomeLayer.BiomeLayerJsonAdapter.class)
public class BiomeLayer extends ListLayer<BiomeLayer.BiomePoint> implements ISetupRoomLayer {

	public static final String TYPE = "terra/biome";

	public static final String KEY = "biome";

	private final List<BiomeInfo> biomes = new ArrayList<>();
	private transient Progress progress = Progress.createArray;

	private final int percent;
	private final DungeonKeySupplier def;

	@SafeVarargs
	public BiomeLayer(DungeonGenerator generator, DungeonKeySupplier def, int percent, BiomeInfo... biomes) {
		this(generator, def, percent, Arrays.asList(biomes));
	}

	public BiomeLayer(DungeonGenerator generator, DungeonKeySupplier def, int percent, Collection<BiomeInfo> biomes) {
		super(JSON_MAP.BIOME, generator);
		this.def = def;
		this.percent = percent;
		this.biomes.addAll(biomes);
		this.biomes.removeIf(BiomeInfo::notValid);
	}

	@Override
	public final BiomeLayer withDungeon(DungeonGenerator generator) {
		return new BiomeLayer(generator, this.def, this.percent, this.biomes);
	}

	@Override
	public final void setupRoom(DungeonRoomInfo room) {
		room.data().put(KEY, this.def);
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {
		copyList(this.biomes, BiomeInfo::copy);
	}

	@Override
	public final float generate() throws DungeonException {
		Progress prevProgress = this.progress;
		float i = 1;

		switch (this.progress) {
			case createArray -> {
				List<DungeonRoomInfo> poses = new ArrayList<>();
				this.forEach(room -> {
					poses.add(room.room());
				});
				int size = poses.size();
				this.filled = 0;
				while (this.filled * 10f * 100 / size <= this.percent) {
					this.list.add(new BiomePoint(poses.remove(this.random().nextInt(size - this.filled))));
					this.filled++;
				}
			}
			case createPoint -> {
				Iterator<BiomePoint> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					BiomePoint point = iterator.next();
					MapTString data = point.room.data();
					RoomLayerFilterValues values = this.getRoomValues(point.room);
					List<Obj2FloatPair<BiomeInfo>> randomCollection = new ArrayList<>();
					for (BiomeInfo biomeData : this.biomes) {
						if (biomeData.test(this, values)) {
							randomCollection.add(new Obj2FloatPairRecord<>(biomeData.weight, biomeData));
						}
					}
					if (!randomCollection.isEmpty()) {
						BiomeInfo info = new WeightedPool<>(randomCollection).get(this.random().nextFloat());
						data.put(KEY, info.value());
						point.info.setValue(info);
						if (info.substractCount()) {
							this.biomes.remove(info);
						}
					} else {
						iterator.remove();
					}
				}
				this.biomes.clear();
			}
			case spread -> {
				Collections.shuffle(this.list, this.random());
				Iterator<BiomePoint> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					BiomePoint point = iterator.next();
					if (point.fill(this)) {
						iterator.remove();
					}
				}

				int size = this.size();

				if (size != this.filled) {
					i = this.size() * 100f / this.filled;
				}
			}
		}
		this.progress = this.progress.next(i);

		return this.progress.progress(prevProgress, i);
	}

	private enum Progress implements IDungeonLayerProgress<Progress> {
		createArray,
		createPoint,
		spread
		;

		@Override
		public final Progress[] getValues() {
			return Progress.values();
		}
	}

	public record BiomePoint(DungeonRoomInfo room, ObjectHolder<BiomeInfo> info, List<DungeonRoomInfo> rooms) {

		private BiomePoint(DungeonRoomInfo room) {
			this(room, new ObjectHolder<>(), CollectionBuilder.list(DungeonRoomInfo.class).add(room).build());
		}

		public final boolean fill(BiomeLayer layer) {
			BiomeInfo info = this.info.getValue();
			Random random = layer.random();
			Collections.shuffle(this.rooms, random);

			int impulse = 0;
			try {
				impulse = info.impulse.random(random);
			} catch (Exception e) {
				throw e;
			}
			for (int i = 0; i < impulse; i++) {
				DungeonRoomInfo room = this.rooms.get(random.nextInt(this.rooms.size()));
				Vec3I pos = room.pos();

				for (Direction direction : Direction.values()) {
					if (random.nextInt(3) != 0) {
						DungeonRoomCreateInfo target = layer.putOrGet(pos.addI(direction));
						DungeonRoomInfo targetRoom = target.room();
						MapTString targetData = target.data();
						if (target.isInside() && targetData.get(KEY) == layer.def) {
							targetData.put(KEY, info.value);
							this.rooms.add(targetRoom);
						}
					}
				}
			}
			Iterator<DungeonRoomInfo> iterator = this.rooms.iterator();
			while (iterator.hasNext()) {
				DungeonRoomInfo next = iterator.next();
				Vec3I pos = next.pos();
				boolean found = false;
				for (Direction direction : Direction.values()) {
					DungeonRoomCreateInfo target = layer.putOrGet(pos.addI(direction));
					if (target.isInside() && target.data().get(KEY) == layer.def) {
						found = true;
						break;
					}
				}
				if (!found) {
					iterator.remove();
				}
			}
			return this.rooms.isEmpty();
		}
	}

	public static record BiomeInfo(int weight, @SkipSerialization(predicate = RoomLayerFiltersNullPredicate.class) RoomLayerFilters layerRange, LayerRange impulse, DungeonKeySupplier value, @SkipSerialization(predicate = DungeonInfoCountHolderNullPredicate.class) DungeonInfoCountHolder count) implements IDungeonLimitedCount {

		public BiomeInfo(int weight, RoomLayerFilters layerRange, LayerRange impulse, DungeonKeySupplier value) {
			this(weight, layerRange, impulse, value, DungeonInfoCountHolder.NULL);
		}

		public final boolean notValid() {
			return this.value == null || this.layerRange.notValid() || this.impulse.notValid() || (this.impulse.min() == 0 && this.impulse.range() == 0);
		}

		public final boolean test(BiomeLayer layer, RoomLayerFilterValues values) {
			return this.layerRange.test(layer.random(), values);
		}

		public final BiomeInfo copy() {
			return this.count.getValue() <= -1 ? this : new BiomeInfo(this.weight, this.layerRange, this.impulse, this.value, this.count.copy());
		}
	}

	static class BiomeLayerJsonAdapter extends JsonReflectiveBuilderCodec<BiomeLayer> {

		public BiomeLayerJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, BiomeLayerData.class, registry);
		}

		private static class BiomeLayerData implements ILayerData<BiomeLayer> {
			protected int percent = 10;
	
			protected DungeonKeySupplier def;
			private BiomeInfoData[] biomes;
	
			@Override
			public final BiomeLayer withDungeon(DungeonGenerator generator) {
				this.isEmpty("biomes", this.biomes);
				this.lessThan("percent", this.percent);
				BiomeInfo[] biomesInfo = Stream.of(this.biomes).map(e -> new BiomeInfo(e.weight, e.layerRange, e.impulse, e.value, e.count)).toArray(e -> new BiomeInfo[e]);
				return new BiomeLayer(generator, this.def, this.percent, biomesInfo);
			}
		}

		public static class BiomeInfoData {
			public int weight = 1;
			public LayerRange impulse = LayerRange.ONE;
			public DungeonInfoCountHolder count = DungeonInfoCountHolder.NULL;
	
			public DungeonKeySupplier value;
			public RoomLayerFilters layerRange = RoomLayerFilters.NULL;
		}
	}

	public static final BiomeLayer example(DungeonGenerator generator) {
		Random random = new Random(0);
		int range = FastMath.round((TemperatureLayer.MAX - TemperatureLayer.MIN) / 5f);
		List<BiomeInfo> biomes = new ArrayList<>(); 
		for (int i = 0; i < range; i++) {
			for (int j = 1; j < 3; j++) {
				int name = i * 5 + TemperatureLayer.MIN;

				int minTemp = name;
				int maxTemp = minTemp + 5;
				int weight = random.nextInt(10) + 5;
				minTemp += random.nextInt(5) - 2;
				maxTemp += random.nextInt(5) - 2;

				int minImpulse = random.nextInt(4);
				int maxImpulse = random.nextInt(6);

				int minWet = random.nextInt(25) + 26;
				int maxWet = random.nextInt(25) + 51;

				List<RoomLayerFilter<?>> filters = new ArrayList<>();

				filters.add(new TempRoomFilter(new LayerRange(minTemp, maxTemp)));

				if (random.nextBoolean()) {
					filters.add(new WetRoomFilter(new LayerRange(minWet, maxWet)));
				}

				String key = String.format("%s-%s", name, j);

				biomes.add(new BiomeInfo(weight, new RoomLayerFilters(filters), new LayerRange(minImpulse, maxImpulse), () -> key, new DungeonInfoCountHolder(1 + random.nextInt(3))));
			}
		}

		return new BiomeLayer(generator, () -> "void", 11, biomes);
	}

}
