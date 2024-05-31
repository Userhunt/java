package net.w3e.base.json.system.convert;

import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperConvertPrimitive extends BaseJsonHelper {

	Logger logger();

	default Boolean convertBoolean(JsonElement j, Boolean def) {
		return this.convertBoolean(j, def, true);
	}
	default Boolean convertBoolean(JsonElement j, Boolean def, boolean log) {
		return this.convertBoolean(j, def, log, false);
	}
	default Boolean convertBoolean(JsonElement j, Boolean def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertBoolean(logger(), j, def, log, canBeNull);
	}
	static Boolean convertBoolean(Logger logger, JsonElement j, Boolean def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertBoolean(logger, j, def, log, canBeNull);
	}

	default Byte convertByte(JsonElement j, Byte def) {
		return this.convertByte(j, def, true);
	}
	default Byte convertByte(JsonElement j, Byte def, boolean log) {
		return this.convertByte(j, def, log, false);
	}
	default Byte convertByte(JsonElement j, Byte def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertByte(logger(), j, def, log, canBeNull);
	}
	static Byte convertByte(Logger logger, JsonElement j, Byte def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertByte(logger, j, def, log, canBeNull);
	}

	default Short convertShort(JsonElement j, Short def) {
		return this.convertShort(j, def, true);
	}
	default Short convertShort(JsonElement j, Short def, boolean log) {
		return this.convertShort(j, def, log, false);
	}
	default Short convertShort(JsonElement j, Short def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertShort(logger(), j, def, log, canBeNull);
	}
	static Short convertShort(Logger logger, JsonElement j, Short def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertShort(logger, j, def, log, canBeNull);
	}

	default Integer convertInt(JsonElement j, Integer def) {
		return this.convertInt(j, def, true);
	}
	default Integer convertInt(JsonElement j, Integer def, boolean log) {
		return this.convertInt(j, def, log, false);
	}
	default Integer convertInt(JsonElement j, Integer def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertInt(logger(), j, def, log, canBeNull);
	}
	static Integer convertInt(Logger logger, JsonElement j, Integer def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertInt(logger, j, def, log, canBeNull);
	}

	default Long convertLong(JsonElement j, Long def) {
		return this.convertLong(j, def, true);
	}
	default Long convertLong(JsonElement j, Long def, boolean log) {
		return this.convertLong(j, def, log, false);
	}
	default Long convertLong(JsonElement j, Long def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertLong(logger(), j, def, log, canBeNull);
	}
	static Long convertLong(Logger logger, JsonElement j, Long def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertLong(logger, j, def, log, canBeNull);
	}

	default Float convertFloat(JsonElement j, Float def) {
		return this.convertFloat(j, def, true);
	}
	default Float convertFloat(JsonElement j, Float def, boolean log) {
		return this.convertFloat(j, def, log, false);
	}
	default Float convertFloat(JsonElement j, Float def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertFloat(logger(), j, def, log, canBeNull);
	}
	static Float convertFloat(Logger logger, JsonElement j, Float def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertFloat(logger, j, def, log, canBeNull);
	}

	default Double convertDouble(JsonElement j, Double def) {
		return this.convertDouble(j, def, true);
	}
	default Double convertDouble(JsonElement j, Double def, boolean log) {
		return this.convertDouble(j, def, log, false);
	}
	default Double convertDouble(JsonElement j, Double def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertDouble(logger(), j, def, log, canBeNull);
	}
	static Double convertDouble(Logger logger, JsonElement j, Double def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertDouble(logger, j, def, log, canBeNull);
	}

	default String convertString(JsonElement j, String def) {
		return this.convertString(j, def, true);
	}
	default String convertString(JsonElement j, String def, boolean log) {
		return this.convertString(j, def, log, false);
	}
	default String convertString(JsonElement j, String def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertString(logger(), j, def, log, canBeNull);
	}
	static String convertString(Logger logger, JsonElement j, String def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertString(logger, j, def, log, canBeNull);
	}

	default UUID convertUUID(JsonElement j, UUID def) {
		return this.convertUUID(j, def, true);
	}
	default UUID convertUUID(JsonElement j, UUID def, boolean log) {
		return this.convertUUID(j, def, log, false);
	}
	default UUID convertUUID(JsonElement j, UUID def, boolean log, boolean canBeNull) {
		return BaseJsonHelperConvertPrimitive.convertUUID(logger(), j, def, log, canBeNull);
	}
	static UUID convertUUID(Logger logger, JsonElement j, UUID def, boolean log, boolean canBeNull) {
		return BJsonUtil.convertUUID(logger, j, def, log, canBeNull);
	}
}
