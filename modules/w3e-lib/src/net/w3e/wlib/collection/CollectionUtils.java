package net.w3e.wlib.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.skds.lib2.mat.FastMath;

public class CollectionUtils {

	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T extends Object> T[] removeElFromArray(Class<T> c, T[] array, int i) {
		List<T> list = new ArrayList<>(Arrays.asList(array));
		list.remove(i);

		T[] genericArray = (T[]) Array.newInstance(c, list.size());
		return list.toArray(genericArray);
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T extends Object> T[] removeElFromArray(Class<T> c, T[] array, T i) {
		List<T> list = new ArrayList<>(Arrays.asList(array));
		list.remove(i);

		T[] genericArray = (T[]) Array.newInstance(c, list.size());
		return list.toArray(genericArray);
	}

	@Deprecated
	public static final <T> T getRandom(Collection<T> set) {
		return getRandom(set, FastMath.RANDOM);
	}

	@Deprecated
	public static final <T> T getRandom(Collection<T> set, Random random) {
		List<T> list = new ArrayList<>(set);
		if (list.isEmpty()) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		}
		int a = random.nextInt(set.size());
		return list.get(a);
	}

	public static final <T, V> Collection<V> convert(Collection<T> original, Function<T, V> function) {
		return original.stream().map(function).toList();
	}

	public static final <T, V> List<V> convert(List<T> original, Function<T, V> function) {
		return original.stream().map(function).toList();
	}

	public static final <T, V> Set<V> convert(Set<T> original, Function<T, V> function) {
		return original.stream().map(function).collect(Collectors.toSet());
	}

	public static final <T, V> ArraySet<V> convert(ArraySet<T> original, Function<T, V> function) {
		return new ArraySet<>(original.stream().map(function).collect(Collectors.toSet()));
	}

	public static final <T> Collection<T> filter(Collection<T> original, Predicate<T> predicate) {
		return original.stream().filter(predicate).collect(Collectors.toList());
	}

	public static final <T, V> List<T> filter(List<T> original, Predicate<T> predicate) {
		return original.stream().filter(predicate).collect(Collectors.toList());
	}

	public static final <T, V> Set<T> filter(Set<T> original, Predicate<T> predicate) {
		return original.stream().filter(predicate).collect(Collectors.toSet());
	}

	public static final <T, V> ArraySet<T> filter(ArraySet<T> original, Predicate<T> predicate) {
		return new ArraySet<>(original.stream().filter(predicate).collect(Collectors.toList()));
	}

	@Deprecated
	public static final <T> void fixMap(Map<T, Object> map) {
		List<T> list = new ArrayList<>();
		for (Entry<T, Object> e : map.entrySet()) {
			Object value = e.getValue();
			if (value == null) {
				list.add(e.getKey());
			}
		}
		for (T t : list) {
			map.remove(t);
		}
	}

	@Deprecated
	public static final <T> void fixMapWithCollection(Map<T, ? extends Collection<?>> map) {
		List<T> list = new ArrayList<>();
		for (Entry<T, ? extends Collection<?>> e : map.entrySet()) {
			Collection<?> value = e.getValue();
			if (value == null || value.isEmpty()) {
				list.add(e.getKey());
			}
		}
		for (T t : list) {
			map.remove(t);
		}
	}

	@Deprecated
	public static <T> T foundMin(Collection<T> collection, Function<T, Integer> function) {
		int max = Integer.MAX_VALUE;
		T res = null;
		for (T t : collection) {
			int v = function.apply(t);
			if (v < max) {
				max = v;
				res = t;
			}
		}
		return res;
	}

	/**
	 * ugly hack for java
	 */
	@Deprecated
	public static final <K, V extends Comparable<V>> Map<K, V> valueSortU(Map<K, V> map) {
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

	@Deprecated
	public static final <K, V extends Comparable<V>> Map<K, V> valueSort(Map<K, V> map) {
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

	@SuppressWarnings("unchecked")
	public static final <T> T[] createArray(Class<T> clazz, int size) {
		return (T[])Array.newInstance(clazz, size);
	}

	@SafeVarargs
	public static final <T> T[] createArray(T... values) {
		return values;
	}

	@SuppressWarnings("unchecked")
	public static final <T> Supplier<T>[] createArrayOfSupplier(Class<T> clazz, int size) {
		return (Supplier<T>[])Array.newInstance(Supplier.class, size);
	}

	@SuppressWarnings("unchecked")
	public static final <T> List<T>[] createArrayOfList(Class<T> clazz, int size) {
		return (List<T>[])Array.newInstance(List.class, size);
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public static final <T> T[] removeNullElements(T[] array) {
		List<T> list = new ArrayList<T>();

		for(T s : array) {
			if (s != null) {
				list.add(s);
			}
		}

		return (T[])list.toArray();
	}

	public static final <T> Set<T> identitySet() {
		return Collections.newSetFromMap(new IdentityHashMap<>());
	}
}
