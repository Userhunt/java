package net.w3e.base.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;

public class RandomCollection<T> {

	protected static record Entry<T>(T object, float[] random, float weight) implements Comparable<Entry<T>> {
		protected Entry(T object, float weight) {
			this(object, new float[1], weight);
		}
		@Override
		public int compareTo(Entry<T> o) {
			return -Float.compare(this.weight, o.weight);
		}
	}

	private final List<Entry<T>> entries = new ArrayList<>();
	private final FloatSupplier random;
	private float total;
	private boolean sort;
	private boolean autoSort;

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
	
	public final int size() {
		return this.entries.size();
	}

	public final RandomCollection<T> autoSort() {
		this.autoSort = true;
		return this;
	}

	public final T getRandom() {
		float r = this.random.getAsFloat() * this.total;
		for (Entry<T> entry : this.entries) {
			if (entry.random()[0] >= r) {
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
		if (remove == null) {
			return false;
		}
		boolean find = false;
		Iterator<Entry<T>> iterator = this.entries.iterator();
		while (iterator.hasNext()) {
			Entry<T> next = iterator.next();
			if (next.object == remove) {
				find = true;
				this.sort = false;
				iterator.remove();
				break;
			}
		}
		if (!find) {
			return false;
		}

		this.sort();

		return true;
	}

	public final RandomCollection<T> add(double weight, T object) {
		return this.add((float)weight, object);
	}

	public final RandomCollection<T> add(float weight, T object) {
		if (weight > 0.0 && object != null) {
			this.total += weight;
			return this.add(new Entry<>(object, weight));
		} else {
			return this;
		}
	}

	public final RandomCollection<T> add(int weight, T object) {
		if (weight > 0 && object != null) {
			this.total += weight;
			return this.add(new Entry<>(object, weight));
		} else {
			return this;
		}
	}

	protected final RandomCollection<T> add(Entry<T> entry) {
		entry.random[0] = this.total;
		this.entries.add(entry);
		this.sort = false;
		return this;
	}

	public final void sort() {
		if (this.sort == true) {
			return;
		}
		this.total = 0;
		if (!this.sort && this.autoSort) {
			Collections.sort(this.entries);
		}

		for (Entry<T> entry : this.entries) {
			this.total += entry.weight;
			entry.random[0] = this.total;
		}
		this.sort = true;
	}

	public final void clear() {
		this.entries.clear();
		this.total = 0;
		this.sort = true;
	}

	@FunctionalInterface
	public static interface FloatSupplier {
		float getAsFloat();
	}
}
