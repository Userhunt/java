package net.w3e.base.generator.collection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.DoubleSupplier;

public class RandomCollection<E> {

	private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
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
	}
}
