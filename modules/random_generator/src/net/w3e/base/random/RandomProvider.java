package net.w3e.base.random;

import java.util.Random;

public class RandomProvider extends IRandom {
	
	protected final Random random;

	public RandomProvider() {
		this(new Random());
	}

	public RandomProvider(long seed) {
		this(new Random(seed));
	}

	public RandomProvider(Random random) {
		this.random = random;
	}

	@Override
	public final int nextInt(int bound) {
		return this.random.nextInt(bound);
	}

	@Override
	public final int nextInt() {
		return this.random.nextInt();
	}

	@Override
	public final double nextDouble() {
		return this.random.nextDouble();
	}

	@Override
	public final long nextLong() {
		return this.random.nextLong();
	}

}
