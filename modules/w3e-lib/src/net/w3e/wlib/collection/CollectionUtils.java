package net.w3e.wlib.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.ToIntFunction;

import net.w3e.wlib.collection.identity.IdentityLinkedHashMap;

public class CollectionUtils extends net.w3e.lib.utils.collection.CollectionUtils {

	public static <T> T foundMin(Collection<T> collection, ToIntFunction<T> function) {
		int max = Integer.MAX_VALUE;
		T res = null;
		for (T t : collection) {
			int v = function.applyAsInt(t);
			if (v < max) {
				max = v;
				res = t;
			}
		}
		return res;
	}

	public static final <K, C extends Comparable<C>, V extends C> Map<K, V> valueSort(Map<K, V> map) {
		if (map.size() <= 1) {
			return (Map<K, V>)map;
		}
		Comparator<K> valueComparator = new Comparator<K>() {
			@Override
			public int compare(K k1, K k2) {
				return map.get(k1).compareTo(map.get(k2));
			}
		};

		Map<K, V> sorted = new TreeMap<>(valueComparator);
		sorted.putAll(map);

		Map<K, V> res = new LinkedHashMap<>(sorted);

		return res;
	}

	@SafeVarargs
	public static final <T> T[] createArray(T... values) {
		return values;
	}

	public static final <T> Set<T> identitySet() {
		return Collections.newSetFromMap(new IdentityHashMap<>());
	}

	public static final <T> Set<T> identityLinkedSet() {
		return Collections.newSetFromMap(new IdentityLinkedHashMap<>());
	}
}
