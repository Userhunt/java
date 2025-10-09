package net.w3e.wlib.dungeon.layers.terra.noise;

import java.lang.reflect.Type;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.DeserializeBuilder;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.io.json.elements.JsonString;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.misc.noise.Noise;
import net.w3e.wlib.mat.OpenSimplex2S;
import net.w3e.wlib.mat.WMatUtil;

@AllArgsConstructor
@RequiredArgsConstructor
@DefaultCodec(NoiseData.NoiseDataJsonAdapter.class)
public class NoiseData implements Cloneable {

	public final int min;
	public final int max;
	public final int defValue;
	public final double scale;
	private final Mode mode;

	private transient Object data;

	public static final NoiseData INSTANCE = new NoiseData(0, 100, 0, 1d / 8, Mode.OPENSIMPLEX2S);

	@Override
	public final NoiseData clone() {
		return new NoiseData(this.min, this.max, this.defValue, this.scale, this.mode, this.data);
	}

	public final float toRange(float value) {
		return WMatUtil.mapRange(value, this.mode.min(), 1, this.min, this.max);
	}

	public final void setup(long seed) {
		this.data = this.mode.setup(this, seed);
	}

	public float generate(long seed, double x, double y, double z) {
		return this.mode.generate(this, seed, x, y, z);
	}

	public enum Mode {
		OPENSIMPLEX2S() {
			@Override
			public int min() {
				return -1;
			}
			@Override
			public float generate(NoiseData data, long seed, double x, double y, double z) {
				return OpenSimplex2S.noise3_ImproveXZ(seed, x, y, z);
			}
		},
		SKDS() {
			@Override
			public int min() {
				return 0;
			}
			@Override
			public float generate(NoiseData data, long seed, double x, double y, double z) {
				double d = 2;
				return ((Noise)data.data).getValueInPoint(x * d, y * d, z * d);
			}
			@Override
			public Object buildData(JsonObject data) {
				return SosisonUtils.parseJson(data, NoiseSKDSBuilder.class);
			}
			@Override
			public Object setup(NoiseData data, long seed) {
				return ((NoiseSKDSBuilder)data.data).build(seed);
			}
		},
		;

		public abstract int min();

		public abstract float generate(NoiseData data, long seed, double x, double y, double z);

		public Object buildData(JsonObject data) {
			return null;
		}

		public Object setup(NoiseData data, long seed) {
			return null;
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	public static class NoiseSKDSBuilder implements DeserializeBuilder<NoiseSKDSBuilder> {
		private NoiseAmplitude amplitude = NoiseAmplitude.EXPONENT;
		private NoiseInterpolation interpolation = NoiseInterpolation.COS;

		public NoiseSKDSBuilder setAmplitude(NoiseAmplitude amplitude) {
			this.amplitude = amplitude;
			return this;
		}

		public NoiseSKDSBuilder setInterpolation(NoiseInterpolation interpolation) {
			this.interpolation = interpolation;
			return this;
		}

		@Override
		public NoiseSKDSBuilder build() {
			return new NoiseSKDSBuilder(this.amplitude, this.interpolation);
		}

		public Noise build(long seed) {
			return new Noise(seed, 1, this.amplitude.function, 2, this.interpolation.function);
		}

		public JsonObject getAsJson() {
			JsonObject json = new JsonObject();
			json.put("amplitude", new JsonString(amplitude.name()));
			json.put("interpolation", new JsonString(interpolation.name()));
			return json;
		}
	}

	@AllArgsConstructor
	public enum NoiseAmplitude {
		EXPONENT(Noise.AmplitudeFunction.EXPONENT),
		SQUARE(Noise.AmplitudeFunction.SQUARE),
		LINEAR(Noise.AmplitudeFunction.LINEAR),
		FIBONACCI(Noise.AmplitudeFunction.FIBONACCI),
		;

		public final Noise.AmplitudeFunction function;
	}

	@AllArgsConstructor
	public enum NoiseInterpolation {
		COS(FastMath::cosInterpolate),
		LERP(FastMath::lerp),
		NEAREST(FastMath::nearest),
		;

		public final FastMath.FloatInterpolation function;
	}

	static class NoiseDataJsonAdapter extends ReflectiveBuilderCodec<NoiseLayer> {

		public NoiseDataJsonAdapter(Type type, CodecRegistry registry) {
			super(type, NoiseDataBuilder.class, registry);
		}
	}

	public static class NoiseDataBuilder implements DeserializeBuilder<NoiseData> {
		private int min = NoiseData.INSTANCE.min;
		private int max = NoiseData.INSTANCE.max;
		private int defValue = NoiseData.INSTANCE.defValue;
		private double scale = NoiseData.INSTANCE.scale;
		private Mode mode = Mode.OPENSIMPLEX2S;
		private JsonObject data = new JsonObject();

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

		public final NoiseDataBuilder setDefValue(int value) {
			this.defValue = value;
			return this;
		}

		public final NoiseDataBuilder generateDefValue() {
			this.defValue = FastMath.round(((double)this.max - this.min) / 2 + this.min);
			return this;
		}

		public final NoiseDataBuilder setScale(double scale) {
			if (scale < 0) {
				scale = -1d / scale;
			}
			this.scale = scale;
			return this;
		}

		public final NoiseDataBuilder setMode(Mode mode) {
			this.mode = mode;
			return this;
		}

		public final NoiseDataBuilder setModeData(JsonObject data) {
			this.data = data;
			return this;
		}

		@Override
		public final NoiseData build() {
			return new NoiseData(this.min, this.max, this.defValue, this.scale, this.mode, this.mode.buildData(this.data));
		}
	}
}
