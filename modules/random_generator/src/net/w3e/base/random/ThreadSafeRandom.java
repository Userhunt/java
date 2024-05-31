package net.w3e.base.random;

/**
 * A random that can be shared by multiple threads safely.
 */
public class ThreadSafeRandom extends CheckedRandom {

	public ThreadSafeRandom(long seed) {
		super(seed);
	}

	@Override
	public final IRandom split() {
		return new ThreadSafeRandom(this.nextLong());
	}

	@Override
	public final void setSeed(long seed) {
		this.seed.set((seed ^ 0x5DEECE66DL) & SEED_MASK);
	}

	@Override
	public final int next(int bits) {
		long m;
		long l;
		while (!this.seed.compareAndSet(l = this.seed.get(), m = l * MULTIPLIER + INCREMENT & SEED_MASK)) {
		}
		return (int)(m >>> INT_BITS - bits);
	}
}

