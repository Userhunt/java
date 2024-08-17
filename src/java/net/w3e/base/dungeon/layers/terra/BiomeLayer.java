package net.w3e.base.dungeon.layers.terra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.collection.CollectionBuilder;
import net.w3e.base.collection.RandomCollection;
import net.w3e.base.dungeon.DungeonException;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.base.dungeon.layers.roomvalues.AbstractLayerRoomValues;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.base.dungeon.layers.ISetupLayer;
import net.w3e.base.dungeon.layers.LayerRange;
import net.w3e.base.dungeon.layers.ListLayer;
import net.w3e.base.holders.ObjectHolder;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;
import net.w3e.base.message.MessageUtil;

public class BiomeLayer<T> extends ListLayer<BiomeLayer.BiomePoint<T>> implements ISetupLayer {

	public static final String KEY = "biome";

	private final List<BiomeInfo<T>> biomeList = new ArrayList<>();
	private final List<BiomeInfo<T>> workList = new ArrayList<>();
	private Progress progress = Progress.createPoint;

	private final int percent;
	private final T def;

	@SafeVarargs
	public BiomeLayer(DungeonGenerator generator, T def, int percent, BiomeInfo<T>... biomes) {
		this(generator, def, percent, Arrays.asList(biomes));
	}

	public BiomeLayer(DungeonGenerator generator, T def, int percent, Collection<BiomeInfo<T>> biomes) {
		super(generator);
		this.def = def;
		this.percent = percent;
		this.biomeList.addAll(biomes);
		this.biomeList.removeIf(BiomeInfo::notValid);
	}

	@Override
	public final BiomeLayer<T> withDungeon(DungeonGenerator generator) {
		return new BiomeLayer<T>(generator, this.def, this.percent, this.biomeList);
	}

	@Override
	public final void setup(DungeonRoomInfo room) {
		room.data().put(KEY, this.def);
	}

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		this.list.clear();
		this.filled = -1;

		this.progress = Progress.createArray;

		this.workList.clear();
		this.biomeList.stream().map(BiomeInfo::copy).forEach(this.workList::add);
	}

	@Override
	public final int generate() throws DungeonException {
		Progress prevProgress = this.progress;
		float i = 100;

		switch (this.progress) {
			case createArray -> {
				List<DungeonRoomInfo> poses = new ArrayList<>();
				this.forEach(room -> {
					poses.add(room.room());
				});
				int size = poses.size();
				this.filled = 0;
				while (this.filled * 10f * 100 / size <= this.percent) {
					this.list.add(new BiomePoint<>(poses.remove(this.random().nextInt(size - this.filled))));
					this.filled++;
				}
			}
			case createPoint -> {
				Iterator<BiomePoint<T>> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					BiomePoint<T> point = iterator.next();
					MapTString data = point.room.data();
					AbstractLayerRoomValues<BaseLayerRoomRange> values = this.getRoomValues(point.room);
					RandomCollection<BiomeInfo<T>> random = new RandomCollection<>(this.random());
					for (BiomeInfo<T> biomeData : this.workList) {
						if (biomeData.test(this, values)) {
							random.add(biomeData.weight, biomeData);
						}
					}
					if (!random.isEmpty()) {
						BiomeInfo<T> info = random.getRandom();
						data.put(KEY, info.value());
						point.info.set(info);
						if (info.substractCount()) {
							this.workList.remove(info);
						}
					} else {
						iterator.remove();
					}
				}
				this.workList.clear();
			}
			case spread -> {
				Collections.shuffle(this.list, this.random());
				Iterator<BiomePoint<T>> iterator = this.list.iterator();
				while (iterator.hasNext()) {
					BiomePoint<T> point = iterator.next();
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

	public record BiomePoint<T>(DungeonRoomInfo room, ObjectHolder<BiomeInfo<T>> info, List<DungeonRoomInfo> rooms) {

		private BiomePoint(DungeonRoomInfo room) {
			this(room, new ObjectHolder<>(), CollectionBuilder.list(DungeonRoomInfo.class).add(room).build());
		}

		public final boolean fill(BiomeLayer<T> layer) {
			BiomeInfo<T> info = this.info.get();
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
				WVector3I pos = room.pos();

				for (WDirection direction : WDirection.values()) {
					if (random.nextInt(3) != 0) {
						DungeonRoomCreateInfo target = layer.putOrGet(pos.add(direction));
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
				WVector3I pos = next.pos();
				boolean found = false;
				for (WDirection direction : WDirection.values()) {
					DungeonRoomCreateInfo target = layer.putOrGet(pos.add(direction.getRelative()));
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

	public static record BiomeInfo<T>(int weight, BaseLayerRoomRange layerRange, LayerRange impulse, T value, IntHolder count) implements IDungeonLimitedCount {

		public BiomeInfo(int weight, BaseLayerRoomRange layerRange, LayerRange impulse, T value, int count) {
			this(weight, layerRange, impulse, value, new IntHolder(count));
		}

		public BiomeInfo(int weight, BaseLayerRoomRange layerRange, LayerRange impulse, T value) {
			this(weight, layerRange, impulse, value, -1);
		}

		public final boolean notValid() {
			return this.value == null || this.layerRange.notValid() || this.impulse.notValid() || (this.impulse.min() == 0 && this.impulse.range() == 0);
		}

		public final boolean test(BiomeLayer<T> layer, AbstractLayerRoomValues<BaseLayerRoomRange> values) {
			return values.test(layer.random(), this.layerRange);
		}

		public final BiomeInfo<T> copy() {
			return this.count.getAsInt() <= -1 ? this : new BiomeInfo<T>(this.weight, this.layerRange, this.impulse, this.value, this.count.copy());
		}
	}

	public abstract static class BiomeLayerData<T> implements ILayerAdapter<BiomeLayer<T>> {
		private int percent = 10;

		@Nullable
		protected abstract T getDef();
		protected abstract BiomeInfo<T>[] getBiomes();

		@Override
		public final BiomeLayer<T> withDungeon(DungeonGenerator generator) {
			BiomeInfo<T>[] biomes = this.getBiomes();
			this.isEmpty("biomes", biomes);
			this.lessThan("percent", this.percent);

			return new BiomeLayer<T>(generator, this.getDef(), this.percent, biomes);
		}
	}

	public abstract static class BiomeInfoData<T> implements ILayerAdapter<BiomeInfo<T>> {
		public int weight = 1;
		public LayerRange impulse = LayerRange.ONE;
		public int count = -1;

		protected abstract T getValue();
		protected abstract BaseLayerRoomRange getLayerRange();

		@Override
		public final BiomeInfo<T> withDungeon(DungeonGenerator generator) {
			BaseLayerRoomRange layerRange = this.getLayerRange();
			this.nonNull("layerRange", layerRange);
			if (layerRange.notValid()) {
				throw new IllegalStateException(MessageUtil.ILLEGAL.createMsg("layerRange"));
			}
			this.lessThan("weight", this.weight);
			this.nonNull("impulse", this.impulse);
			if (this.count != -1) {
				this.lessThan("count", this.count);
			}
			T value = this.getValue();
			this.nonNull("value", value);

			return new BiomeInfo<T>(weight, layerRange, impulse, value, this.count);
		}
	}

	public static final BiomeLayer<String> example(DungeonGenerator generator) {
		Random random = new Random(0);
		int range = BMatUtil.round((TemperatureLayer.MAX - TemperatureLayer.MIN) / 5f);
		List<BiomeInfo<String>> biomes = new ArrayList<>(); 
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
				biomes.add(new BiomeInfo<String>(weight, new BaseLayerRoomRange(new LayerRange(minTemp, maxTemp), random.nextBoolean() ? new LayerRange(minWet, maxWet) : null, null, null, null), new LayerRange(minImpulse, maxImpulse), String.format("%s-%s", name, j), 1 + random.nextInt(3)));
			}
		}

		return new BiomeLayer<>(generator, "void", 11, biomes);
	}

}
