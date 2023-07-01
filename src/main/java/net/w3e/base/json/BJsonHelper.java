package net.w3e.base.json;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.collection.ArraySet;
import net.w3e.base.json.system.convert.BaseJsonHelperConvert;
import net.w3e.base.json.system.read.BaseJsonHelperRead;
import net.w3e.base.message.MessageUtil;

@Deprecated // not parsed
public interface BJsonHelper extends BaseJsonHelperRead, BaseJsonHelperConvert {

	/*========================================= convert collection ======================================*/
	default <T> List<T> convertList(JsonElement j, Function<JsonElement, T> function, String type) {
		return this.convertList(j, function, type, true);
	}
	default <T> List<T> convertList(JsonElement j, Function<JsonElement, T> function, String type, boolean log) {
		return BJsonUtil.convertList(logger(), j, function, type, log);
	}

	default <T> Set<T> convertSet(JsonElement j, Function<JsonElement, T> function, String type) {
		return this.convertSet(j, function, type, true);
	}
	default <T> Set<T> convertSet(JsonElement j, Function<JsonElement, T> function, String type, boolean log) {
		return BJsonUtil.convertSet(logger(), j, function, type, log);
	}

	default <T> ArraySet<T> convertArraySet(JsonElement j, Function<JsonElement, T> function, String type) {
		return this.convertArraySet(j, function, type, true);
	}
	default <T> ArraySet<T> convertArraySet(JsonElement j, Function<JsonElement, T> function, String type, boolean log) {
		return BJsonUtil.convertArraySet(logger(), j, function, type, log);
	}

	default <T> List<T> convertList(JsonElement j, T[] t, JsonDeserializationContext context) {
		return this.convertList(j, t, context, true);
	}
	default <T> List<T> convertList(JsonElement j, T[] t, JsonDeserializationContext context, boolean log) {
		return BJsonUtil.convertList(logger(), j, t, context, log);
	}

	default <T> Set<T> convertSet(JsonElement j, T[] t, JsonDeserializationContext context) {
		return this.convertSet(j, t, context, true);
	}
	default <T> Set<T> convertSet(JsonElement j, T[] t, JsonDeserializationContext context, boolean log) {
		return BJsonUtil.convertSet(this.logger(), j, t, context, log);
	}

	default <T> ArraySet<T> convertArraySet(JsonElement j, T[] t, JsonDeserializationContext context) {
		return this.convertArraySet(j, t, context, true);
	}
	default <T> ArraySet<T> convertArraySet(JsonElement j, T[] t, JsonDeserializationContext context, boolean log) {
		return BJsonUtil.convertArraySet(this.logger(), j, t, context, log);
	}

	/*========================================= read map ======================================*/

	default <T, V, R extends Map<T, V>> R readMap(R map, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction) {
		return this.readMap(map, j, key, keyFunction, valueFunction, true);
	}
	default <T, V, R extends Map<T, V>> R readMap(R map, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, boolean log) {
		return this.readMap(map, j, key, keyFunction, valueFunction, null, log);
	}
	default <T, V, R extends Map<T, V>> R readMap(R map, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType) {
		return this.readMap(map, j, key, keyFunction, valueFunction, keyType, true);
	}
	default <T, V, R extends Map<T, V>> R readMap(R map, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, boolean log) {
		return this.readMap(map, j, key, keyFunction, valueFunction, keyType, null, log);
	}
	default <T, V, R extends Map<T, V>> R readMap(R map, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType) {
		return this.readMap(map, j, key, keyFunction, valueFunction, keyType, valueType, true);
	}
	default <T, V, R extends Map<T, V>> R readMap(R map, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType, boolean log) {
		return BJsonUtil.readMap(map, this.logger(), j, key, keyFunction, valueFunction, keyType, valueType, log);
	}

	default <T, V> HashMap<T, V> readHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction) {
		return this.readHashMap(j, key, keyFunction, valueFunction, true);
	}
	default <T, V> HashMap<T, V> readHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, boolean log) {
		return this.readHashMap(j, key, keyFunction, valueFunction, null, log);
	}
	default <T, V> HashMap<T, V> readHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType) {
		return this.readHashMap(j, key, keyFunction, valueFunction, keyType, true);
	}
	default <T, V> HashMap<T, V> readHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, boolean log) {
		return this.readHashMap(j, key, keyFunction, valueFunction, keyType, null, log);
	}
	default <T, V> HashMap<T, V> readHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType) {
		return this.readHashMap(j, key, keyFunction, valueFunction, keyType, valueType, true);
	}
	default <T, V> HashMap<T, V> readHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType, boolean log) {
		return BJsonUtil.readHashMap(this.logger(), j, key, keyFunction, valueFunction, keyType, valueType, log);
	}

	default <T, V> LinkedHashMap<T, V> readLinkedHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction) {
		return this.readLinkedHashMap(j, key, keyFunction, valueFunction, true);
	}
	default <T, V> LinkedHashMap<T, V> readLinkedHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, boolean log) {
		return this.readLinkedHashMap(j, key, keyFunction, valueFunction, null, log);
	}
	default <T, V> LinkedHashMap<T, V> readLinkedHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType) {
		return this.readLinkedHashMap(j, key, keyFunction, valueFunction, keyType, true);
	}
	default <T, V> LinkedHashMap<T, V> readLinkedHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, boolean log) {
		return this.readLinkedHashMap(j, key, keyFunction, valueFunction, keyType, null, log);
	}
	default <T, V> LinkedHashMap<T, V> readLinkedHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType) {
		return this.readLinkedHashMap(j, key, keyFunction, valueFunction, keyType, valueType, true);
	}
	default <T, V> LinkedHashMap<T, V> readLinkedHashMap(JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType, boolean log) {
		return BJsonUtil.readLinkedHashMap(this.logger(), j, key, keyFunction, valueFunction, keyType, valueType, log);
	}

	/*========================================= read object ======================================*/
	default <T> T readObject(JsonObject j, String target, T def, BiFunction<JsonElement, String, T> function, String type) {
		return this.readObject(j, target, def, function, type, true);
	}
	default <T> T readObject(JsonObject j, String target, T def, BiFunction<JsonElement, String, T> function, String type, boolean log) {
		return this.readObject(j, target, def, function, type, log, false);
	}
	default <T> T readObject(JsonObject j, String target, T def, BiFunction<JsonElement, String, T> function, String type, boolean log, boolean canBeNull) {
		return BJsonUtil.readObject(logger(), j, target, def, function, type, log, canBeNull);
	}

	default <T extends Enum<T>> T readEnum(JsonObject j, String target, T def, Function<String, T> function, String type) {
		return this.readEnum(j, target, def, function, type, true);
	}
	default <T extends Enum<T>> T readEnum(JsonObject j, String target, T def, Function<String, T> function, String type, boolean log) {
		return this.readEnum(j, target, def, function, type, log, false);
	}
	default <T extends Enum<T>> T readEnum(JsonObject j, String target, T def, Function<String, T> function, String type, boolean log, boolean canBeNull) {
		return BJsonUtil.readEnum(logger(), j, target, def, function, type, log, canBeNull);
	}

	/*========================================= convert object ======================================*/
	default <T> T convertObject(JsonObject j, T def, BiFunction<JsonElement, String, T> function, String type) {
		return this.convertObject(j, def, function, type, true);
	}
	default <T> T convertObject(JsonObject j, T def, BiFunction<JsonElement, String, T> function, String type, boolean log) {
		return this.convertObject(j, def, function, type, log, false);
	}
	default <T> T convertObject(JsonObject j, T def, BiFunction<JsonElement, String, T> function, String type, boolean log, boolean canBeNull) {
		return BJsonUtil.convertObject(logger(), j, def, function, type, log, canBeNull);
	}

	default <T extends Enum<T>> T convertEnum(JsonElement j, T def, Function<String, T> function, String type) {
		return this.convertEnum(j, def, function, type, true);
	}
	default <T extends Enum<T>> T convertEnum(JsonElement j, T def, Function<String, T> function, String type, boolean log) {
		return this.convertEnum(j, def, function, type, log, false);
	}
	default <T extends Enum<T>> T convertEnum(JsonElement j, T def, Function<String, T> function, String type, boolean log, boolean canBeNull) {
		return BJsonUtil.convertEnum(logger(), j, def, function, type, log, canBeNull);
	}

	/*========================================= min/max ======================================*/
	default float minMax(float value, Float min, Float max, String arg, float def) {
		if (min != null && value < min) {
			this.throwMessage(MessageUtil.LESS_THAN.createMsg(arg, value, min));
			value = def;
		}
		if (max != null && value > max) {
			this.throwMessage(MessageUtil.MORE_THAN.createMsg(arg, value, max));
			value = def;
		}
		return value;
	}

	default int minMax(int value, Integer min, Integer max, String arg, int def) {
		if (min != null && value < min) {
			this.throwMessage(MessageUtil.LESS_THAN.createMsg(arg, value, min));
			value = def;
		}
		if (max != null && value > max) {
			this.throwMessage(MessageUtil.MORE_THAN.createMsg(arg, value, max));
			value = def;
		}
		return value;
	}
}
