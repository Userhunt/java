package net.w3e.base.json.system.read;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperReadJson extends BaseJsonHelper {

	default JsonObject readJsonObject(JsonObject j, String target) {
		return this.readJsonObject(j, target, new JsonObject());
	}
	default JsonObject readJsonObject(JsonObject j, String target, JsonObject def) {
		return this.readJsonObject(j, target, def, JsonObject.class.getSimpleName());
	}
	default JsonObject readJsonObject(JsonObject j, String target, JsonObject def, String type) {
		return this.readJsonObject(j, target, def, type, true);
	}
	default JsonObject readJsonObject(JsonObject j, String target, JsonObject def, String type, boolean log) {
		return this.readJsonObject(j, target, def, type, log, false);
	}
	default JsonObject readJsonObject(JsonObject j, String target, JsonObject def, String type, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadJson.readJsonObject(logger(), j, target, def, type, log, canBeNull);
	}
	static JsonObject readJsonObject(Logger logger, JsonObject j, String target, JsonObject def, String type, boolean log, boolean canBeNull) {
		return BJsonUtil.readJsonObject(logger, j, target, def, type, log, canBeNull);
	}

	default JsonObject readJsonObject(JsonObject j, String target, String arg) {
		return this.readJsonObject(j, target, arg, true);
	}
	default JsonObject readJsonObject(JsonObject j, String target, String arg, boolean log) {
		return this.readJsonObject(j, target, arg, log, false);
	}
	default JsonObject readJsonObject(JsonObject j, String target, String arg, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadJson.readJsonObject(logger(), j, target, new JsonObject(), arg, log, canBeNull);
	}

	default JsonObject readJsonObject(JsonObject j, String target, boolean log) {
		return this.readJsonObject(j, target, log, false);
	}
	default JsonObject readJsonObject(JsonObject j, String target, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadJson.readJsonObject(logger(), j, target, new JsonObject(), JsonObject.class.getSimpleName(), log, canBeNull);
	}

	default JsonElement readJsonElement(JsonObject j, String target, JsonElement def) {
		return this.readJsonElement(j, target, def, JsonElement.class.getSimpleName());
	}
	default JsonElement readJsonElement(JsonObject j, String target, JsonElement def, String arg) {
		return this.readJsonElement(j, target, def, arg, true);
	}
	default JsonElement readJsonElement(JsonObject j, String target, JsonElement def, String arg, boolean log) {
		return this.readJsonElement(j, target, def, arg, log, false);
	}
	default JsonElement readJsonElement(JsonObject j, String target, JsonElement def, String arg, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadJson.readJsonElement(logger(), j, target, def, arg, log, canBeNull);
	}
	static JsonElement readJsonElement(Logger logger, JsonObject j, String target, JsonElement def, String arg, boolean log, boolean canBeNull) {
		return BJsonUtil.readJsonElement(logger, j, target, def, arg, log, canBeNull);
	}

	default JsonArray readJsonArray(JsonObject j, String target) {
		return this.readJsonArray(j, target, new JsonArray());
	}
	default JsonArray readJsonArray(JsonObject j, String target, boolean log) {
		return this.readJsonArray(j, target, new JsonArray(), JsonArray.class.getSimpleName(), log);
	}
	default JsonArray readJsonArray(JsonObject j, String target, String arg, boolean log) {
		return this.readJsonArray(j, target, new JsonArray(), arg, log);
	}
	default JsonArray readJsonArray(JsonObject j, String target, JsonArray def) {
		return this.readJsonArray(j, target, def, JsonArray.class.getSimpleName());
	}
	default JsonArray readJsonArray(JsonObject j, String target, JsonArray def, String arg) {
		return this.readJsonArray(j, target, def, arg, true);
	}
	default JsonArray readJsonArray(JsonObject j, String target, JsonArray def, String arg, boolean log) {
		return this.readJsonArray(j, target, def, arg, log, false);
	}
	default JsonArray readJsonArray(JsonObject j, String target, JsonArray def, String arg, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadJson.readJsonArray(logger(), j, target, def, arg, log, canBeNull);
	}
	static JsonArray readJsonArray(Logger logger, JsonObject j, String target, JsonArray def, String arg, boolean log, boolean canBeNull) {
		return BJsonUtil.readJsonArray(logger, j, target, def, arg, log, canBeNull);
	}
}
