package net.w3e.base.collection;

import java.util.HashMap;
import java.util.Map;

public class MapT<K> extends HashMap<K, Object> {

	public MapT() {
		super();
	}

    public MapT(Map<? extends K, ? extends Object> map) {
		super(map);
	}

	@SuppressWarnings("unchecked")
	public final <T> T getT(K key) {
		return (T)get(key);
	}

	public final <T> T getT(K key, Class<T> clazz) {
		return getT(key);
	}

	public final boolean getBoolean(K key) {
		return getT(key, boolean.class);
	}

	public final byte getByte(K key) {
		return getNumber(key).byteValue();
	}

	public final short getShort(K key) {
		return getNumber(key).shortValue();
	}

	public final int getInt(K key) {
		return getNumber(key).intValue();
	}

	public final long getLong(K key) {
		return getNumber(key).longValue();
	}

	public final float getFloat(K key) {
		return getNumber(key).floatValue();
	}

	public final double getDouble(K key) {
		return getNumber(key).doubleValue();
	}

	public final Number getNumber(K key) {
		Number number = getT(key, Number.class);
		return number == null ? 0 : number;
	}

	public static class MapTString extends MapT<String> {

		public MapTString() {
			super();
		}

		public MapTString(Map<String, ? extends Object> map) {
			super(map);
		}
	}
}
