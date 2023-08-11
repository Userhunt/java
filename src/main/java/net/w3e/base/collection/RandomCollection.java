package net.w3e.base.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	public final RandomCollection<T> add(float weight, T object) {
		this.total += weight;
		return add(new Entry<>(object, this.total, weight));
	}

	public final RandomCollection<T> add(int weight, T object) {
		this.total += weight;
		return add(new Entry<>(object, this.total, weight));
	}

	protected final RandomCollection<T> add(Entry<T> entry) {
		this.entries.add(entry);
		return this;
	}

	protected static interface FloatSupplier {
		float getAsFloat();
	}

	/*private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
	private final DoubleSupplier random;
	private double total = 0;

	public RandomCollection() {
		this(new Random());
	}

	public RandomCollection(Random random) {
		this.random = random::nextDouble;
	}

	public RandomCollection(DoubleSupplier random) {
		this.random = random;
	}

	public final RandomCollection<E> add(int weight, E entry) {
		return add((double)weight, entry);
	}

	public final RandomCollection<E> add(double weight, E entry) {
		if (weight <= 0) return this;
		this.total += weight;
		map.put(this.total, entry);
		return this;
	}

	public final E next() {
		double value = random.getAsDouble() * total;
		return map.higherEntry(value).getValue();
	}

	public final E remove() {
		E next = next();
		remove(next);
		return next;
	}

	public final boolean remove(E remove) {
		boolean find = false;
		for (E e : this.map.values()) {
			if (e == remove) {
				find = true;
			}
		}
		if (!find) {
			return false;
		}

		this.total = 0;
		Map<Double, E> save = new LinkedHashMap<>(this.map);
		this.map.clear();

		double prev = 0;
		for (Map.Entry<Double, E> entry : save.entrySet()) {
			double key = entry.getKey();
			double weight = key - prev;
			prev += weight;
			E value = entry.getValue();
			if (value != remove) {
				add(weight, remove);
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}*/
}
