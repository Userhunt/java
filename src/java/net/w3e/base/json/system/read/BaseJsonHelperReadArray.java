package net.w3e.base.json.system.read;

import java.util.function.BiFunction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperReadArray extends BaseJsonHelper {

	static double[] readDoubleArrayUnsetSize(JsonObject j, String name, int length) {
		return BJsonUtil.readDoubleArrayUnsetSize(j, name, length, true);
	}
	default double[] readDoubleArrayInsUnsetSize(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readDoubleArrayUnsetSize(logger(), j, name, length, log);
	}

	default double[] readDoubleArrayIns(JsonObject j, String name, int length) {
		return BJsonUtil.readDoubleArray(j, name, length, true);
	}
	default double[] readDoubleArrayIns(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readDoubleArray(logger(), j, name, length, log);
	}

	default byte[] readByteArrayInsUnsetSize(JsonObject j, String name, int length) {
		return BJsonUtil.readByteArrayUnsetSize(j, name, length, true);
	}
	default byte[] readByteArrayInsUnsetSize(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readByteArrayUnsetSize(logger(), j, name, length, log);
	}

	default byte[] readByteArrayIns(JsonObject j, String name, int length) {
		return BJsonUtil.readByteArray(j, name, length, true);
	}
	default byte[] readByteArrayIns(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readByteArray(logger(), j, name, length, log);
	}

	default int[] readIntArrayInsUnsetSize(JsonObject j, String name, int length) {
		return BJsonUtil.readIntArrayUnsetSize(j, name, length, true);
	}
	default int[] readIntArrayInsUnsetSize(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readIntArrayUnsetSize(logger(), j, name, length, log);
	}

	default int[] readIntArrayIns(JsonObject j, String name, int length) {
		return BJsonUtil.readIntArray(j, name, length, true);
	}
	default int[] readIntArrayIns(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readIntArray(logger(), j, name, length, log);
	}

	default float[] readFloatArrayInsUnsetSize(JsonObject j, String name, int length) {
		return BJsonUtil.readFloatArrayUnsetSize(j, name, length, true);
	}
	default float[] readFloatArrayInsUnsetSize(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readFloatArrayUnsetSize(logger(), j, name, length, log);
	}

	default float[] readFloatArrayIns(JsonObject j, String name, int length) {
		return BJsonUtil.readFloatArray(j, name, length, true);
	}
	default float[] readFloatArrayIns(JsonObject j, String name, int length, boolean log) {
		return BJsonUtil.readFloatArray(logger(), j, name, length, log);
	}

	default <T> T[] readArray(JsonObject j, String name, Class<T> clazz, BiFunction<JsonElement, String, T> function) {
		return this.readArray(j, name, clazz, function, null);
	}

	default <T> T[] readArray(JsonObject j, String name, Class<T> clazz, BiFunction<JsonElement, String, T> function, T[] def) {
		return this.readArray(j, name, clazz, function, def, true);
	}

	default <T> T[] readArray(JsonObject j, String name, Class<T> clazz, BiFunction<JsonElement, String, T> function, T[] def, boolean log) {
		return BJsonUtil.readArray(this.logger(), j, name, clazz, def, function, log);
	}

	/*default int[] toArray(int[] base, JsonObject jsonObject, String key, JsonDeserializationContext context) {
		return BJsonUtil.toArray(base, (BJsonHelper)this, jsonObject, key, context);
	}*/
}
