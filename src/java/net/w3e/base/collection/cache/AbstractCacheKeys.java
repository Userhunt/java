package net.w3e.base.collection.cache;

import java.util.Collection;

import org.apache.logging.log4j.util.StackLocatorUtil;

import net.w3e.base.collection.ArraySet;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.message.MessageUtil;

public abstract class AbstractCacheKeys<T> {

	protected static boolean log = true;

	private boolean init = false;

	public final void init() {
		this.init = true;
		initIns();
	}

	protected abstract void initIns();

	public final T getNoCache(T key) {
		return get(key, false);
	}

	public final T get(T key) {
		return get(key, true);
	}

	public final T get(T key, boolean cached) {
		if (key == null) {
			return key;
		}
		if (!this.init) {
			this.init = true;
			init();
		}
		return getIns(key, cached);
	}

	protected abstract T getIns(T key, boolean cached);

	public final ArraySet<T> getNoCache(T[] keys) {
		return get(keys, false);
	}

	public final ArraySet<T> get(T[] keys) {
		return get(keys, true);
	}

	public final ArraySet<T> get(T[] keys, boolean cached) {
		ArraySet<T> locations = new ArraySet<>();

		for (T key : keys) {
			locations.add(get(key, cached));
		}

		return locations;
	}

	public final ArraySet<T> getNoCache(Collection<T> keys) {
		return this.get(keys, false);
	}

	public final ArraySet<T> get(Collection<T> keys) {
		return this.get(keys, true);
	}

	public final ArraySet<T> get(Collection<T> keys, boolean cached) {
		ArraySet<T> locations = new ArraySet<>();

		for (T key : keys) {
			locations.add(get(key, cached));
		}

		return locations;
	}

	public abstract boolean remove(T value);

	@SuppressWarnings("unchecked")
	public final <V extends AbstractCacheKeys<T>> V register(T value) {
		if (contains(value, true)) {
			if (log) {
				BJsonUtil.MSG_UTIL().warn(MessageUtil.KEY_DUPLICATE.createMsg(keys(), value) + " " + StackLocatorUtil.getStackTraceElement(2));
			}
		} else {
			registerIns(value);
		}
		return (V)this;
	}

	protected abstract void registerIns(T value);

	@SuppressWarnings("unchecked")
	public final <V extends AbstractCacheKeys<T>> V clear() {
		this.init = false;
		clearIns();
		return (V)this;
	}

	protected abstract void clearIns();

	public abstract int size();

	public final boolean isEmpty() {
		return this.size() == 0;
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
