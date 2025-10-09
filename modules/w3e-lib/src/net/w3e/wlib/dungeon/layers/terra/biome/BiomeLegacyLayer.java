package net.w3e.wlib.dungeon.layers.terra.biome;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import lombok.CustomLog;
import net.skds.lib2.io.codec.BuiltinCodecFactory;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.SkipSerialization;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.collection.WeightedPool;
import net.skds.lib2.utils.linkiges.Obj2FloatPair;
import net.skds.lib2.utils.linkiges.Obj2FloatPairRecord;
import net.w3e.wlib.collection.CollectionBuilder;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.filters.RoomFilters;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.ISetupRoomLayer;
import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.ListRegistryLayer;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.json.WJsonBuilder;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonNamed;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.registry.DungeonRegistryObject;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

@CustomLog
@DefaultCodec(BiomeLegacyLayer.BiomeLayerJsonAdapter.class)
public class BiomeLegacyLayer extends ListRegistryLayer<BiomeLegacyLayer.BiomePoint, BiomeLegacyLayer.BiomeInfo> implements ISetupRoomLayer {

	public static final String TYPE = "terra/biome/legacy";

	private transient Progress progress = Progress.createArray;

	private final int percent;
	private final DungeonKeySupplier def;

	// TODO
	@DefaultCodec(BiomeLegacyLayer.ValuesCodec.class)
	private final List<DungeonRegistryObject> values;

	public BiomeLegacyLayer(DungeonGenerator generator, Collection<DungeonRegistryObject> values, DungeonKeySupplier def, int percent) {
		super(JSON_MAP.BIOME_LEGACY, generator, values);
		this.def = def;
		this.percent = percent;
		this.values = super.registryList;
	}

	@Override
	protected final BiomeInfo getData(DungeonRegistryContext registryContext, DungeonKeySupplier keyName) {
		return registryContext.getTyped(keyName, BiomeInfo.class);
	}

	@Override
	public final BiomeLegacyLayer createGenerator(DungeonGenerator generator) {
		return new BiomeLegacyLayer(generator, this.registryList, this.def, this.percent);
	}

	@Override
	public final void setupRoom(DungeonRoomInfo room) {
		room.getData().setBiome(this.def);
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {
		super.setupLayer(composite);
		this.workDataList.removeIf(BiomeInfo::notValid);
	}

	@Override
	public final float generate() throws DungeonException {
		Progress prevProgress = this.progress;
		float i = 1;

		final Random random = this.random();
		switch (this.progress) {
			case createArray -> {
				List<DungeonRoomInfo> poses = new ArrayList<>();
				this.forEach(room -> {
					poses.add(room.room());
				});
				int size = poses.size();
				this.filled = 0;

				while (this.filled * 10f * 100 / size <= this.percent && !poses.isEmpty()) {
					DungeonRoomInfo room = ArrayUtils.getRandom(poses, random);
					DungeonRoomData data = room.getData();
					if (data.getBiome() != this.def) {
						continue;
					}
					List<Obj2FloatPair<BiomeInfo>> randomCollection = new ArrayList<>();
					for (BiomeInfo biomeData : this.workDataList) {
						randomCollection.add(new Obj2FloatPairRecord<>(biomeData.weight, biomeData));
					}
					if (randomCollection.isEmpty()) {
						continue;
					}
					BiomeInfo info = new WeightedPool<>(randomCollection).get(random.nextFloat());
					data.setBiome(info.keyName());
					if (info.substractCount()) {
						this.workDataList.remove(info);
					}

					BiomePoint point = new BiomePoint(room, info, random);

					this.list.add(point);
					this.filled++;
				}
				this.workDataList.clear();
			}
			case spread -> {
				this.list.removeIf(point -> point.fill(this));
				Collections.shuffle(this.list, random);

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
		spread
		;

		@Override
		public final Progress[] getValues() {
			return Progress.values();
		}

	}

	public record BiomePointRoom(DungeonRoomInfo room, BitSet walkedDirections) {
		public BiomePointRoom(DungeonRoomInfo room) {
			this(room, new BitSet());
		}
	}

	public record BiomePoint(BiomeInfo info, int impulse, List<BiomePointRoom> rooms) {

		private BiomePoint(DungeonRoomInfo room, BiomeInfo info, Random random) {
			this(info, info.impulse.random(random), CollectionBuilder.list(BiomePointRoom.class).add(new BiomePointRoom(room)).build());
		}

		public final boolean fill(BiomeLegacyLayer layer) {
			if (this.impulse <= 0) {
				log.log("impulse is empty " + this.rooms.getFirst());
				return true;
			}
			Random random = layer.random();
			Collections.shuffle(this.rooms, random);

			for (int i = 0; i < this.impulse && i < this.rooms.size(); i++) {
				BiomePointRoom room = this.rooms.get(i);
				Vec3I pos = room.room.getPos();

				BitSet walkedDirections = room.walkedDirections();
				for (Direction direction : Direction.values()) {
					int ordinal = direction.ordinal();
					if (!walkedDirections.get(ordinal) && random.nextInt(3) != 0) {
						//log.log(pos + " " + direction);
						walkedDirections.set(ordinal);
						DungeonRoomCreateInfo target = layer.putOrGet(pos.addI(direction));
						DungeonRoomInfo targetRoom = target.room();
						DungeonRoomData targetData = target.getData();
						boolean bl = target.isInside() && targetData.getBiome() == layer.def;
						if (bl) {
							bl = this.info.test(layer, targetRoom);
							if (!bl) {
								bl = this.info.applyChanceSperad(random);
								//log.log("chanceSpread " + this.info.chanceSpread + " " + bl);
							}
							if (bl) {
								targetData.setBiome(this.info.keyName());
								this.rooms.add(new BiomePointRoom(targetRoom));
							}
						}
					}
				}
			}
			Iterator<BiomePointRoom> iterator = this.rooms.iterator();
			while (iterator.hasNext()) {
				BiomePointRoom next = iterator.next();

				dirs: {
					BitSet walkedDirections = next.walkedDirections();
					for (Direction direction : Direction.values()) {
						if (!walkedDirections.get(direction.ordinal())) {
							break dirs;
						}
					}
					//log.log("clear by directions " + next.room.pos());
					iterator.remove();
					continue;
				}

				p: {
					final Vec3I pos = next.room.getPos();
					for (Direction direction : Direction.values()) {
						DungeonRoomCreateInfo target = layer.putOrGet(pos.addI(direction));
						if (target.isInside() && target.getData().getBiome() == layer.def) {
							break p;
						}
					}
					//log.log("clear by lock " + next.room.pos());
					iterator.remove();
					continue;
				}
			}
			return this.rooms.isEmpty();
		}

	}

	@DefaultCodec(BiomeLegacyLayer.BiomeInfoJsonAdapter.class)
	public static record BiomeInfo(
		DungeonKeySupplier keyName,
		int weight,
		@SkipSerialization(predicate = RoomFilters.RoomFiltersNullPredicate.class) RoomFilters filter,
		LayerRange impulse,
		@SkipSerialization(predicate = ChanceSpreadNullPredicate.class) float chanceSpread,
		@SkipSerialization(predicate = DungeonInfoCountHolder.DungeonInfoCountHolderNullPredicate.class) DungeonInfoCountHolder count
	) implements IDungeonLimitedCount, IDungeonNamed {

		public BiomeInfo(DungeonKeySupplier keyName, int weight, RoomFilters filter, LayerRange impulse, float chanceSpread) {
			this(keyName, weight, filter, impulse, chanceSpread, DungeonInfoCountHolder.NULL);
		}

		@Override
		public boolean notValid() {
			return this.keyName == null || this.filter.notValid() || this.impulse.notValid() || (this.impulse.min() == 0 && this.impulse.range() == 0);
		}

		public boolean test(BiomeLegacyLayer layer, DungeonRoomInfo values) {
			return this.filter.test(layer.random(), values);
		}

		public boolean applyChanceSperad(Random random) {
			if (this.chanceSpread >= 1) {
				return true;
			} else if (this.chanceSpread <= 0) {
				return false;
			} else {
				return random.nextFloat() <= this.chanceSpread;
			}
		}

		@Override
		public BiomeInfo clone() {
			return this.count.getValue() <= -1 ? this : new BiomeInfo(this.keyName(), this.weight(), this.filter(), this.impulse(), this.chanceSpread(), this.count().copy());
		}

	}

	static class BiomeLayerJsonAdapter extends ReflectiveBuilderCodec<BiomeLegacyLayer> {

		public BiomeLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, BiomeLayerData.class, registry);
		}

		private static class BiomeLayerData extends RegistryLayerDataBuilder<BiomeLegacyLayer, BiomeLegacyLayer.BiomeInfo> {

			protected int percent = 10;

			protected DungeonKeySupplier def;

			@Override
			public final BiomeLegacyLayer build() {
				this.lessThan(this.percent, "percent");
				return new BiomeLegacyLayer(null, this.values, this.def, this.percent);
			}
		}
	}

	static class BiomeInfoJsonAdapter extends ReflectiveBuilderCodec<BiomeInfo> {

		public BiomeInfoJsonAdapter(Type type, CodecRegistry registry) {
			super(type, BiomeInfoData.class, registry);
		}

		public static class BiomeInfoData implements WJsonBuilder<BiomeInfo> {
			public DungeonKeySupplier keyName;
			public int weight = 1;
			public RoomFilters filter = RoomFilters.NULL;
			public LayerRange impulse = LayerRange.ONE;
			public float chanceSpread;
			public DungeonInfoCountHolder count = DungeonInfoCountHolder.NULL;

			@Override
			public BiomeInfo build() {
				this.nonNull(this.keyName, "keyName");
				this.lessThan(this.weight, "weight");
				this.nonNull(this.filter, "filter");
				if (this.filter.notValid()) {
					throw new IllegalStateException("filter is not valid");
				}
				this.nonNull(this.impulse, "impulse");
				if (this.impulse.notValid() || (this.impulse.min() == 0 && this.impulse.range() == 0)) {
					throw new IllegalStateException("impulse is not valid");
				}
				this.nonNull(this.count, "count");
				if (this.count.getValue() > -1) {
					this.lessThan(this.count.getValue(), "count");
				}
				return new BiomeInfo(this.keyName, this.weight, this.filter, this.impulse, this.chanceSpread, this.count);
			}

		}

	}

	static class ChanceSpreadNullPredicate implements Predicate<Float> {
		@Override
		public boolean test(Float t) {
			return t <= 0f;
		}
	}

	static class ValuesCodec extends BuiltinCodecFactory.CollectionCodec {

		public ValuesCodec(Type tClass, CodecRegistry registry) {
			super(List.class, new DungeonRegistryObject.JCodec(BiomeLegacyLayer.BiomeInfo.class, registry), registry, ArrayList::new);
		}

	}

}
