package net.w3e.wlib.collection.cache;

import net.w3e.wlib.collection.ArraySet;

public abstract class CacheKeys<T> extends AbstractCacheKeys<T> {

	private final ArraySet<T> keys = new ArraySet<>();

	@Override
	protected final T getIns(T key, boolean cached) {
		for (T t : this.keys) {
			if (key == t) {
				return t;
			}
		}
		for (T t : this.keys) {
			if (key.equals(t)) {
				return t;
			}
		}
		if (cached) {
			this.keys.add(key);
		}
		return key;
	}

	@Override
	public boolean remove(T value) {
		return this.keys.remove(value);
	}

	@Override
	protected final void registerIns(T value) {
		if (value == null) {
			keys.add(value);
			return;
		}
		for (T t : this.keys) {
			if (value.equals(t)) {
				this.keys.remove(value);
				break;
			}
		}
		this.keys.add(value);
	}

	@Override
	protected void clearIns() {
		this.keys.clear();
	}

	@Override
	public final int size() {
		return this.keys.size();
	}

	@Override
	public final ArraySet<T> keys() {
		return new ArraySet<>(this.keys);
	}

	public static class CacheKeysEmpty<T> extends CacheKeys<T> {
		@Override
		public void initIns() {}
	}
}
