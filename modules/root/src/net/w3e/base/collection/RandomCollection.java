package net.w3e.base.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;

public class RandomCollection<T> {

	protected static record Entry<T>(T object, float r, float w) {}

	private final List<Entry<T>> entries = new ArrayList<>();
	private final FloatSupplier random;
	private float total;

	public RandomCollection() {
		this(new Random());
	}

	public RandomCollection(Random random) {
		this(random::nextFloat);
	}

	public RandomCollection(FloatSupplier random) {
		this.random = random;
	}

	public static final <T> RandomCollection<T> create(DoubleSupplier random) {
		return new RandomCollection<>(() -> (float)random.getAsDouble());
	}

	public final T getRandom() {
		float r = this.random.getAsFloat() * this.total;
			for (Entry<T> entry: entries) {
				if (entry.r() >= r) {
					return entry.object;
				}
			}
		return null;
	}

	public final T remove() {
		T next = getRandom();
		remove(next);
		return next;
	}

	public final boolean remove(T remove) {
		boolean find = false;
		for (Entry<T> e : this.entries) {
			if (e.object == remove) {
				find = true;
			}
		}
		if (!find) {
			return false;
		}

		this.total = 0;
		List<Entry<T>> list = new ArrayList<>();
		this.entries.clear();

		for (Entry<T> entry : list) {
			if (entry.object != remove) {
				add(entry.w, entry.object);
			}
		}

		return true;
	}

	public final RandomCollection<T> add(double weight, T object) {
		return this.add((float)weight, object);
	}

	public final RandomCollection<T> add(float weight, T object) {
		this.total += weight;
		return this.add(new Entry<>(object, this.total, weight));
	}

	public final RandomCollection<T> add(int weight, T object) {
		this.total += weight;
		return this.add(new Entry<>(object, this.total, weight));
	}

	protected final RandomCollection<T> add(Entry<T> entry) {
		this.entries.add(entry);
		return this;
	}

	@FunctionalInterface
	public static interface FloatSupplier {
		float getAsFloat();
	}
}
