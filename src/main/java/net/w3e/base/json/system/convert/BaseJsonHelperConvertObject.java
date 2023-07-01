package net.w3e.base.json.system.convert;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperConvertObject extends BaseJsonHelper {

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
}
