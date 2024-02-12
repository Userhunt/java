package net.w3e.base.json.system.read;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.collection.ArraySet;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperReadCollection extends BaseJsonHelper {

	default <T> List<T> readList(JsonObject j, String target, Function<JsonElement, T> function, String type) {
		return this.readList(j, target, function, type, true);
	}
	default <T> List<T> readList(JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return BaseJsonHelperReadCollection.readList(logger(), j, target, function, type, log);
	}
	static <T> List<T> readList(Logger logger, JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return BJsonUtil.readList(logger, j, target, function, type, log);
	}

	default <T> Set<T> readSet(JsonObject j, String target, Function<JsonElement, T> function, String type) {
		return this.readSet(j, target, function, type, true);
	}
	default <T> Set<T> readSet(JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return BaseJsonHelperReadCollection.readSet(logger(), j, target, function, type, log);
	}
	static <T> Set<T> readSet(Logger logger, JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return BJsonUtil.readSet(logger, j, target, function, type, log);
	}

	default <T> ArraySet<T> readArraySet(JsonObject j, String target, Function<JsonElement, T> function, String type) {
		return this.readArraySet(j, target, function, type, true);
	}
	default <T> ArraySet<T> readArraySet(JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return BaseJsonHelperReadCollection.readArraySet(logger(), j, target, function, type, log);
	}
	static <T> ArraySet<T> readArraySet(Logger logger, JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return BJsonUtil.readArraySet(logger, j, target, function, type, log);
	}

	default <T> List<T> readList(JsonObject j, String key, T[] t, JsonDeserializationContext context) {
		return this.readList(j, key, t, context, true);
	}
	default <T> List<T> readList(JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return BaseJsonHelperReadCollection.readList(logger(), j, key, t, context, log);
	}
	static <T> List<T> readList(Logger logger, JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return BJsonUtil.readList(logger, j, key, t, context, log);
	}

	default <T> Set<T> readSet(JsonObject j, String key, T[] t, JsonDeserializationContext context) {
		return this.readSet(j, key, t, context, true);
	}
	default <T> Set<T> readSet(JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return BaseJsonHelperReadCollection.readSet(logger(), j, key, t, context, log);
	}
	static <T> Set<T> readSet(Logger logger, JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return BJsonUtil.readSet(logger, j, key, t, context, log);
	}

	default <T> ArraySet<T> readArraySet(JsonObject j, String key, T[] t, JsonDeserializationContext context) {
		return this.readArraySet(j, key, t, context, true);
	}
	default <T> ArraySet<T> readArraySet(JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return BaseJsonHelperReadCollection.readArraySet(logger(), j, key, t, context, log);
	}
	static <T> ArraySet<T> readArraySet(Logger logger, JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return BJsonUtil.readArraySet(logger, j, key, t, context, log);
	}
}
