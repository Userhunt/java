package net.w3e.base.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;

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

	private final V collection;

	public CollectionBuilder(V collection) {
		this.collection = collection;
	}

	public CollectionBuilder<T, V> add(T object) {
		collection.add(object);
		return this;
	}

	@SafeVarargs
	public final CollectionBuilder<T, V> add(T... objects) {
		for (T object : objects) {
			collection.add(object);
		}
		return this;
	}

	public CollectionBuilder<T, V> addAll(Collection<T> object) {
		collection.addAll(object);
		return this;
	}

	@SafeVarargs
	public final CollectionBuilder<T, V> addAll(Collection<T>... objects) {
		for (Collection<T> object : objects) {
			collection.addAll(object);
		}
		return this;
	}

	public CollectionBuilder<T, V> addAll(T[] object) {
		for (T t : object) {
			collection.add(t);
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

	public CollectionBuilder<T, V> remove(T objcet) {
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


	public V build() {
		return collection;
	}

	public ImmutableSet<T> buildImmutableSet() {
		return ImmutableSet.copyOf(collection);
	}

}
