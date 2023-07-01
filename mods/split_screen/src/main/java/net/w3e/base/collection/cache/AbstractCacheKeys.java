package net.w3e.base.collection.cache;

import java.util.Collection;

import org.apache.logging.log4j.util.StackLocatorUtil;

import net.w3e.base.collection.ArraySet;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.message.MessageUtil;

/**
 * 15.04.23
 */
public abstract class AbstractCacheKeys<T, V> {

	protected static boolean log = true;

	private boolean init = false;

	@SuppressWarnings("unchecked")
	public final V cast() {
		return (V)this;
	}

	public final void init() {
		this.init = true;
		initIns();
	}

	protected abstract void initIns();

	public final T get(T key) {
		if (key == null) {
			return key;
		}
		if (!this.init) {
			this.init = true;
			init();
		}
		return getIns(key);
	}

	protected abstract T getIns(T key);

	public final ArraySet<T> get(T[] keys) {
		ArraySet<T> locations = new ArraySet<>();

		for (T key : keys) {
			locations.add(get(key));
		}

		return locations;
	}

	public final ArraySet<T> get(Collection<T> keys) {
		ArraySet<T> locations = new ArraySet<>();

		for (T key : keys) {
			locations.add(get(key));
		}

		return locations;
	}

	public final V register(T value) {
		if (contains(value, true)) {
			if (log) {
				BJsonUtil.MSG_UTIL().warn(MessageUtil.KEY_DUPLICATE.createMsg(keys(), value) + " " + StackLocatorUtil.getStackTraceElement(2));
			}
		} else {
			registerIns(value);
		}
		return cast();
	}

	protected abstract void registerIns(T value);

	public final V clear() {
		this.init = false;
		clearIns();
		return cast();
	}

	protected abstract void clearIns();

	public abstract int size();

	public final boolean isEmpty() {
		return size() == 0;
	}

	public abstract ArraySet<T> keys();

	public final boolean contains(T value) {
		return contains(value, true);
	}

	public final boolean contains(T value, boolean strict) {
		if (value == null) {
			return false;
		}
		ArraySet<T> keys = keys();
		if (strict) {
			for (T key : keys) {
				if (key == value) {
					return true;
				}
			}
			return false;
		} else {
			return keys.contains(value);
		}
	}
}
