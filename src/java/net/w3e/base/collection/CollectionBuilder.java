package net.w3e.base.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class CollectionBuilder<T, V extends Collection<T>> {

	public static <T> CollectionBuilder<Consumer<T>, ArrayList<Consumer<T>>> listConsumer(Class<T> t) {
		return new CollectionBuilder<Consumer<T>, ArrayList<Consumer<T>>>(new ArrayList<>());
	}

	public static <T, V> CollectionBuilder<BiConsumer<T, V>, ArrayList<BiConsumer<T, V>>> listBiConsumer(Class<T> t, Class<V> v) {
		return new CollectionBuilder<BiConsumer<T, V>, ArrayList<BiConsumer<T, V>>>(new ArrayList<>());
	}

	public static <T> CollectionBuilder<T, ArrayList<T>> list(Class<T> t) {
		return new CollectionBuilder<T, ArrayList<T>>(new ArrayList<>());
	}

	public static <T> CollectionBuilder<T, Set<T>> set(Class<T> t) {
		return new CollectionBuilder<T, Set<T>>(new HashSet<>());
	}

	public static <T> CollectionBuilder<T, ArraySet<T>> arraySet(Class<T> t) {
		return new CollectionBuilder<T, ArraySet<T>>(new ArraySet<>());
	}

	public static <T> CollectionBuilder<T, LinkedHashSet<T>> linkedSet(Class<T> t) {
		return new CollectionBuilder<T, LinkedHashSet<T>>(new LinkedHashSet<>());
	}

	public static DoubleCollectionBuilder<DoubleList> doubleList() {
		return new DoubleCollectionBuilder<DoubleList>(new DoubleArrayList());
	}

	protected final V collection;

	public CollectionBuilder(V collection) {
		this.collection = collection;
	}

	public CollectionBuilder<T, V> add(T object) {
		this.collection.add(object);
		return this;
	}

	@SafeVarargs
	public final CollectionBuilder<T, V> add(T... objects) {
		for (T object : objects) {
			this.collection.add(object);
		}
		return this;
	}

	public CollectionBuilder<T, V> addAll(Collection<T> object) {
		this.collection.addAll(object);
		return this;
	}

	@SafeVarargs
	public final CollectionBuilder<T, V> addAll(Collection<T>... objects) {
		for (Collection<T> object : objects) {
			this.collection.addAll(object);
		}
		return this;
	}

	public final CollectionBuilder<T, V> addAll(T[] object) {
		for (T t : object) {
			this.collection.add(t);
		}
		return this;
	}

	@SafeVarargs
	public final CollectionBuilder<T, V> addAll(T[]... objects) {
		for (T[] object : objects) {
			addAll(object);
		}
		return this;
	}

	public final CollectionBuilder<T, V> remove(T objcet) {
		this.collection.remove(objcet);
		return this;
	}

	@SafeVarargs
	public final CollectionBuilder<T, V> remove(T... objects) {
		for (T object : objects) {
			remove(object);
		}
		return this;
	}


	public final V build() {
		return this.collection;
	}

	public final ImmutableSet<T> buildImmutableSet() {
		return ImmutableSet.copyOf(this.collection);
	}

	public static class DoubleCollectionBuilder<V extends Collection<Double>> extends CollectionBuilder<Double, V> {

		public DoubleCollectionBuilder(V collection) {
			super(collection);
		}

		public DoubleCollectionBuilder<V> add(double object) {
			this.collection.add(object);
			return this;
		}
	
		@SafeVarargs
		public final DoubleCollectionBuilder<V> add(double... objects) {
			for (double object : objects) {
				this.collection.add(object);
			}
			return this;
		}
	
		public DoubleCollectionBuilder<V> addAll(double[] object) {
			for (double t : object) {
				this.collection.add(t);
			}
			return this;
		}
	
		@SafeVarargs
		public final DoubleCollectionBuilder<V> addAll(double[]... objects) {
			for (double[] object : objects) {
				addAll(object);
			}
			return this;
		}
	}
}
