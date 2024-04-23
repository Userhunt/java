package net.w3e.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import net.w3e.base.holders.Object2Holder;

public class BStringUtil {

	private static final Map<Class<?>, Function<Object, String>> TO_STRING = new LinkedHashMap<>();

	@SuppressWarnings("unchecked")
	public static final <T> boolean registerToString(Class<T> cl, Function<T, String> function) {
		if (TO_STRING.containsKey(cl)) {
			return false;
		} else {
			TO_STRING.put(cl, (Function<Object, String>)function);
			return true;
		}
	}

	static {
		registerToString(String.class, (obj) -> obj);
	}

	private static boolean customString;

	public static final String applyCustomString(@NotNull Object object) {
		if (customString) {
			Function<Object, String> function = TO_STRING.get(object.getClass());
			if (function != null) {
				return function.apply(object);
			}
		}
		return null;
	}

	public static final String toString(Object object, boolean custom) {
		customString = custom;
		String string = toString(object);
		customString = false;
		return string;
	}

	public static String toString(Object object) {
		if (object == null) {
			return "null";
		}
		if (customString) {
			Function<Object, String> function = TO_STRING.get(object.getClass());
			if (function != null) {
				return function.apply(object);
			}
		}
		if (object instanceof Collection) {
			return toString((Collection<?>)object);
		}
		if (object instanceof Object[]) {
			return toString((Object[])object);
		}
		if (object.getClass().isArray()) {
			if (object instanceof boolean[]) {
				return toString((boolean[])object);
			}
			if (object instanceof byte[]) {
				return toString((byte[])object);
			}
			if (object instanceof short[]) {
				return toString((short[])object);
			}
			if (object instanceof int[]) {
				return toString((int[])object);
			}
			if (object instanceof long[]) {
				return toString((long[])object);
			}
			if (object instanceof float[]) {
				return toString((float[])object);
			}
			if (object instanceof double[]) {
				return toString((double[])object);
			}
		}
		if (object instanceof Map) {
			return toString((Map<?, ?>)object);
		}

		return object.toString();
	}

	public static final <T> String toString(Collection<T> collection) {
		return toString(collection, BStringUtil::toString);
	}

	public static final <T> String toString(Collection<T> collection, Function<T, String> function) {
		if (collection == null) {
			return null;
		}
		Iterator<T> iterator = collection.iterator();
		String str = "[";
		while (iterator.hasNext()) {
			try {
				String next = function.apply(iterator.next());
				if (next == null) {
					str += "null";
				} else {
					str += next;
				}
				if (iterator.hasNext()) {
					str += ",";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str + "]";
	}

	public static final <T, V> String toString(Map<T, V> map) {
		return toString(map, (e) -> new Object2Holder<>(e.getKey(), e.getValue()));
	}

	public static final <T, V, R, F> String toString(Map<T, V> map, Function<Entry<T, V>, Object2Holder<R, F>> function) {
		Map<R, F> m = new LinkedHashMap<>();
		for (Entry<T, V> entry : map.entrySet()) {
			Object2Holder<R, F> tuple = function.apply(entry);
			m.put(tuple.getA(), tuple.getB());
		}
		String str = "{";
		Iterator<Entry<R, F>> iterator = m.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<R, F> next = iterator.next();
			str += toString(next.getKey()) + ":" + toString(next.getValue());
			if (iterator.hasNext()) {
				str += ",";
			}
		}
		return str + "}";
	}

	public static final String toString(Object[] objects) {
		String str = "[";
		Iterator<Object> iterator = Arrays.asList(objects).iterator();
		while (iterator.hasNext()) {
			str += toString(iterator.next());
			if (iterator.hasNext()) {
				str += ",";
			}
		}
		return str + "]";
	}

	public static final String toString(boolean[] objects) {
		List<Boolean> output = new ArrayList<Boolean>();
		for (boolean value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(byte[] objects) {
		List<Byte> output = new ArrayList<Byte>();
		for (byte value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(short[] objects) {
		List<Short> output = new ArrayList<Short>();
		for (short value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(int[] objects) {
		return toString(Arrays.stream(objects).boxed().collect(Collectors.toList()));
	}

	public static final String toString(long[] objects) {
		return toString(Arrays.stream(objects).boxed().collect(Collectors.toList()));
	}

	public static final String toString(float[] objects) {
		List<Float> output = new ArrayList<Float>();
		for (float value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(double[] objects) {
		return toString(Arrays.stream(objects).boxed().collect(Collectors.toList()));
	}

	public static final List<StringBuilder> tableFormat(List<?>... lists) {
		return tableFormat(" ", lists);
	}

	public static final List<StringBuilder> tableFormat(String split, List<?>... lists) {
		split = split == null ? " " : split;
		List<StringBuilder> builders = new ArrayList<>();
		int max = 0;
		List<List<?>> array = Arrays.asList(lists).stream().filter(Objects::nonNull).toList();
		for (List<?> list : array) {
			max = Math.max(max, list.size());
		}
		for (int i = 0; i < max; i++) {
			builders.add(new StringBuilder());
		}

		Iterator<List<?>> iterator = array.iterator();
		while (iterator.hasNext()) {
			List<?> list = iterator.next();
			if (list == null) {
				continue;
			}
			int i = 0;
			int size = list.size();
			for (StringBuilder builder : builders) {
				Object value = null;
				if (i < size) {
					value = list.get(i);
				}
				if (value == null) {
					value = "";
				}
				builder.append(value.toString());
				i++;
			}
			max = 0;
			for (StringBuilder builder : builders) {
				max = Math.max(max, builder.length());
			}
			for (StringBuilder builder : builders) {
				while (builder.length() < max) {
					builder.append(" ");
				}
			}

			if (iterator.hasNext()) {
				for (StringBuilder builder : builders) {
					builder.append(split);
				}
			}
		}

		return builders;
	}

	@Deprecated
	public static final List<StringBuilder> matrixFormat(List<?>... lists) {
		return null;
	}

	public static final String quote(String string) {
		return '"' + string.replaceAll("\"", "\\\\\"") + '"';
	}

	public static final String unQuote(String string) {
		return string.substring(1, string.length() - 1).replaceAll("\\\\\"", "\"");
	}
}
