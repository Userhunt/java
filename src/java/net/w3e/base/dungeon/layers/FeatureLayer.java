package net.w3e.base.dungeon.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.w3e.base.BooleanEnum;
import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.dungeon.DungeonException;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.dungeon.layers.RoomLayer.RoomData;
import net.w3e.base.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.base.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.message.MessageUtil;

public class FeatureLayer<T> extends ListLayer<FeatureLayer.FeaturePoint<T>> implements ISetupLayer {

	public static final String KEY = "features";

	private final List<FeatureVariant<T>> featureList = new ArrayList<>();
	private final List<FeatureVariant<T>> workList = new ArrayList<>();
	private Progress progress;

	@SafeVarargs
	public FeatureLayer(DungeonGenerator generator, FeatureVariant<T>... features) {
		this(generator, Arrays.asList(features));
	}

	public FeatureLayer(DungeonGenerator generator, Collection<FeatureVariant<T>> features) {
		super(generator);
		this.featureList.addAll(features);
		this.featureList.removeIf(FeatureVariant::notValid);
	}

	@Override
	public final FeatureLayer<T> withDungeon(DungeonGenerator generator) {
		return new FeatureLayer<>(generator, this.featureList);
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
		this.featureList.stream().map(FeatureVariant::copy).forEach(this.workList::add);
		if (this.workList.isEmpty()) {
			throw new DungeonException(MessageUtil.IS_EMPTY.createMsg("Feature layer"));
		}
	}

	@Override
	public final int generate() throws DungeonException {
		Progress prevProgress = this.progress;
		float i = 100;

		switch (this.progress) {
			case createArray -> {
				this.forEach(room -> {
					if (room.exists() && !room.isWall()) {
						this.list.add(new FeaturePoint<>(room.room()));
					}
					this.filled = this.list.size();
				}, true);
			}
			case fillRooms -> {
				Collections.shuffle(this.list);
				Iterator<FeaturePoint<T>> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					FeaturePoint<T> point = iterator.next();
					DungeonRoomInfo room = point.room;
					int soft = room.connectCount(false) - room.connectCount(true);

					MapTString data = room.data();
					RoomData<T> roomData = data.getT(RoomLayer.KEY);
					if (roomData != null) {
						soft -= roomData.soft().size();
					}
					point.softCount.setValue(soft);

					for (FeatureVariant<T> feature : this.workList) {
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

				Iterator<FeaturePoint<T>> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					FeaturePoint<T> point = iterator.next();
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
					i = this.size() * 100f / this.filled;
				}
			}
		}
		this.progress = this.progress.next(i);

		return this.progress.progress(prevProgress, i);
	}

	private final void removeLimitReachedFromVariants() throws DungeonException {
		Iterator<FeaturePoint<T>> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			FeaturePoint<T> point = iterator.next();
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

	public record FeaturePoint<T>(DungeonRoomInfo room, List<FeatureVariant<T>> variants, IntHolder softCount) {
		public FeaturePoint(DungeonRoomInfo room) {
			this(room, new ArrayList<>(), new IntHolder());
		}

		public final boolean canSoft() {
			return this.softCount.getAsInt() > 0;
		}

		public final boolean initRoom(FeatureLayer<T> layer) {
			FeatureVariant<T> variant;
			if (this.variants.size() == 1) {
				variant = this.variants.removeFirst();
			} else {
				variant = this.variants.remove(layer.random().nextInt(this.variants.size()));
			}
			if (variant.softRequire && !this.canSoft()) {
				return false;
			}
			variant.substractCount();

			List<T> features = this.room.data().getT(KEY);
			if (features == null) {
				features = new ArrayList<>();
				this.room.data().put(KEY, features);
			}
			features.add(variant.value);
			if (variant.softRequire) {
				this.softCount.remove();
			}

			return variant.isLimitReachedCount();
		}
	}

	public record FeatureVariant<T>(BaseLayerRoomRange layerRange, BooleanEnum enterance, boolean softRequire, T value, IntHolder count) implements IDungeonLimitedCount {

		public FeatureVariant(BaseLayerRoomRange layerRange, BooleanEnum enterance, boolean softRequire, T value, int count) {
			this(layerRange, enterance, softRequire, value, new IntHolder(count));
		}

		public final boolean notValid() {
			return this.layerRange.notValid();
		}

		public final boolean test(FeatureLayer<T> layer, DungeonRoomInfo room, boolean canSoft) {
			if (this.softRequire && !canSoft) {
				return false;
			}
			if (this.enterance != BooleanEnum.NULL && !((this.enterance == BooleanEnum.TRUE) == room.isEnterance())) {
				return false;
			}
			return layer.getRoomValues(room).test(layer.random(), this.layerRange);
		}

		public final FeatureVariant<T> copy() {
			return new FeatureVariant<T>(this.layerRange, this.enterance, this.softRequire, this.value, this.count.copy());
		}

		@Override
		public final String toString() {
			StringBuilder builder = new StringBuilder("{");
			if (!this.layerRange.isNull()) {
				builder.append(String.format(",baseLayerRange:%s", this.layerRange));
			}
			if (this.enterance != BooleanEnum.NULL) {
				builder.append(String.format(",enterance:%s", this.enterance.name().toLowerCase()));
			}
			if (this.softRequire) {
				builder.append(",softRequire");
			}
			builder.append(String.format(",value:%s", this.value));
			if (!this.isUnlimitedCount()) {
				builder.append(String.format(",count:%s", this.count.get()));
			}
			builder.append("}");

			return builder.toString();
		}
	}

	public abstract static class FeatureLayerData<T> implements ILayerAdapter<FeatureLayer<T>> {

		protected abstract FeatureVariant<T>[] getFeatures();

		@Override
		public final FeatureLayer<T> withDungeon(DungeonGenerator generator) {
			FeatureVariant<T>[] variants = this.getFeatures();
			this.isEmpty("variants", variants);

			return new FeatureLayer<T>(generator, variants);
		}
	}

	public abstract static class FeatureVariantData<T> implements ILayerAdapter<FeatureVariant<T>> {
		private Boolean enterance;
		private boolean softRequire = false;
		public int count = -1;

		protected abstract T getValue();
		protected abstract BaseLayerRoomRange getLayerRange();

		@Override
		public final FeatureVariant<T> withDungeon(DungeonGenerator generator) {
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
			BooleanEnum enterance = this.enterance != null ? (this.enterance ? BooleanEnum.TRUE : BooleanEnum.FALSE) : BooleanEnum.NULL;
			return new FeatureVariant<T>(layerRange, enterance, this.softRequire, value, count);
		}
	}

	public static final FeatureLayer<String> example(DungeonGenerator generator) {
		List<FeatureVariant<String>> features = new ArrayList<>();
		Random random = new Random(0);
		for (int i = 0; i < 20; i++) {
			features.add(new FeatureVariant<String>(BaseLayerRoomRange.NULL, random.nextInt(100) + 1 <= 5 ? BooleanEnum.TRUE : BooleanEnum.FALSE, random.nextInt(100) + 1 <= 90, String.valueOf(i + 1), random.nextInt(100) + 1 <= 75 ? random.nextInt(3) + 1 : -1));
		}

		return new FeatureLayer<>(generator, features);
	}
}
