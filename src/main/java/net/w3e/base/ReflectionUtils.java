package net.w3e.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 16.03.23 15:36
 */
public class ReflectionUtils {

	public static final List<Class<?>> getParentList(Class<?> class1) {
		List<Class<?>> list = new ArrayList<>();
		Class<?> class2 = class1;
		while (class2.getSuperclass() != null) {
			class2 = class2.getSuperclass();
			list.add(class2);
		}
		return list;
	}

	public static final boolean instaceOf(Class<?> class1, Class<?> type) {
		if (type == null) {
			return false;
		}
		if (class1 == type) {
			return true;
		}
		Class<?> class3 = class1;
		while (class3.getSuperclass() != null) {
			class3 = class3.getSuperclass();
			if (class3 == type) {
				return true;
			}
		}
		return false;
	}

	public static final boolean containsInCollection(Class<?> class1, Collection<Class<?>> list) {
		if (list.contains(class1)) {
			return true;
		}
		Class<?> class2 = class1;
		while (class2.getSuperclass() != null) {
			class2 = class2.getSuperclass();
			if (list.contains(class2)) {
				return true;
			}
		}
		return false;
	}

	public static Field access(Class<?> cl, String key) {
		try {
			Field field = cl.getDeclaredField(key);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static Field access(Class<?> cl, int key) {
		try {
			Field field = cl.getDeclaredFields()[key];
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
