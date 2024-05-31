package net.w3e.base.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;

public class MapCollectionBuilder<T, S, V extends Collection<S>, R extends Map<T, V>> {

	public static <T, V> MapCollectionBuilder<T, V, Set<V>, Map<T, Set<V>>> hashMapWithSet(Class<T> t, Class<V> v) {
		return new MapCollectionBuilder<>(new HashMap<>(), collection -> new HashSet<>(collection));
	}

	public static <T, V> MapCollectionBuilder<T, V, List<V>, Map<T, List<V>>> hashMapWithList(Class<T> t, Class<V> v) {
		return new MapCollectionBuilder<>(new HashMap<>(), collection -> Collections.unmodifiableList(new ArrayList<>(collection)));
	}

	private final R map;
	private final Function<Collection<S>, V> function;

	public MapCollectionBuilder(R map, Function<Collection<S>, V> function) {
		this.map = map;
		this.function = function;
	}

	public final MapCollectionBuilder<T, S, V, R> put(T t, V v) {
		V value = map.get(t);
		if (value != null) {
			List<S> list = new ArrayList<>(value);
			list.addAll(v);
			v = function.apply(list);
		}
		this.map.put(t, v);
		return this;
	}

	private final V getEmpty() {
		return function.apply(Collections.emptyList());
	}

	public MapCollectionBuilder<T, S, V, R> put(Entry<T, V> entry) {
		put(entry.getKey(), entry.getValue());
		return this;
	}

	@SafeVarargs
	public final MapCollectionBuilder<T, S, V, R> put(Entry<T, V>... entry) {
		for (Entry<T, V> entry2 : entry) {
			put(entry2.getKey(), entry2.getValue());
		}
		return this;
	}

	public MapCollectionBuilder<T, S, V, R> put(T key) {
		put(key, getEmpty());
		return this;
	}

	public MapCollectionBuilder<T, S, V, R> put(Set<T> keys) {
		for (T t : keys) {
			put(t, getEmpty());
		}
		return this;
	}

	public MapCollectionBuilder<T, S, V, R> put(Collection<T> keys, V v) {
		for (T t : keys) {
			put(t, v);
		}
		return this;
	}

	public MapCollectionBuilder<T, S, V, R> put(Collection<T> keys, S v) {
		for (T t : keys) {
			List<S> collection = new ArrayList<>();
			collection.add(v);
			put(t, this.function.apply(collection));
		}
		return this;
	}

	public MapCollectionBuilder<T, S, V, R> put(T t, S s) {
		List<S> collection = new ArrayList<>();
		collection.add(s);
		put(t, this.function.apply(collection));
		return this;
	}

	@SafeVarargs
	public final MapCollectionBuilder<T, S, V, R> put(T t, S... s) {
		List<S> set;
		if (map.containsKey(t)) {
			set = new ArrayList<>(map.get(t));
		} else {
			set = new ArrayList<>();
		}
		for (S s2 : s) {
			set.add(s2);
		}
		put(t, this.function.apply(set));
		return this;
	}

	public final MapCollectionBuilder<T, S, V, R> putAll(Map<T, V> map) {
		for (Entry<T, V> entry : map.entrySet()) {
			put(entry);
		}
		return this;
	}

	@SafeVarargs
	public final MapCollectionBuilder<T, S, V, R> putAll(Map<T, V>... map) {
		for (Map<T,V> m : map) {
			putAll(m);
		}
		return this;
	}

	public R build() {
		return map;
	}
}
