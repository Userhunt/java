package net.w3e.base.dungeon.layers.terra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.collection.RandomCollection;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.layers.BaseLayerRange;
import net.w3e.base.dungeon.layers.BaseLayerRange.BaseLayerRangeRoomValues;
import net.w3e.base.dungeon.layers.IListLayer;
import net.w3e.base.dungeon.layers.ISetupLayer;
import net.w3e.base.dungeon.layers.LayerRange;
import net.w3e.base.holders.ObjectHolder;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;

public class BiomeLayer<T> extends DungeonLayer implements ISetupLayer, IListLayer {

	public static final String KEY = "biome";

	private final List<BiomeInfo<T>> biomeList = new ArrayList<>();
	private final List<BiomeInfo<T>> workList = new ArrayList<>();
	private final List<BiomePoint<T>> list = new ArrayList<>();
	private int filled = -1;
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
	public final void setup(DungeonRoomInfo room) {
		room.data().put(KEY, this.def);
	}

	@Override
	public final void regenerate(boolean composite) {
		this.list.clear();
		this.workList.clear();
		this.filled = -1;
		this.progress = Progress.createArray;

		this.biomeList.stream().map(BiomeInfo::copy).forEach(this.workList::add);
	}

	@Override
	public final int generate() {
		Progress prevProgress = this.progress;
		float i = 0;

		if (this.progress == Progress.createArray) {
			List<WVector3> poses = new ArrayList<>();
			this.forEach(room -> {
				poses.add(room.pos());
			}, false);
			int size = poses.size();
			this.filled = 0;
			while (this.filled * 10f * 100 / size <= this.percent) {
				this.list.add(new BiomePoint<>(poses.remove(this.random().nextInt(size - this.filled))));
				this.filled++;
			}
			this.progress = Progress.createPoint;
			i = 100;
		} else if (this.progress == Progress.createPoint) {
			Iterator<BiomePoint<T>> iterator = this.list.iterator();
			while (iterator.hasNext()) {
				BiomePoint<T> point = iterator.next();
				DungeonRoomCreateInfo room = this.putOrGet(point.pos);
				MapTString data = room.data();
				BaseLayerRangeRoomValues values = new BaseLayerRangeRoomValues(room.room());
				RandomCollection<BiomeInfo<T>> random = new RandomCollection<>(this.random());
				for (BiomeInfo<T> biomeData : this.workList) {
					if (biomeData.test(values)) {
						random.add(biomeData.weight, biomeData);
					}
				}
				if (!random.isEmpty()) {
					BiomeInfo<T> info = random.getRandom();
					data.put(KEY, info.value());
					point.info.set(info);
					if (info.substract()) {
						this.workList.remove(info);
					}
				} else {
					iterator.remove();
				}
			}
			this.workList.clear();
			this.progress = Progress.spread;
			i = 100;
		} else if (this.progress == Progress.spread) {
			Collections.shuffle(this.list, this.random());
			Iterator<BiomePoint<T>> iterator = this.list.iterator();
			while (iterator.hasNext()) {
				BiomePoint<T> point = iterator.next();
				DungeonRoomCreateInfo room = this.putOrGet(point.pos);
				if (point.rooms.isEmpty()) {
					point.rooms.add(room.room());
				}
				if (point.fill(this)) {
					iterator.remove();
				}
			}

			int size = this.size();

			if (size != this.filled) {
				i = this.size() * 100f / this.filled;
			} else {
				i = 100;
			}
		}

		float partScale = 100f / Progress.values().length;

		return BMatUtil.round(prevProgress.ordinal() * partScale + i * partScale / 100);
	}

	private enum Progress {
		createArray,
		createPoint,
		spread
		;
	}

	@Override
	public final int size() {
		return this.filled - this.list.size();
	}

	@Override
	public final int filled() {
		return this.filled;
	}

	private record BiomePoint<T>(WVector3 pos, ObjectHolder<BiomeInfo<T>> info, List<DungeonRoomInfo> rooms) {

		private BiomePoint(WVector3 pos) {
			this(pos, new ObjectHolder<>(), new ArrayList<>());
		}

		public final boolean fill(BiomeLayer<T> layer) {
			BiomeInfo<T> info = this.info.get();
			Random random = layer.random();
			Collections.shuffle(this.rooms, random);

			int impulse = info.impulse.random(random);
			for (int i = 0; i < impulse; i++) {
				DungeonRoomInfo room = this.rooms.get(random.nextInt(this.rooms.size()));
				WVector3 pos = room.pos();

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
				WVector3 pos = next.pos();
				boolean found = false;
				for (WDirection direction : WDirection.values()) {
					DungeonRoomCreateInfo target = layer.putOrGet(pos.add(direction.relative));
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

	public static record BiomeInfo<T>(int weight, BaseLayerRange baseLayerRange, LayerRange impulse, T value, int[] count) {

		public BiomeInfo(int weight, BaseLayerRange baseLayerRange, LayerRange impulse, T value, int count) {
			this(weight, baseLayerRange, impulse, value, new int[]{count});
		}

		public BiomeInfo(int weight, BaseLayerRange baseLayerRange, LayerRange impulse, T value) {
			this(weight, baseLayerRange, impulse, value, -1);
		}

		public final boolean notValid() {
			return this.value == null || this.baseLayerRange.notValid() || this.impulse.notValid() || (this.impulse.min() == 0 && this.impulse.range() == 0);
		}

		public final boolean test(BaseLayerRangeRoomValues values) {
			return this.baseLayerRange.test(values);
		}

		public final boolean substract() {
			return this.count[0] >= 0 && this.count[0]-- <= 0;
		}

		public final BiomeInfo<T> copy() {
			return this.count[0] <= -1 ? this : new BiomeInfo<T>(this.weight, this.baseLayerRange, this.impulse, this.value, new int[]{this.count[0]});
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
				biomes.add(new BiomeInfo<String>(weight, new BaseLayerRange(new LayerRange(minTemp, maxTemp), random.nextBoolean() ? new LayerRange(minWet, maxWet) : null, null, null), new LayerRange(minImpulse, maxImpulse), String.format("%s-%s", name, j), 1 + random.nextInt(3)));
			}
		}

		return new BiomeLayer<>(generator, "void", 11, biomes);
	}

}
