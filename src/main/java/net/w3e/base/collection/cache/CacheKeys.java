package net.w3e.base.collection.cache;

import net.w3e.base.collection.ArraySet;

/**
 * 15.04.23
 */
public abstract class CacheKeys<T> extends AbstractCacheKeys<T, CacheKeys<T>> {

	private final ArraySet<T> keys = new ArraySet<>();

	protected final T getIns(T key) {
		for (T t : keys) {
			if (key == t) {
				return t;
			}
		}
		for (T t : keys) {
			if (key.equals(t)) {
				return t;
			}
		}
		keys.add(key);
		return key;
	}

	protected final void registerIns(T value) {
		if (value == null) {
			keys.add(value);
			return;
		}
		for (T t : keys) {
			if (value.equals(t)) {
				keys.remove(value);
				break;
			}
		}
		keys.add(value);
	}

	protected final void clearIns() {
		this.keys.clear();
	}

	@Override
	public int size() {
		return this.keys.size();
	}

	@Override
	public final ArraySet<T> keys() {
		return new ArraySet<>(keys);
	}

	public static class CacheKeysEmpty<T> extends CacheKeys<T> {
		@Override
		public void initIns() {}
	}
}
