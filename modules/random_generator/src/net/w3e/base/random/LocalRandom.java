package net.w3e.base.random;

/**
 * A local random, not intended to be shared across threads.
 */
public class LocalRandom extends BaseRandom {
	private long seed;
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public LocalRandom(long seed) {
		this.setSeed(seed);
	}

	@Override
	public final IRandom split() {
		return new LocalRandom(this.nextLong());
	}

	@Override
	public final RandomSplitter nextSplitter() {
		return new CheckedRandom.Splitter(this.nextLong());
	}

	@Override
	public final void setSeed(long seed) {
		this.seed = (seed ^ 0x5DEECE66DL) & SEED_MASK;
		this.gaussianGenerator.reset();
	}

	@Override
	public final int next(int bits) {
		long l;
		this.seed = l = this.seed * MULTIPLIER + INCREMENT & SEED_MASK;
		return (int)(l >> INT_BITS - bits);
	}

	@Override
	public final double nextGaussian() {
		return this.gaussianGenerator.next();
	}
}

