package net.w3e.wlib.dungeon.layers;

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
import net.w3e.lib.TFNStateEnum;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.layers.RoomLayer.RoomData;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilters;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilters.RoomLayerFiltersNullPredicate;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder.DungeonInfoCountHolderNullPredicate;
import net.w3e.wlib.json.WJsonBuilder;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.log.LogUtil;

@DefaultJsonCodec(FeatureLayer.FeatureLayerJsonAdapter.class)
public class FeatureLayer extends ListLayer<FeatureLayer.FeaturePoint> implements ISetupRoomLayer {

	public static final String TYPE = "feature";

	public static final String KEY = "features";

	private final List<FeatureVariant> features = new ArrayList<>();
	private transient Progress progress = Progress.createArray;

	@SafeVarargs
	public FeatureLayer(DungeonGenerator generator, FeatureVariant... features) {
		this(generator, Arrays.asList(features));
	}

	public FeatureLayer(DungeonGenerator generator, Collection<FeatureVariant> features) {
		super(JSON_MAP.FEATURE, generator);
		this.features.addAll(features);
		this.features.removeIf(FeatureVariant::notValid);
	}

	@Override
	public final FeatureLayer withDungeon(DungeonGenerator generator) {
		return new FeatureLayer(generator, this.features);
	}

	@Override
	public final void setupRoom(DungeonRoomInfo room) {
		room.data().put(KEY, null);
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {
		copyList(this.features, FeatureVariant::copy);
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
				Collections.shuffle(this.list);
				Iterator<FeaturePoint> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					FeaturePoint point = iterator.next();
					DungeonRoomInfo room = point.room;
					int soft = room.connectCount(false) - room.connectCount(true);

					MapTString data = room.data();
					RoomData roomData = data.getT(RoomLayer.KEY);
					if (roomData != null) {
						soft -= roomData.soft().size();
					}
					point.softCount.setValue(soft);

					for (FeatureVariant feature : this.features) {
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
				Collections.shuffle(this.list);
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

			List<DungeonKeySupplier> features = this.room.data().getT(KEY);
			if (features == null) {
				features = new ArrayList<>();
				this.room.data().put(KEY, features);
			}
			features.add(variant.value);
			if (variant.softRequire) {
				this.softCount.decrement();
			}

			return variant.isLimitReachedCount();
		}
	}

	public record FeatureVariant(@SkipSerialization(predicate = RoomLayerFiltersNullPredicate.class) RoomLayerFilters layerRange, TFNStateEnum entrance, @SkipSerialization boolean softRequire, DungeonKeySupplier value, @SkipSerialization(predicate = DungeonInfoCountHolderNullPredicate.class) DungeonInfoCountHolder count) implements IDungeonLimitedCount {

		public final boolean notValid() {
			return this.layerRange.notValid();
		}

		public final boolean test(FeatureLayer layer, DungeonRoomInfo room, boolean canSoft) {
			if (this.softRequire && !canSoft) {
				return false;
			}
			if (this.entrance.isStated() && !((this.entrance.isTrue()) == room.isEntrance())) {
				return false;
			}
			return this.layerRange.test(layer.random(), layer.getRoomValues(room));
		}

		public final FeatureVariant copy() {
			return new FeatureVariant(this.layerRange, this.entrance, this.softRequire, this.value, this.count.copy());
		}

		@Override
		public final String toString() {
			StringBuilder builder = new StringBuilder("{");
			if (!this.layerRange.isNull()) {
				builder.append(String.format(",baseLayerRange:%s", this.layerRange));
			}
			if (this.entrance.isStated()) {
				builder.append(String.format(",entrance:%s", this.entrance.name().toLowerCase()));
			}
			if (this.softRequire) {
				builder.append(",softRequire");
			}
			builder.append(String.format(",value:%s", this.value));
			if (!this.isUnlimitedCount()) {
				builder.append(String.format(",count:%s", this.count.getValue()));
			}
			builder.append("}");

			return builder.toString();
		}
	}

	static class FeatureLayerJsonAdapter extends JsonReflectiveBuilderCodec<FeatureLayer> {

		public FeatureLayerJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, FeatureLayerData.class, registry);
		}

		private static class FeatureLayerData implements ILayerData<FeatureLayer> {
			protected FeatureVariantData[] features;
			@Override
			public FeatureLayer withDungeon(DungeonGenerator generator) {
				this.isEmpty("variants", this.features);
				FeatureVariant[] featuresVariant = Stream.of(this.features).map(FeatureVariantData::build).toArray(e -> new FeatureVariant[e]);
				return new FeatureLayer(generator, featuresVariant);
			}
		}

		public static class FeatureVariantData implements WJsonBuilder<FeatureVariant> {
			public Boolean entrance;
			public boolean softRequire = false;
			public DungeonInfoCountHolder count = DungeonInfoCountHolder.NULL;

			public DungeonKeySupplier value;
			public RoomLayerFilters layerRange = RoomLayerFilters.NULL;

			@Override
			public final FeatureVariant build() {
				this.nonNull("layerRange", layerRange);
				if (layerRange.notValid()) {
					throw new IllegalStateException(LogUtil.ILLEGAL.createMsg("layerRange"));
				}
				if (this.count.getValue() > -1) {
					this.lessThan("count", this.count.getValue());
				}
				this.nonNull("value", this.value);

				TFNStateEnum entrance = this.entrance != null ? (this.entrance ? TFNStateEnum.TRUE : TFNStateEnum.FALSE) : TFNStateEnum.NOT_STATED;
				return new FeatureVariant(this.layerRange, entrance, this.softRequire, this.value, this.count);
			}
		}
	}

	public static final FeatureLayer example(DungeonGenerator generator) {
		List<FeatureVariant> features = new ArrayList<>();
		Random random = new Random(0);
		for (int i = 0; i < 20; i++) {
			String name = String.valueOf(i + 1);
			features.add(new FeatureVariant(RoomLayerFilters.NULL, random.nextInt(100) + 1 <= 5 ? TFNStateEnum.TRUE : TFNStateEnum.FALSE, random.nextInt(100) + 1 <= 70, () -> name, new DungeonInfoCountHolder(random.nextInt(100) + 1 <= 75 ? random.nextInt(3) + 1 : -1)));
		}

		return new FeatureLayer(generator, features);
	}
}
