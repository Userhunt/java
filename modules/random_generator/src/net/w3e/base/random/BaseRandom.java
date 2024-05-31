package net.w3e.base.random;

public abstract class BaseRandom extends IRandom {

	protected static final int INT_BITS = 48;
	protected static final long SEED_MASK = 0xFFFFFFFFFFFFL;
	protected static final long MULTIPLIER = 25214903917L;
	protected static final long INCREMENT = 11L;

	public abstract int next(int var1);

	@Override
	public final int nextInt() {
		return this.next(32);
	}

	@Override
	public final int nextInt(int bound) {
		int j;
		int i;
		if (bound <= 0) {
			throw new IllegalArgumentException("Bound must be positive");
		}
		if ((bound & bound - 1) == 0) {
			return (int)((long)bound * (long)this.next(31) >> 31);
		}
		while ((i = this.next(31)) - (j = i % bound) + (bound - 1) < 0) {
		}
		return j;
	}

	@Override
	public final long nextLong() {
		int i = this.next(32);
		int j = this.next(32);
		long l = (long)i << 32;
		return l + (long)j;
	}

	@Override
	public final boolean nextBoolean() {
		return this.next(1) != 0;
	}

	@Override
	public final float nextFloat() {
		return (float)this.next(24) * FLOAT_MULTIPLIER;
	}

	@Override
	public final double nextDouble() {
		int i = this.next(26);
		int j = this.next(27);
		long l = ((long)i << 27) + (long)j;
		return (double)l * DOUBLE_MULTIPLIER;
	}
}
