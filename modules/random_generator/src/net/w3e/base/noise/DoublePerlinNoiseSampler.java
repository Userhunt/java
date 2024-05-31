package net.w3e.base.noise;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.w3e.base.collection.CollectionBuilder;
import net.w3e.base.random.IRandom;

import java.util.List;

public class DoublePerlinNoiseSampler {
	private static final double DOMAIN_SCALE = 1.0181268882175227;
	private final double amplitude;
	private final OctavePerlinNoiseSampler firstSampler;
	private final OctavePerlinNoiseSampler secondSampler;
	private final double maxValue;
	private final NoiseParameters parameters;

	@Deprecated
	public static DoublePerlinNoiseSampler createLegacy(IRandom random, NoiseParameters parameters) {
		return new DoublePerlinNoiseSampler(random, parameters, false);
	}

	public static DoublePerlinNoiseSampler create(IRandom random, int offset, double ... octaves) {
		return DoublePerlinNoiseSampler.create(random, new NoiseParameters(offset, new DoubleArrayList(octaves)));
	}

	public static DoublePerlinNoiseSampler create(IRandom random, NoiseParameters parameters) {
		return new DoublePerlinNoiseSampler(random, parameters, true);
	}

	@SuppressWarnings("deprecation")
	private DoublePerlinNoiseSampler(IRandom random, NoiseParameters parameters, boolean modern) {
		int i = parameters.firstOctave;
		DoubleList doubleList = parameters.amplitudes;
		this.parameters = parameters;
		if (modern) {
			this.firstSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
			this.secondSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
		} else {
			this.firstSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
			this.secondSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
		}
		int j = Integer.MAX_VALUE;
		int k = Integer.MIN_VALUE;
		DoubleListIterator doubleListIterator = doubleList.iterator();
		while (doubleListIterator.hasNext()) {
			int l = doubleListIterator.nextIndex();
			double d = doubleListIterator.nextDouble();
			if (d == 0.0) continue;
			j = Math.min(j, l);
			k = Math.max(k, l);
		}
		this.amplitude = 0.16666666666666666 / DoublePerlinNoiseSampler.createAmplitude(k - j);
		this.maxValue = (this.firstSampler.getMaxValue() + this.secondSampler.getMaxValue()) * this.amplitude;
	}

	public double getMaxValue() {
		return this.maxValue;
	}

	private static double createAmplitude(int octaves) {
		return 0.1 * (1.0 + 1.0 / (double)(octaves + 1));
	}

	public double sample(double x, double y, double z) {
		double d = x * DOMAIN_SCALE;
		double e = y * DOMAIN_SCALE;
		double f = z * DOMAIN_SCALE;
		return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(d, e, f)) * this.amplitude;
	}

	public NoiseParameters copy() {
		return this.parameters;
	}

	public record NoiseParameters(int firstOctave, DoubleList amplitudes) {

		public NoiseParameters(int firstOctave, List<Double> amplitudes) {
			this(firstOctave, new DoubleArrayList(amplitudes));
		}

		public NoiseParameters(int firstOctave, double firstAmplitude, double... amplitudes) {
			this(firstOctave, CollectionBuilder.doubleList().add(firstAmplitude).add(amplitudes).build());
		}
	}
}

