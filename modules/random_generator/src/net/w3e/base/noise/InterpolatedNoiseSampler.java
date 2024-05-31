/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package net.w3e.base.noise;

import net.w3e.base.math.BMatUtil;
import net.w3e.base.random.IRandom;
import net.w3e.base.random.Xoroshiro128PlusPlusRandom;

import java.util.stream.IntStream;

public class InterpolatedNoiseSampler {
	private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
	private final OctavePerlinNoiseSampler upperInterpolatedNoise;
	private final OctavePerlinNoiseSampler interpolationNoise;
	private final double scaledXzScale;
	private final double scaledYScale;
	private final double xzFactor;
	private final double yFactor;
	private final double smearScaleMultiplier;
	private final double maxValue;
	private final double xzScale;
	private final double yScale;

	public static InterpolatedNoiseSampler createBase3dNoiseFunction(double xzScale, double yScale, double xzFactor, double yFactor, double smearScaleMultiplier) {
		return new InterpolatedNoiseSampler(new Xoroshiro128PlusPlusRandom(0L), xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier);
	}

	public InterpolatedNoiseSampler(OctavePerlinNoiseSampler lowerInterpolatedNoise, OctavePerlinNoiseSampler upperInterpolatedNoise, OctavePerlinNoiseSampler interpolationNoise, double xzScale, double yScale, double xzFactor, double yFactor, double smearScaleMultiplier) {
		this.lowerInterpolatedNoise = lowerInterpolatedNoise;
		this.upperInterpolatedNoise = upperInterpolatedNoise;
		this.interpolationNoise = interpolationNoise;
		this.xzScale = xzScale;
		this.yScale = yScale;
		this.xzFactor = xzFactor;
		this.yFactor = yFactor;
		this.smearScaleMultiplier = smearScaleMultiplier;
		this.scaledXzScale = 684.412 * this.xzScale;
		this.scaledYScale = 684.412 * this.yScale;
		this.maxValue = lowerInterpolatedNoise.method_40556(this.scaledYScale);
	}

	@SuppressWarnings("deprecation")
	public InterpolatedNoiseSampler(IRandom random, double xzScale, double yScale, double xzFactor, double yFactor, double smearScaleMultiplier) {
		this(OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)), OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-15, 0)), OctavePerlinNoiseSampler.createLegacy(random, IntStream.rangeClosed(-7, 0)), xzScale, yScale, xzFactor, yFactor, smearScaleMultiplier);
	}

	public final InterpolatedNoiseSampler copyWithRandom(IRandom random) {
		return new InterpolatedNoiseSampler(random, this.xzScale, this.yScale, this.xzFactor, this.yFactor, this.smearScaleMultiplier);
	}

	public record NoisePos(int blockX, int blockY, int blockZ) {}

	@SuppressWarnings("deprecation")
	public final double sample(NoisePos pos) {
		double d = (double)pos.blockX() * this.scaledXzScale;
		double e = (double)pos.blockY() * this.scaledYScale;
		double f = (double)pos.blockZ() * this.scaledXzScale;
		double g = d / this.xzFactor;
		double h = e / this.yFactor;
		double i = f / this.xzFactor;
		double j = this.scaledYScale * this.smearScaleMultiplier;
		double k = j / this.yFactor;
		double l = 0.0;
		double m = 0.0;
		double n = 0.0;
		double o = 1.0;
		for (int p = 0; p < 8; ++p) {
			PerlinNoiseSampler perlinNoiseSampler = this.interpolationNoise.getOctave(p);
			if (perlinNoiseSampler != null) {
				n += perlinNoiseSampler.sample(OctavePerlinNoiseSampler.maintainPrecision(g * o), OctavePerlinNoiseSampler.maintainPrecision(h * o), OctavePerlinNoiseSampler.maintainPrecision(i * o), k * o, h * o) / o;
			}
			o /= 2.0;
		}
		double q = (n / 10.0 + 1.0) / 2.0;
		boolean bl2 = q >= 1.0;
		boolean bl3 = q <= 0.0;
		o = 1.0;
		for (int r = 0; r < 16; ++r) {
			PerlinNoiseSampler perlinNoiseSampler2;
			double s = OctavePerlinNoiseSampler.maintainPrecision(d * o);
			double t = OctavePerlinNoiseSampler.maintainPrecision(e * o);
			double u = OctavePerlinNoiseSampler.maintainPrecision(f * o);
			double v = j * o;
			if (!bl2 && (perlinNoiseSampler2 = this.lowerInterpolatedNoise.getOctave(r)) != null) {
				l += perlinNoiseSampler2.sample(s, t, u, v, e * o) / o;
			}
			if (!bl3 && (perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(r)) != null) {
				m += perlinNoiseSampler2.sample(s, t, u, v, e * o) / o;
			}
			o /= 2.0;
		}
		return BMatUtil.clampedLerp(l / 512.0, m / 512.0, q) / 128.0;
	}

	public final double minValue() {
		return -this.maxValue();
	}

	public final double maxValue() {
		return this.maxValue;
	}

	public static final InterpolatedNoiseSampler noise_overworld() {
		return noise_overworld(new Xoroshiro128PlusPlusRandom(0L));
	}

	public static final InterpolatedNoiseSampler noise_nether() {
		return noise_nether(new Xoroshiro128PlusPlusRandom(0L));
	}
	
	public static final InterpolatedNoiseSampler noise_end() {
		return noise_end(new Xoroshiro128PlusPlusRandom(0L));
	}

	public static final InterpolatedNoiseSampler noise_overworld(IRandom random) {
		return new InterpolatedNoiseSampler(random, 0.25, 0.125, 80.0, 160.0, 8.0);
	}

	public static final InterpolatedNoiseSampler noise_nether(IRandom random) {
		return new InterpolatedNoiseSampler(random, 0.25, 0.375, 80.0, 60.0, 8.0);
	}
	
	public static final InterpolatedNoiseSampler noise_end(IRandom random) {
		return new InterpolatedNoiseSampler(random, 0.25, 0.25, 80.0, 160.0, 4.0);
	}
}

