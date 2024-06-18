package net.w3e.base.random;

import java.util.random.RandomGenerator;

public abstract class IRandom implements RandomGenerator {

	protected static final float FLOAT_MULTIPLIER = 5.9604645E-8f;
	protected static final double DOUBLE_MULTIPLIER = (double)1.110223E-16f;

	public void skip(int count) {
		for (int i = 0; i < count; ++i) {
			this.nextInt();
		}
	}

	@Override
	public abstract int nextInt();
	@Override
	public abstract long nextLong();

	public IRandom split() {
		throw new UnsupportedOperationException("Unimplemented method 'split'");
	}

	public RandomSplitter nextSplitter() {
		throw new UnsupportedOperationException("Unimplemented method 'nextSplitter'");
	}

	public void setSeed(long seed) {
		throw new UnsupportedOperationException("Unimplemented method 'setSeed'");
	}

	public abstract static class RandomSplitter {
		public abstract IRandom split(String string);

		public abstract IRandom split(int x, int y, int z);
	}
}
