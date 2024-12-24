package net.w3e.wlib.dungeon.layers.terra;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import lombok.AllArgsConstructor;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.Vec3I;
import net.w3e.lib.mat.OpenSimplex2S;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.json.ILayerDeserializerAdapter;
import net.w3e.wlib.mat.WMatUtil;

public abstract class NoiseLayer extends TerraLayer<Integer> {

	protected final NoiseData noise;
	private transient long seed;

	public NoiseLayer(DungeonGenerator generator, NoiseData noies, boolean fast) {
		this(generator, noies, 50, fast);
	}

	public NoiseLayer(DungeonGenerator generator, NoiseData noise, int stepRate, boolean fast) {
		super(generator, noise.defKey, noise.defValue, stepRate, fast);
		this.noise = noise;
	}

	@Override
	public abstract NoiseLayer withDungeon(DungeonGenerator generator);

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		this.seed = this.random().nextLong();
		super.regenerate(composite);
	}

	@Override
	public final void generate(DungeonRoomInfo room) throws DungeonException {
		Vec3I pos = room.pos();
		double scale = this.noise.scale();
		double x = pos.xi() * scale;
		double y = pos.yi() * scale;
		double z = pos.zi() * scale;
		float noise = OpenSimplex2S.noise3_ImproveXZ(this.seed, x, y, z);
		noise = this.noise.toRange(noise);
		noise = this.modify(noise);
		room.data().put(this.defKey, noise);
	}

	protected float modify(float value) {
		return value;
	}

	public static record NoiseData(int min, int max, double scale, String defKey, int defValue) {

		public static final NoiseData INSTANCE = new NoiseData(0, 100, 1d / 8, null, 0);

		public final NoiseData withKey(String defKey) {
			return new NoiseData(this.min, this.max, this.scale, defKey, this.defValue);
		}

		private final float toRange(float value) {
			return WMatUtil.mapRange(value, -1, 1, this.min, this.max);
		}
	}

	public static class NoiseDataBuilder {
		private int min = NoiseData.INSTANCE.min;
		private int max = NoiseData.INSTANCE.max;
		private double scale = NoiseData.INSTANCE.scale;
		private String defKey;
		private int defValue = NoiseData.INSTANCE.defValue;

		public final NoiseDataBuilder setMin(int min) {
			this.min = min;
			return this;
		}

		public final NoiseDataBuilder setMax(int max) {
			this.max = max;
			return this;
		}

		public final NoiseDataBuilder setMinMax(int min, int max) {
			this.min = min;
			this.max = max;
			return this;
		}

		public final NoiseDataBuilder generateDefValue() {
			this.defValue = FastMath.round(((double)this.max - this.min) / 2 + this.min);
			return this;
		}

		public final NoiseDataBuilder setDefKey(String key) {
			this.defKey = key;
			return this;
		}

		public final NoiseDataBuilder setScale(double scale) {
			if (scale < 0) {
				scale = -1d / scale;
			}
			this.scale = scale;
			return this;
		}

		public final NoiseData build() {
			return new NoiseData(this.min, this.max, this.scale, this.defKey, this.defValue);
		}
	}

	@AllArgsConstructor
	public abstract static class NoiseLayerAdapter<D extends NoiseLayerData<? extends NoiseLayer>> implements ILayerDeserializerAdapter<D, NoiseLayer> {

		private final Class<D> dataClass;

		@Override
		public final NoiseLayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			D data = context.deserialize(json, this.dataClass);
			this.nonNull("noise", data.noise);
			this.lessThan("stepRate", data.stepRate);

			return deserialize(data, context).withDungeon(null);
		}
	}

	protected abstract static class NoiseLayerData<T extends NoiseLayer> implements ILayerData<T> {
		protected NoiseData noise = NoiseData.INSTANCE;
		protected int stepRate = 50;
		protected boolean fast = false;
		@Override
		public final T withDungeon(DungeonGenerator generator) {
			return this.withDungeon(generator, this.noise, this.stepRate, this.fast);
		}

		protected abstract T withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate, boolean fast);
	}
}
