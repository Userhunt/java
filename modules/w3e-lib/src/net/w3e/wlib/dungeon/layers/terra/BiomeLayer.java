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

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import lombok.AllArgsConstructor;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.Vec3I;
import net.skds.lib2.utils.Holders.ObjectHolder;
import net.w3e.lib.utils.collection.CollectionBuilder;
import net.w3e.wlib.collection.RandomCollection;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.json.ILayerDeserializerAdapter;
import net.w3e.wlib.dungeon.layers.ISetupLayer;
import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.ListLayer;
import net.w3e.wlib.dungeon.layers.interfaces.DungeonInfoCountHolder;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLayerProgress;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.dungeon.layers.roomvalues.AbstractLayerRoomValues;
import net.w3e.wlib.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.wlib.log.LogUtil;

public class BiomeLayer<T> extends ListLayer<BiomeLayer.BiomePoint<T>> implements ISetupLayer {

	public static final String TYPE = "terra/biome";

	public static final String KEY = "biome";

	private final List<BiomeInfo<T>> biomes = new ArrayList<>();
	private final transient List<BiomeInfo<T>> workList = new ArrayList<>();
	private transient Progress progress = Progress.createPoint;

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
		this.biomes.addAll(biomes);
		this.biomes.removeIf(BiomeInfo::notValid);
	}

	@Override
	public String keyName() {
		return TYPE;
	}

	@Override
	public final BiomeLayer<T> withDungeon(DungeonGenerator generator) {
		return new BiomeLayer<T>(generator, this.def, this.percent, this.biomes);
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
		this.biomes.stream().map(BiomeInfo::copy).forEach(this.workList::add);
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
						point.info.setValue(info);
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
			BiomeInfo<T> info = this.info.getValue();
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

	public static record BiomeInfo<T>(int weight, BaseLayerRoomRange layerRange, LayerRange impulse, T value, DungeonInfoCountHolder count) implements IDungeonLimitedCount {

		public BiomeInfo(int weight, BaseLayerRoomRange layerRange, LayerRange impulse, T value, int count) {
			this(weight, layerRange, impulse, value, new DungeonInfoCountHolder(count));
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
			return this.count.getValue() <= -1 ? this : new BiomeInfo<T>(this.weight, this.layerRange, this.impulse, this.value, this.count.copy());
		}
	}

	@AllArgsConstructor
	public abstract static class BiomeLayerAdapter<D> implements ILayerDeserializerAdapter<BiomeLayerData<D>, BiomeLayer<D>> {

		private final Class<? extends BiomeLayerData<D>> dataClass;

		@SuppressWarnings("unchecked")
		@Override
		public final BiomeLayer<D> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			BiomeLayerData<D> data = context.deserialize(json, this.dataClass);
			this.isEmpty("biomes", data.biomes);
			this.lessThan("percent", data.percent);

			data = deserialize(data, context);
			data.biomesInfo = Stream.of(data.biomes).map(e -> new BiomeInfo<>(e.weight, e.getLayerRange(), e.impulse, e.getValue(), e.count)).toArray(e -> new BiomeInfo[e]);
			return data.withDungeon(null);
		}
	}

	public abstract static class BiomeLayerData<T> implements ILayerData<BiomeLayer<T>> {
		protected int percent = 10;

		@Nullable
		protected abstract T getDef();
		private BiomeInfoData<T>[] biomes;
		private transient BiomeInfo<T>[] biomesInfo;

		@Override
		public final BiomeLayer<T> withDungeon(DungeonGenerator generator) {
			return new BiomeLayer<T>(generator, this.getDef(), this.percent, this.biomesInfo);
		}
	}

	@AllArgsConstructor
	public static class BiomeInfoAdapter<D> implements ILayerDeserializerAdapter<BiomeInfoData<D>, BiomeInfoData<D>> {

		private final Class<? extends BiomeInfoData<D>> dataClass;

		@Override
		public final BiomeInfoData<D> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			BiomeInfoData<D> data = context.deserialize(json, this.dataClass);
			this.nonNull("layerRange", data.getLayerRange());
			if (data.getLayerRange().notValid()) {
				throw new IllegalStateException(LogUtil.ILLEGAL.createMsg("layerRange"));
			}
			this.lessThan("weight", data.weight);
			this.nonNull("impulse", data.impulse);
			if (data.count != -1) {
				this.lessThan("count", data.count);
			}
			D value = data.getValue();
			this.nonNull("value", value);
			return deserialize(data, context);
		}
	}

	public abstract static class BiomeInfoData<T> {
		public int weight = 1;
		public LayerRange impulse = LayerRange.ONE;
		public int count = -1;

		protected abstract T getValue();
		protected abstract BaseLayerRoomRange getLayerRange();
	}

	public static final BiomeLayer<String> example(DungeonGenerator generator) {
		Random random = new Random(0);
		int range = FastMath.round((TemperatureLayer.MAX - TemperatureLayer.MIN) / 5f);
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
