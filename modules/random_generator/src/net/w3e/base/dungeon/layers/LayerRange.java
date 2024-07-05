package net.w3e.base.dungeon.layers;

import java.util.Random;

public record LayerRange(int min, int max) implements Comparable<LayerRange> {
	public final boolean notValid() {
		return this.max < this.min;
	}

	public final int range() {
		return this.max - min;
	}

	public final int random(Random random) {
		return random.nextInt(this.range()) + this.min;
	}

	public final boolean test(int value) {
		return value == Integer.MIN_VALUE || (value >= this.min && value <= this.max);
	}

	@Override
	public final int compareTo(LayerRange range) {
		return Integer.compare(this.min, range.min);
	}
}

