package net.w3e.base.json.system.convert;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import net.w3e.base.collection.ArraySet;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperConvertList extends BaseJsonHelper {

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
}
