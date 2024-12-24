package net.w3e.wlib.dungeon.layers;

import java.util.Random;

public record LayerRange(int min, int max, int range) implements Comparable<LayerRange> {

	public static final LayerRange ONE = new LayerRange(1, 1, 0);

	public LayerRange(int min, int max) {
		this(min, max, max - min);
	}
	public final boolean notValid() {
		return this.max < this.min;
	}
	public final boolean notValid(int min, int max) {
		return this.max < this.min || this.min < min || this.max > max;
	}

	/*public final int range() {
		return this.max - min;
	}*/

	public final int random(Random random) {
		int range = this.range();
		if (range == 0) {
			return this.min;
		}
		return random.nextInt(range) + this.min;
	}

	public final boolean test(int value) {
		return value == Integer.MIN_VALUE || (value >= this.min && value <= this.max);
	}

	@Override
	public final int compareTo(LayerRange range) {
		return Integer.compare(this.min, range.min);
	}

	public static final LayerRange randomize(Random random, int min, int max) {
		LayerRange range = new LayerRange(min, max);
		if (range.notValid()) {
			return new LayerRange(0, 0);
		}
		int a = range.random(random);
		int b = range.random(random);
		return new LayerRange(Math.min(a, b), Math.max(a, b));
	}
}

