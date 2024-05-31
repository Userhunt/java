package net.w3e.base.json.system.read;

import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.system.BaseJsonHelper;

public interface BaseJsonHelperReadPrimitive extends BaseJsonHelper {

	default Boolean readBoolean(JsonObject j, String target, Boolean def) {
		return this.readBoolean(j, target, def, true);
	}
	default Boolean readBoolean(JsonObject j, String target, Boolean def, boolean log) {
		return this.readBoolean(j, target, def, log, false);
	}
	default Boolean readBoolean(JsonObject j, String target, Boolean def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readBoolean(logger(), j, target, def, log, canBeNull);
	}
	static Boolean readBoolean(Logger logger, JsonObject j, String target, Boolean def, boolean log, boolean canBeNull) {
		return BJsonUtil.readBoolean(logger, j, target, def, log, canBeNull);
	}

	default Byte readByte(JsonObject j, String target, Byte def) {
		return this.readByte(j, target, def, true);
	}
	default Byte readByte(JsonObject j, String target, Byte def, boolean log) {
		return this.readByte(j, target, def, log, false);
	}
	default Byte readByte(JsonObject j, String target, Byte def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readByte(logger(), j, target, def, log, canBeNull);
	}
	static Byte readByte(Logger logger, JsonObject j, String target, Byte def, boolean log, boolean canBeNull) {
		return BJsonUtil.readByte(logger, j, target, def, log, canBeNull);
	}

	default Short readShort(JsonObject j, String target, Short def) {
		return this.readShort(j, target, def, true);
	}
	default Short readShort(JsonObject j, String target, Short def, boolean log) {
		return this.readShort(j, target, def, log, false);
	}
	default Short readShort(JsonObject j, String target, Short def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readShort(logger(), j, target, def, log, canBeNull);
	}
	static Short readShort(Logger logger, JsonObject j, String target, Short def, boolean log, boolean canBeNull) {
		return BJsonUtil.readShort(logger, j, target, def, log, canBeNull);
	}

	default Integer readInt(JsonObject j, String target, Integer def) {
		return this.readInt(j, target, def, true);
	}
	default Integer readInt(JsonObject j, String target, Integer def, boolean log) {
		return this.readInt(j, target, def, log, false);
	}
	default Integer readInt(JsonObject j, String target, Integer def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readInt(logger(), j, target, def, log, canBeNull);
	}
	static Integer readInt(Logger logger, JsonObject j, String target, Integer def, boolean log, boolean canBeNull) {
		return BJsonUtil.readInt(logger, j, target, def, log, canBeNull);
	}

	default Long readLong(JsonObject j, String target, Long def) {
		return this.readLong(j, target, def, true);
	}
	default Long readLong(JsonObject j, String target, Long def, boolean log) {
		return this.readLong(j, target, def, log, false);
	}
	default Long readLong(JsonObject j, String target, Long def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readLong(logger(), j, target, def, log, canBeNull);
	}
	static Long readLong(Logger logger, JsonObject j, String target, Long def, boolean log, boolean canBeNull) {
		return BJsonUtil.readLong(logger, j, target, def, log, canBeNull);
	}

	default Float readFloat(JsonObject j, String target, Float def) {
		return this.readFloat(j, target, def, true);
	}
	default Float readFloat(JsonObject j, String target, Float def, boolean log) {
		return this.readFloat(j, target, def, log, false);
	}
	default Float readFloat(JsonObject j, String target, Float def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readFloat(logger(), j, target, def, log, canBeNull);
	}
	static Float readFloat(Logger logger, JsonObject j, String target, Float def, boolean log, boolean canBeNull) {
		return BJsonUtil.readFloat(logger, j, target, def, log, canBeNull);
	}

	default Double readDouble(JsonObject j, String target, Double def) {
		return this.readDouble(j, target, def, true);
	}
	default Double readDouble(JsonObject j, String target, Double def, boolean log) {
		return this.readDouble(j, target, def, log, false);
	}
	default Double readDouble(JsonObject j, String target, Double def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readDouble(logger(), j, target, def, log, canBeNull);
	}
	static Double readDouble(Logger logger, JsonObject j, String target, Double def, boolean log, boolean canBeNull) {
		return BJsonUtil.readDouble(logger, j, target, def, log, canBeNull);
	}

	default String readString(JsonObject j, String target, String def) {
		return this.readString(j, target, def, true);
	}
	default String readString(JsonObject j, String target, String def, boolean log) {
		return this.readString(j, target, def, log, false);
	}
	default String readString(JsonObject j, String target, String def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readString(logger(), j, target, def, log, canBeNull);
	}
	static String readString(Logger logger, JsonObject j, String target, String def, boolean log, boolean canBeNull) {
		return BJsonUtil.readString(logger, j, target, def, log, canBeNull);
	}

	default UUID readUUID(JsonObject j, String target, UUID def) {
		return this.readUUID(j, target, def, true);
	}
	default UUID readUUID(JsonObject j, String target, UUID def, boolean log) {
		return this.readUUID(j, target, def, log, false);
	}
	default UUID readUUID(JsonObject j, String target, UUID def, boolean log, boolean canBeNull) {
		return BaseJsonHelperReadPrimitive.readUUID(logger(), j, target, def, log, canBeNull);
	}
	static UUID readUUID(Logger logger, JsonObject j, String target, UUID def, boolean log, boolean canBeNull) {
		return BJsonUtil.readUUID(logger, j, target, def, log, canBeNull);
	}
}
