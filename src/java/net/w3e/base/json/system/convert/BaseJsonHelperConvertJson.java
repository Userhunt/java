package net.w3e.base.json.system.convert;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperConvertJson extends BaseJsonHelper {

	default JsonObject convertJsonObject(JsonElement j) {
		return this.convertJsonObject(j, new JsonObject());
	}
	default JsonObject convertJsonObject(JsonElement j, JsonObject def) {
		return this.convertJsonObject(j, def, JsonObject.class.getSimpleName());
	}
	default JsonObject convertJsonObject(JsonElement j, JsonObject def, String arg) {
		return this.convertJsonObject(j, def, arg, true);
	}
	default JsonObject convertJsonObject(JsonElement j, JsonObject def, String arg, boolean log) {
		return this.convertJsonObject(j, def, arg, log, false);
	}
	default JsonObject convertJsonObject(JsonElement j, JsonObject def, String arg, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertJson.convertJsonObject(logger(), j, def, arg, log, canBeNull);
	}
	static JsonObject convertJsonObject(Logger logger, JsonElement j, JsonObject def, String arg, boolean log, boolean canBeNull) {
		return BJsonUtil.convertJsonObject(logger, j, def, arg, log, canBeNull);
	}

	default JsonElement convertJsonElement(JsonElement j, JsonElement def) {
		return this.convertJsonElement(j, def, JsonElement.class.getSimpleName());
	}
	default JsonElement convertJsonElement(JsonElement j, JsonElement def, String arg) {
		return this.convertJsonElement(j, def, arg, true);
	}
	default JsonElement convertJsonElement(JsonElement j, JsonElement def, String arg, boolean log) {
		return this.convertJsonElement(j, def, arg, log, false);
	}
	default JsonElement convertJsonElement(JsonElement j, JsonElement def, String arg, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertJson.convertJsonElement(logger(), j, def, arg, log, canBeNull);
	}
	static JsonElement convertJsonElement(Logger logger, JsonElement j, JsonElement def, String arg, boolean log, boolean canBeNull) {
		return BJsonUtil.convertJsonElement(logger, j, def, arg, log, canBeNull);
	}

	default JsonArray convertJsonArray(JsonElement j) {
		return this.convertJsonArray(j, new JsonArray());
	}
	default JsonArray convertJsonArray(JsonElement j, JsonArray def) {
		return this.convertJsonArray(j, def, JsonArray.class.getSimpleName());
	}
	default JsonArray convertJsonArray(JsonElement j, JsonArray def, String arg) {
		return this.convertJsonArray(j, def, arg, true);
	}
	default JsonArray convertJsonArray(JsonElement j, JsonArray def, String arg, boolean log) {
		return this.convertJsonArray(j, def, arg, log, false);
	}
	default JsonArray convertJsonArray(JsonElement j, JsonArray def, String arg, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertJson.convertJsonArray(logger(), j, def, arg, log, canBeNull);
	}
	static JsonArray convertJsonArray(Logger logger, JsonElement j, JsonArray def, String arg, boolean log, boolean canBeNull) {
		return BJsonUtil.convertJsonArray(logger, j, def, arg, log, canBeNull);
	}
}
