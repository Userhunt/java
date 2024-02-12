package net.w3e.base.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

public class MapBuilder<T, V, R extends Map<T, V>> {

	public static <T, V> MapBuilder<T, V, Map<T, V>> hashMap(Class<T> t, Class<V> v) {
		return new MapBuilder<T, V, Map<T, V>>(new HashMap<>());
	}

	public static <T, V> MapBuilder<T, Set<V>, Map<T, Set<V>>> hashMapWithSet(Class<T> t, Class<V> v) {
		return new MapBuilder<T, Set<V>, Map<T, Set<V>>>(new HashMap<>());
	}

	private final R map;

	public MapBuilder(R map) {
		this.map = map;
	}

	public MapBuilder<T, V, R> put(T t, V v) {
		map.put(t, v);
		return this;
	}

	public MapBuilder<T, V, R> put(Entry<T, V> entry) {
		map.put(entry.getKey(), entry.getValue());
		return this;
	}

	@SafeVarargs
	public final MapBuilder<T, V, R> put(Entry<T, V>... entry) {
		for (Entry<T, V> entry2 : entry) {
			put(entry2);
		}
		return this;
	}

	public final MapBuilder<T, V, R> put(Collection<Entry<T, V>> entry) {
		for (Entry<T, V> entry2 : entry) {
			put(entry2);
		}
		return this;
	}

	@SafeVarargs
	public final MapBuilder<T, V, R> put(Collection<Entry<T, V>>... entry) {
		for (Collection<Entry<T, V>> entry2 : entry) {
			put(entry2);
		}
		return this;
	}

	public MapBuilder<T, V, R> put(Set<T> keys, V v) {
		for (T t : keys) {
			map.put(t, v);
		}
		return this;
	}

	public MapBuilder<T, V, R> putAll(Map<T, V> map) {
		map.putAll(map);
		return this;
	}

	@SafeVarargs
	public final MapBuilder<T, V, R> putAll(Map<T, V>... map) {
		for (Map<T,V> m : map) {
			this.map.putAll(m);
		}
		return this;
	}

	public R build() {
		return map;
	}

	public ImmutableMap<T, V> buildImmitableMap() {
		return ImmutableMap.copyOf(map.entrySet());
	}
}
