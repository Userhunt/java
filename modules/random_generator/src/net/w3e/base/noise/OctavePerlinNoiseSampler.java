package net.w3e.base.noise;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.random.IRandom;
import net.w3e.base.random.IRandom.RandomSplitter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Nullable;

public class OctavePerlinNoiseSampler {
	private final PerlinNoiseSampler[] octaveSamplers;
	private final int firstOctave;
	private final DoubleList amplitudes;
	private final double persistence;
	private final double lacunarity;
	private final double maxValue;

	@Deprecated
	public static OctavePerlinNoiseSampler createLegacy(IRandom random, IntStream octaves) {
		return new OctavePerlinNoiseSampler(random, OctavePerlinNoiseSampler.calculateAmplitudes(new IntRBTreeSet(octaves.boxed().collect(ImmutableList.toImmutableList()))), false);
	}

	@Deprecated
	public static OctavePerlinNoiseSampler createLegacy(IRandom random, int offset, DoubleList amplitudes) {
		return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes), false);
	}

	public static OctavePerlinNoiseSampler create(IRandom random, IntStream octaves) {
		return OctavePerlinNoiseSampler.create(random, octaves.boxed().collect(ImmutableList.toImmutableList()));
	}

	public static OctavePerlinNoiseSampler create(IRandom random, List<Integer> octaves) {
		return new OctavePerlinNoiseSampler(random, OctavePerlinNoiseSampler.calculateAmplitudes(new IntRBTreeSet(octaves)), true);
	}

	public static OctavePerlinNoiseSampler create(IRandom random, int offset, double firstAmplitude, double ... amplitudes) {
		DoubleArrayList doubleArrayList = new DoubleArrayList(amplitudes);
		doubleArrayList.add(0, firstAmplitude);
		return new OctavePerlinNoiseSampler(random, Pair.of(offset, doubleArrayList), true);
	}

	public static OctavePerlinNoiseSampler create(IRandom random, int offset, DoubleList amplitudes) {
		return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes), true);
	}

	private static Pair<Integer, DoubleList> calculateAmplitudes(IntSortedSet octaves) {
		if (octaves.isEmpty()) {
			throw new IllegalArgumentException("Need some octaves!");
		}
		int i = -octaves.firstInt();
		int k = i + octaves.lastInt() + 1;
		if (k < 1) {
			throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
		}
		DoubleArrayList doubleList = new DoubleArrayList(new double[k]);
		IntBidirectionalIterator intBidirectionalIterator = octaves.iterator();
		while (intBidirectionalIterator.hasNext()) {
			int l = intBidirectionalIterator.nextInt();
			doubleList.set(l + i, 1.0);
		}
		return Pair.of(-i, doubleList);
	}

	protected OctavePerlinNoiseSampler(IRandom random, Pair<Integer, DoubleList> firstOctaveAndAmplitudes, boolean xoroshiro) {
		this.firstOctave = firstOctaveAndAmplitudes.first();
		this.amplitudes = firstOctaveAndAmplitudes.second();
		int i = this.amplitudes.size();
		int j = -this.firstOctave;
		this.octaveSamplers = new PerlinNoiseSampler[i];
		if (xoroshiro) {
			RandomSplitter randomSplitter = random.nextSplitter();
			for (int k = 0; k < i; ++k) {
				if (this.amplitudes.getDouble(k) == 0.0) continue;
				int l = this.firstOctave + k;
				this.octaveSamplers[k] = new PerlinNoiseSampler(randomSplitter.split("octave_" + l));
			}
		} else {
			PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(random);
			if (j >= 0 && j < i && this.amplitudes.getDouble(j) != 0.0) {
				this.octaveSamplers[j] = perlinNoiseSampler;
			}
			for (int k = j - 1; k >= 0; --k) {
				if (k < i) {
					double e = this.amplitudes.getDouble(k);
					if (e != 0.0) {
						this.octaveSamplers[k] = new PerlinNoiseSampler(random);
						continue;
					}
					OctavePerlinNoiseSampler.skipCalls(random);
					continue;
				}
				OctavePerlinNoiseSampler.skipCalls(random);
			}
			if (Arrays.stream(this.octaveSamplers).filter(Objects::nonNull).count() != this.amplitudes.doubleStream().filter(amplitude -> amplitude != 0.0).count()) {
				throw new IllegalStateException("Failed to create correct number of noise levels for given non-zero amplitudes");
			}
			if (j < i - 1) {
				throw new IllegalArgumentException("Positive octaves are temporarily disabled");
			}
		}
		this.lacunarity = Math.pow(2.0, -j);
		this.persistence = Math.pow(2.0, i - 1) / (Math.pow(2.0, i) - 1.0);
		this.maxValue = this.getTotalAmplitude(2.0);
	}

	protected double getMaxValue() {
		return this.maxValue;
	}

	private static void skipCalls(IRandom random) {
		random.skip(262);
	}

	public double sample(double x, double y, double z) {
		return this.sample(x, y, z, 0.0, 0.0, false);
	}

	@Deprecated
	public double sample(double x, double y, double z, double yScale, double yMax, boolean useOrigin) {
		double d = 0.0;
		double e = this.lacunarity;
		double f = this.persistence;
		for (int i = 0; i < this.octaveSamplers.length; ++i) {
			PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
			if (perlinNoiseSampler != null) {
				double g = perlinNoiseSampler.sample(OctavePerlinNoiseSampler.maintainPrecision(x * e), useOrigin ? -perlinNoiseSampler.originY : OctavePerlinNoiseSampler.maintainPrecision(y * e), OctavePerlinNoiseSampler.maintainPrecision(z * e), yScale * e, yMax * e);
				d += this.amplitudes.getDouble(i) * g * f;
			}
			e *= 2.0;
			f /= 2.0;
		}
		return d;
	}

	public double method_40556(double d) {
		return this.getTotalAmplitude(d + 2.0);
	}

	private double getTotalAmplitude(double scale) {
		double d = 0.0;
		double e = this.persistence;
		for (int i = 0; i < this.octaveSamplers.length; ++i) {
			PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
			if (perlinNoiseSampler != null) {
				d += this.amplitudes.getDouble(i) * scale * e;
			}
			e /= 2.0;
		}
		return d;
	}

	@Nullable
	public PerlinNoiseSampler getOctave(int octave) {
		return this.octaveSamplers[this.octaveSamplers.length - 1 - octave];
	}

	public static double maintainPrecision(double value) {
		return value - (double)BMatUtil.roundLong(value / 3.3554432E7 + 0.5) * 3.3554432E7;
	}

	protected int getFirstOctave() {
		return this.firstOctave;
	}

	protected DoubleList getAmplitudes() {
		return this.amplitudes;
	}
}

