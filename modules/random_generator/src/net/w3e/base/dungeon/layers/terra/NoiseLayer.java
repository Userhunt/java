package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.OpenSimplex2S;
import net.w3e.base.math.vector.i.WVector3I;

public class NoiseLayer extends TerraLayer<Integer> {

	private final NoiseData data;
	private long seed;

	@Deprecated
	public NoiseLayer(DungeonGenerator generator, String defKey, int min, int max, double scale) {
		this(generator, new NoiseDataBuilder().setMinMax(min, max).setScale(scale).setDefKey(defKey).build(), 50);
	}

	public NoiseLayer(DungeonGenerator generator, NoiseData data) {
		this(generator, data, 50);
	}

	public NoiseLayer(DungeonGenerator generator, NoiseData data, int stepRate) {
		super(generator, data.defKey, data.defValue, stepRate);
		this.data = data;
	}

	@Override
	public final void regenerate(boolean composite) {
		this.seed = this.random().nextLong();
		super.regenerate(composite);
	}

	@Override
	public final void generate(DungeonRoomInfo room) {
		WVector3I pos = room.pos();
		double scale = this.data.scale();
		double x = pos.getXI() * scale;
		double y = pos.getYI() * scale;
		double z = pos.getZI() * scale;
		float noise = OpenSimplex2S.noise3_ImproveXZ(this.seed, x, y, z);
		noise = this.data.toRange(noise);
		noise = this.modify(noise);
		room.data().put(this.defKey, noise);
	}

	protected float modify(float value) {
		return value;
	}

	public static record NoiseData(int min, int max, double scale, String defKey, int defValue) {

		public final NoiseData withKey(String defKey) {
			return new NoiseData(this.min, this.max, this.scale, defKey, this.defValue);
		}

		private final float toRange(float value) {
			return BMatUtil.toRange(value, -1, 1, this.min, this.max);
		}
	}

	public static class NoiseDataBuilder {
		private int min = 0;
		private int max = 100;
		private double scale = this.setScale(-8d).scale;
		private String defKey;
		private int defValue = 0;

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
}
