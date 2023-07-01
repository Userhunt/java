package net.w3e.base.json.system.read;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperReadMap extends BaseJsonHelper {
	
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
}
