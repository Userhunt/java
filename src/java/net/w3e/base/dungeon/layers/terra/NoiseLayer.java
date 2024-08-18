package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonException;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.OpenSimplex2S;
import net.w3e.base.math.vector.i.WVector3I;

public abstract class NoiseLayer extends TerraLayer<Integer> {

	protected final NoiseData noise;
	private long seed;

	public NoiseLayer(DungeonGenerator generator, NoiseData noies) {
		this(generator, noies, 50);
	}

	public NoiseLayer(DungeonGenerator generator, NoiseData noise, int stepRate) {
		super(generator, noise.defKey, noise.defValue, stepRate);
		this.noise = noise;
	}

	@Override
	public abstract NoiseLayer withDungeonImpl(DungeonGenerator generator);

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		this.seed = this.random().nextLong();
		super.regenerate(composite);
	}

	@Override
	public final void generate(DungeonRoomInfo room) throws DungeonException {
		WVector3I pos = room.pos();
		double scale = this.noise.scale();
		double x = pos.getXI() * scale;
		double y = pos.getYI() * scale;
		double z = pos.getZI() * scale;
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
			return BMatUtil.toRange(value, -1, 1, this.min, this.max);
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
			this.defValue = BMatUtil.round(((double)this.max - this.min) / 2 + this.min);
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

	@SuppressWarnings({"FieldMayBeFinal"})
	public abstract static class NoiseLayerData<T extends NoiseLayer> implements ILayerAdapter<T> {
		private NoiseData noise = NoiseData.INSTANCE;
		private int stepRate = 50;
		@Override
		public final T withDungeon(DungeonGenerator generator) {
			this.nonNull("noise", this.noise);
			lessThan("stepRate", this.stepRate);
			return this.withDungeon(generator, this.noise, this.stepRate);
		}
		protected abstract T withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate);
	}
}
