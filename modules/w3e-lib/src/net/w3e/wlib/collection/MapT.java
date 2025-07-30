package net.w3e.wlib.collection;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.skds.lib2.io.json.codec.BuiltinCodecFactory.MapCodec;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

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

	public final String getSting(K key) {
		return getT(key);
	}

	public final <T extends Enum<T>> T getEnum(K key) {
		return getT(key);
	}

	public final <T extends Enum<T>> T getEnum(K key, Class<T> clazz) {
		return getEnum(key);
	}

	public final Boolean getBOOLEAN(K key) {
		return getT(key);
	}

	public final Number getNUMBER(K key) {
		return getT(key);
	}

	public final Byte getBYTE(K key) {
		return getT(key);
	}

	public final Short getSHORT(K key) {
		return getT(key);
	}

	public final Integer getINTEGER(K key) {
		return getT(key);
	}

	public final Long getLONG(K key) {
		return getT(key);
	}

	public final Float getFLOAT(K key) {
		return getT(key);
	}

	public final Double getDOUBLE(K key) {
		return getT(key);
	}

	public final UUID getUUID(K key) {
		return getT(key);
	}

	@DefaultJsonCodec(MapTStringCodec.class)
	public static class MapTString extends MapT<String> {

		public MapTString() {
			super();
		}

		public MapTString(Map<String, ? extends Object> map) {
			super(map);
		}
	}

	public static class MapTStringCodec extends MapCodec {

		public MapTStringCodec(Type type, JsonCodecRegistry registry) {
			super(MapTString.class, new Type[]{String.class, Object.class}, registry);
		}
	}
}
