package net.w3e.wlib.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class CollectionBuilder<T, V extends Collection<T>, R extends CollectionBuilder<T, V, R>> {

	public static <T> SimpleCollectionBuilder<Consumer<T>, ArrayList<Consumer<T>>> listConsumer(Class<T> t) {
		return new SimpleCollectionBuilder<Consumer<T>, ArrayList<Consumer<T>>>(new ArrayList<>());
	}

	public static <T, V> SimpleCollectionBuilder<BiConsumer<T, V>, ArrayList<BiConsumer<T, V>>> listBiConsumer(Class<T> t, Class<V> v) {
		return new SimpleCollectionBuilder<BiConsumer<T, V>, ArrayList<BiConsumer<T, V>>>(new ArrayList<>());
	}

	public static <T> SimpleCollectionBuilder<T, ArrayList<T>> list(Class<T> t) {
		return new SimpleCollectionBuilder<T, ArrayList<T>>(new ArrayList<>());
	}

	public static <T> SimpleCollectionBuilder<T, Set<T>> set(Class<T> t) {
		return new SimpleCollectionBuilder<T, Set<T>>(new HashSet<>());
	}

	public static <T> SimpleCollectionBuilder<T, ArraySet<T>> arraySet(Class<T> t) {
		return new SimpleCollectionBuilder<T, ArraySet<T>>(new ArraySet<>());
	}

	public static <T> SimpleCollectionBuilder<T, LinkedHashSet<T>> linkedSet(Class<T> t) {
		return new SimpleCollectionBuilder<T, LinkedHashSet<T>>(new LinkedHashSet<>());
	}

	public static DoubleCollectionBuilder<DoubleList> doubleList() {
		return new DoubleCollectionBuilder<DoubleList>(new DoubleArrayList());
	}

	protected final V collection;

	public CollectionBuilder(V collection) {
		this.collection = collection;
	}

	@SuppressWarnings("unchecked")
	protected final R cast() {
		return (R)this;
	}

	public final R add(T object) {
		this.collection.add(object);
		return this.cast();
	}

	@SafeVarargs
	public final R add(T... objects) {
		for (T object : objects) {
			this.collection.add(object);
		}
		return this.cast();
	}

	public final R addAll(Collection<T> object) {
		this.collection.addAll(object);
		return this.cast();
	}

	@SafeVarargs
	public final R addAll(Collection<T>... objects) {
		for (Collection<T> object : objects) {
			this.collection.addAll(object);
		}
		return this.cast();
	}

	public final R addAll(T[] object) {
		for (T t : object) {
			this.collection.add(t);
		}
		return this.cast();
	}

	@SafeVarargs
	public final R addAll(T[]... objects) {
		for (T[] object : objects) {
			addAll(object);
		}
		return this.cast();
	}

	public final R addAll(Stream<T> stream) {
		Iterator<T> iterator = stream.iterator();
		while (iterator.hasNext()) {
			this.add(iterator.next());
		}
		return this.cast();
	}

	@SafeVarargs
	public final R addAll(Stream<T>... streams) {
		for (Stream<T> stream : streams) {
			this.addAll(stream);
		}
		return this.cast();
	}

	public final R remove(T objcet) {
		this.collection.remove(objcet);
		return this.cast();
	}

	@SafeVarargs
	public final R remove(T... objects) {
		for (T object : objects) {
			remove(object);
		}
		return this.cast();
	}

	public R removeNull() {
		this.collection.removeIf(Objects::isNull);
		return this.cast();
	}


	public final V build() {
		return this.collection;
	}

	public final Set<T> buildImmutableSet() {
		return Collections.unmodifiableSet(new HashSet<>(this.collection));
	}

	public static class SimpleCollectionBuilder<T, V extends Collection<T>> extends CollectionBuilder<T, V, SimpleCollectionBuilder<T, V>> {
		public SimpleCollectionBuilder(V collection) {
			super(collection);
		}
	}

	public static class DoubleCollectionBuilder<V extends Collection<Double>> extends CollectionBuilder<Double, V, DoubleCollectionBuilder<V>> {

		public DoubleCollectionBuilder(V collection) {
			super(collection);
		}

		public final DoubleCollectionBuilder<V> add(double object) {
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
