package net.w3e.base.random;

import java.util.concurrent.atomic.AtomicLong;

import net.w3e.base.math.BMatUtil;

/**
 * A checked random that fails fast when it detects concurrent usage.
 */
public class CheckedRandom extends BaseRandom {
	protected final AtomicLong seed = new AtomicLong();
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public CheckedRandom(long seed) {
		this.setSeed(seed);
	}

	@Override
	public IRandom split() {
		return new CheckedRandom(this.nextLong());
	}

	@Override
	public RandomSplitter nextSplitter() {
		return new Splitter(this.nextLong());
	}

	@Override
	public void setSeed(long seed) {
		if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 0x5DEECE66DL) & SEED_MASK)) {
			throw new IllegalStateException("LegacyRandomSource");
		}
		this.gaussianGenerator.reset();
	}

	@Override
	public int next(int bits) {
		long m;
		long l = this.seed.get();
		if (!this.seed.compareAndSet(l, m = l * MULTIPLIER + INCREMENT & SEED_MASK)) {
			throw new IllegalStateException("LegacyRandomSource");
		}
		return (int)(m >> INT_BITS - bits);
	}

	@Override
	public final double nextGaussian() {
		return this.gaussianGenerator.next();
	}

	public static class Splitter extends RandomSplitter {
		private final long seed;

		public Splitter(long seed) {
			this.seed = seed;
		}

		@Override
		public IRandom split(int x, int y, int z) {
			long l = BMatUtil.hashCode(x, y, z);
			long m = l ^ this.seed;
			return new CheckedRandom(m);
		}

		@Override
		public IRandom split(String seed) {
			int i = seed.hashCode();
			return new CheckedRandom((long)i ^ this.seed);
		}
	}
}

