package net.w3e.wlib.collection.map;

import java.util.Map;
import java.util.UUID;

public interface MapK<K> {

	@SuppressWarnings("unchecked")
	default <T> T getT(K key) {
		return (T)((Map<K, Object>)this).get(key);
	}
	default <T> T getT(K key, Class<T> clazz) {
		return getT(key);
	}

	default boolean getBoolean(K key) {
		return getT(key, boolean.class);
	}

	default byte getByte(K key) {
		return getNumber(key).byteValue();
	}

	default short getShort(K key) {
		return getNumber(key).shortValue();
	}

	default int getInt(K key) {
		return getNumber(key).intValue();
	}

	default long getLong(K key) {
		return getNumber(key).longValue();
	}

	default float getFloat(K key) {
		return getNumber(key).floatValue();
	}

	default double getDouble(K key) {
		return getNumber(key).doubleValue();
	}

	default Number getNumber(K key) {
		Number number = getT(key);
		return number == null ? 0 : number;
	}

	default String getSting(K key) {
		return getT(key);
	}

	default <T extends Enum<T>> T getEnum(K key) {
		return getT(key);
	}

	default <T extends Enum<T>> T getEnum(K key, Class<T> clazz) {
		return getT(key);
	}

	default Boolean getBOOLEAN(K key) {
		return getT(key);
	}

	default Number getNUMBER(K key) {
		return getT(key);
	}

	default Byte getBYTE(K key) {
		return getT(key);
	}

	default Short getSHORT(K key) {
		return getT(key);
	}

	default Integer getINTEGER(K key) {
		return getT(key);
	}

	default Long getLONG(K key) {
		return getT(key);
	}

	default Float getFLOAT(K key) {
		return getT(key);
	}

	default Double getDOUBLE(K key) {
		return getT(key);
	}

	default UUID getUUID(K key) {
		return getT(key);
	}

}
