package net.w3e.wlib.dungeon.layers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.skds.lib2.io.codec.BuiltinCodecFactory;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.SkipSerialization;
import net.w3e.lib.TFNStateEnum;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.filters.RoomFilters;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomData;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.json.WJsonBuilder;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonNamed;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.registry.DungeonRegistryObject;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.log.LogUtil;

@DefaultCodec(FeatureLayer.FeatureLayerJsonAdapter.class)
public class FeatureLayer extends ListRegistryLayer<FeatureLayer.FeaturePoint, FeatureLayer.FeatureVariant> implements ISetupRoomLayer {

	public static final String TYPE = "feature";

	public static final String KEY = "features";

	private transient Progress progress = Progress.createArray;

	@DefaultCodec(FeatureLayer.ValuesCodec.class)
	private final List<DungeonRegistryObject> values;

	public FeatureLayer(DungeonGenerator generator, Collection<DungeonRegistryObject> values) {
		super(JSON_MAP.FEATURE, generator, values);
		this.values = super.registryList;
	}

	@Override
	public final FeatureLayer createGenerator(DungeonGenerator generator) {
		return new FeatureLayer(generator, this.registryList);
	}

	@Override
	protected final FeatureVariant getData(DungeonRegistryContext registryContext, DungeonKeySupplier keyName) {
		return registryContext.getTyped(keyName, FeatureVariant.class);
	}

	@Override
	public final void setupRoom(DungeonRoomInfo room) {
		room.getData().put(KEY, null);
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {
		super.setupLayer(composite);
	}

	@Override
	public final float generate() throws DungeonException {
		Progress prevProgress = this.progress;
		float i = 1;

		switch (this.progress) {
			case createArray -> {
				this.forEach(room -> {
					if (room.exists() && !room.isWall()) {
						this.list.add(new FeaturePoint(room.room()));
					}
					this.filled = this.list.size();
				}, true);
			}
			case fillRooms -> {
				Collections.shuffle(this.list, random());
				Iterator<FeaturePoint> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					FeaturePoint point = iterator.next();
					DungeonRoomInfo room = point.room;
					int soft = room.connectSoftCount();

					DungeonRoomData data = room.getData();
					RoomData roomData = data.getT(RoomLayer.KEY);
					if (roomData != null) {
						soft -= roomData.soft().size();
					}
					point.softCount.setValue(soft);

					for (FeatureVariant feature : this.workDataList) {
						if (feature.test(this, room, point.canSoft())) {
							point.variants.add(feature);
						}
					}
					if (point.variants.isEmpty()) {
						iterator.remove();
					}
				}
			}
			case repeat -> {
				Collections.shuffle(this.list, random());
				boolean remove = false;

				Iterator<FeaturePoint> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					FeaturePoint point = iterator.next();
					if (point.initRoom(this)) {
						remove = true;
					}
					if (point.variants.isEmpty()) {
						iterator.remove();
					}
				}
				if (remove) {
					this.removeLimitReachedFromVariants();
				}
				int size = this.size();

				if (size != this.filled) {
					i = this.size() * 1f / this.filled;
				}
			}
		}
		this.progress = this.progress.next(i);

		return this.progress.progress(prevProgress, i);
	}

	private final void removeLimitReachedFromVariants() throws DungeonException {
		Iterator<FeaturePoint> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			FeaturePoint point = iterator.next();
			if (point.variants.removeIf(IDungeonLimitedCount::isLimitReachedCount)) {
				if (point.variants.isEmpty()) {
					iterator.remove();
				}
			}
		}
	}

	private enum Progress implements IDungeonLayerProgress<Progress> {
		createArray,
		fillRooms,
		repeat,
		;

		@Override
		public final Progress[] getValues() {
			return Progress.values();
		}
	}

	public record FeaturePoint(DungeonRoomInfo room, List<FeatureVariant> variants, DungeonInfoCountHolder softCount) {
		public FeaturePoint(DungeonRoomInfo room) {
			this(room, new ArrayList<>(), new DungeonInfoCountHolder());
		}

		public final boolean canSoft() {
			return this.softCount.getValue() > 0;
		}

		public final boolean initRoom(FeatureLayer layer) {
			FeatureVariant variant;
			if (this.variants.size() == 1) {
				variant = this.variants.removeFirst();
			} else {
				variant = this.variants.remove(layer.random().nextInt(this.variants.size()));
			}
			if (variant.softRequire && !this.canSoft()) {
				return false;
			}
			variant.substractCount();

			List<DungeonKeySupplier> features = this.room.getData().getT(KEY);
			if (features == null) {
				features = new ArrayList<>();
				this.room.getData().put(KEY, features);
			}
			features.add(variant.keyName());
			if (variant.softRequire) {
				this.softCount.decrement();
			}

			return variant.isLimitReachedCount();
		}
	}

	@DefaultCodec(FeatureLayer.FeatureVariantJsonAdapter.class)
	public record FeatureVariant(
		DungeonKeySupplier keyName,
		@SkipSerialization(predicate = RoomFilters.RoomFiltersNullPredicate.class) RoomFilters filters,
		TFNStateEnum entrance,
		@SkipSerialization boolean softRequire,
		@SkipSerialization(predicate = DungeonInfoCountHolder.DungeonInfoCountHolderNullPredicate.class) DungeonInfoCountHolder count
	) implements IDungeonLimitedCount, IDungeonNamed {

		public final boolean notValid() {
			return this.filters.notValid();
		}

		public final boolean test(FeatureLayer layer, DungeonRoomInfo room, boolean canSoft) {
			if (this.softRequire && !canSoft) {
				return false;
			}
			if (this.entrance.isStated() && !((this.entrance.isTrue()) == room.isEntrance())) {
				return false;
			}
			return true;
		}

		@Override
		public final FeatureVariant clone() {
			return new FeatureVariant(this.keyName(), this.filters, this.entrance, this.softRequire, this.count.copy());
		}

		@Override
		public final String toString() {
			StringBuilder builder = new StringBuilder("{");
			builder.append(this.keyName);
			if (!this.filters.isEmpty()) {
				builder.append(String.format(",filters:%s", this.filters));
			}
			if (this.entrance.isStated()) {
				builder.append(String.format(",entrance:%s", this.entrance.name().toLowerCase()));
			}
			if (this.softRequire) {
				builder.append(",softRequire");
			}
			builder.append(String.format(",featureKey:%s", this.keyName));
			if (!this.isUnlimitedCount()) {
				builder.append(String.format(",count:%s", this.count.getValue()));
			}
			builder.append("}");

			return builder.toString();
		}
	}

	static class FeatureLayerJsonAdapter extends ReflectiveBuilderCodec<FeatureLayer> {

		public FeatureLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, FeatureLayerData.class, registry);
		}

		private static class FeatureLayerData extends RegistryLayerDataBuilder<FeatureLayer, FeatureLayer.FeatureVariant> {
			@Override
			public FeatureLayer build() {
				return new FeatureLayer(null, this.values);
			}
		}
	}

	static class FeatureVariantJsonAdapter extends ReflectiveBuilderCodec<FeatureVariant> {

		public FeatureVariantJsonAdapter(Type type, CodecRegistry registry) {
			super(type, FeatureVariantData.class, registry);
		}

		public static class FeatureVariantData implements WJsonBuilder<FeatureVariant> {
			public DungeonKeySupplier keyName;
			public Boolean entrance;
			public boolean softRequire = false;
			public DungeonInfoCountHolder count = DungeonInfoCountHolder.NULL;

			public RoomFilters filter = RoomFilters.NULL;

			@Override
			public final FeatureVariant build() {
				this.nonNull(this.keyName, "keyName");

				this.nonNull(filter, "filter");
				if (filter.notValid()) {
					throw new IllegalStateException(LogUtil.ILLEGAL.createMsg("filter"));
				}
				this.nonNull(this.count, "count");
				if (this.count.getValue() > -1) {
					this.lessThan(this.count.getValue(), "count");
				}

				TFNStateEnum entrance = this.entrance != null ? (this.entrance ? TFNStateEnum.TRUE : TFNStateEnum.FALSE) : TFNStateEnum.NOT_STATED;
				return new FeatureVariant(this.keyName, this.filter, entrance, this.softRequire, this.count);
			}
		}
	}

	static class ValuesCodec extends BuiltinCodecFactory.CollectionCodec {

		public ValuesCodec(Type tClass, CodecRegistry registry) {
			super(List.class, new DungeonRegistryObject.JCodec(FeatureLayer.FeatureVariant.class, registry), registry, ArrayList::new);
		}

	}
}
