package net.w3e.base.json.system.read;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperReadObject extends BaseJsonHelper {

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
}
