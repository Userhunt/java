package net.w3e.base.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.jetbrains.annotations.Nullable;

import net.w3e.base.BStringUtil;
import net.w3e.base.PrintWrapper;
import net.w3e.base.ReflectionUtils;
import net.w3e.base.collection.ArraySet;
import net.w3e.base.message.BMessageLoggerHelper;
import net.w3e.base.message.MessageUtil;

public class BJsonUtil {

	private static Logger DEFAULT_LOGGER = PrintWrapper.LOGGER;

	public static final void setDefaultLoggeer(Logger logger) {
		if (logger != null && DEFAULT_LOGGER != PrintWrapper.LOGGER) {
			DEFAULT_LOGGER = logger;
		}
	}

	private static BMessageLoggerHelper DEFAULT_MSG = PrintWrapper.MSG_UTIL;

	public static final void setDefaultMsgUtil(BMessageLoggerHelper logger) {
		if (logger != null && DEFAULT_MSG != PrintWrapper.MSG_UTIL) {
			DEFAULT_MSG = logger;
		}
	}

	public static final BMessageLoggerHelper MSG_UTIL() {
		return DEFAULT_MSG;
	}

	public static final Logger LOGGER() {
		return DEFAULT_LOGGER;
	}


	private static final List<Object> subMessages = new ArrayList<>();

	private static final List<Class<? extends Exception>> ignoredException = new ArraySet<>();

	private static final List<Class<? extends Exception>> knownException = new ArraySet<>();

	private static boolean lock = false;

	static {
		addIgnoredException(JsonSyntaxException.class);
		addKnownException(JsonSyntaxException.class);
	}
	public static final boolean addIgnoredException(Class<? extends Exception> e) {
		return ignoredException.add(e);
	}

	public static final boolean addKnownException(Class<? extends Exception> e) {
		return knownException.add(e);
	}

	public static final void addSubMessage(Object subMessage) {
		if (subMessage != null) {
			subMessages.add(subMessage);
		}
	}
	public static final void addSubMessageCollection(int index) {
		addSubMessage("element[" + index + "]");
	}

	public static final void removeSubMessage() {
		if (!subMessages.isEmpty()) {
			subMessages.remove(subMessages.size() - 1);
		}
	}

	public static final void clearSubMessage() {
		clearSubMessage(null);
	}

	public static final void clearSubMessage(Boolean lock) {
		if (BJsonUtil.lock && lock == null) {
			return;
		}
		if (lock != null) {
			if (BJsonUtil.lock == lock) {
				return;
			}
			BJsonUtil.lock = lock;
		}

		subMessages.clear();
	}

	public static final Object lastSubMessage() {
		return subMessages.isEmpty() ? "" : subMessages.get(subMessages.size() - 1);
	}

	public static final void throwMessage(Logger logger, Exception last) {
		if (ignoredException.contains(last.getClass())) {
			throwMessage(logger, last.toString());
			return;
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		last.printStackTrace(pw);
		throwMessage(logger, sw.toString());
	}

	public static final void throwMessage(Logger logger, String last) {
		throwMessage(logger, last, 3);
	}

	public static final void throwMessage(Logger logger, String last, int i) {
		String d = "  ";
		String d1 = "";
		if (!subMessages.isEmpty()) {
			String arg = "\n";

			for (Object object : subMessages) {
				arg += d1 + object + "\n";
				d1 += d;
			}

			last = arg + d1 + last + "\n";
		} else {
			last = "\n" + last;
		}
		while(true) {
			StackTraceElement stack = StackLocatorUtil.getStackTraceElement(i);
			if (stack != null) {
				try {
					if (ReflectionUtils.instaceOf(Class.forName(stack.getClassName()), BJsonUtil.class)) {
						i++;
						continue;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				String string = stack.toString();
				string = string.substring(string.lastIndexOf("/") + 1);
				last = string + last;
				break;
			}
		}
		logger.warn(new IllegalArgumentException(last));
	}

	public static final String getException(Exception e, Supplier<String> get) {
		if (knownException.contains(e.getClass())) {
			return e.toString();
		} else {
			return new JsonSyntaxException(get.get()).toString();
		}
	}

	/*========================================= GSON ======================================*/
	public static GsonBuilder GSON() {
		return new GsonBuilder();
	}

	public static final <T> T load(Gson gson, JsonElement data, Class<T> t) {
		return load(gson, data, t, t);
	}

	public static final <T, R> R load(Gson gson, JsonElement data, Type t, Class<R> r) {
		try {
			Object object = gson.fromJson(data, (Type) t);
			return r.cast(object);
		} catch (JsonParseException e) {
			throw e;
		}
	}

	public static final <T> T deepCopy(T object, Class<T> type) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(gson.toJson(object, type), type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static final <T> T deepCopy(T object) {
		try {
			Gson gson = new Gson();
			return (T)gson.fromJson(gson.toJson(object, object.getClass()), object.getClass());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static final <T extends JsonElement> T read(byte[] bytes) {
		try {
			return (T)JsonParser.parseString(new String(bytes));
		} catch (Exception e) {}
		return null;
	}

	/*========================================= Getter ======================================*/

	public static final boolean getAsBoolean(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToBoolean(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Boolean.class.getSimpleName(), getType(element)));
		}
	}

	public static final byte getAsByte(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToByte(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Byte.class.getSimpleName(), getType(element)));
		}
	}

	public static final short getAsShort(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToShort(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Short.class.getSimpleName(), getType(element)));
		}
	}

	public static final int getAsInt(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToInt(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Integer.class.getSimpleName(), getType(element)));
		}
	}

	public static final long getAsLong(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToLong(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Long.class.getSimpleName(), getType(element)));
		}
	}

	public static final float getAsFloat(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToFloat(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Float.class.getSimpleName(), getType(element)));
		}
	}

	public static final double getAsDouble(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToDouble(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Double.class.getSimpleName(), getType(element)));
		}
	}

	public static final String getAsString(JsonObject element, String target) {
		if (element.has(target)) {
			return convertToString(element.get(target), target);
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, String.class.getSimpleName(), getType(element)));
		}
	}
	
	/*========================================= Converter ======================================*/

	private static final String abbreviateMiddle(String str, String middle, int length) {
		if (Strings.isNullOrEmpty(str) || Strings.isNullOrEmpty(middle)) {
			return str;
		}

		if (length >= str.length() || length < (middle.length()+2)) {
			return str;
		}

		int targetSting = length-middle.length();
		int startOffset = targetSting/2+targetSting%2;
		int endOffset = str.length()-targetSting/2;

		StringBuilder builder = new StringBuilder(length);
		builder.append(str.substring(0,startOffset));
		builder.append(middle);
		builder.append(str.substring(endOffset));

		return builder.toString();
	}

	public static final String getType(@Nullable JsonElement p_13884_) {
		String s = abbreviateMiddle(String.valueOf((Object) p_13884_), "...", 10);
		if (p_13884_ == null) {
			return "null (missing)";
		} else if (p_13884_.isJsonNull()) {
			return "null (json)";
		} else if (p_13884_.isJsonArray()) {
			return "an array (" + s + ")";
		} else if (p_13884_.isJsonObject()) {
			return "an object (" + s + ")";
		} else {
			if (p_13884_.isJsonPrimitive()) {
				JsonPrimitive jsonprimitive = p_13884_.getAsJsonPrimitive();
				if (jsonprimitive.isNumber()) {
					return "a number (" + s + ")";
				}

				if (jsonprimitive.isBoolean()) {
					return "a boolean (" + s + ")";
				}
			}

			return s;
		}
	}

	public static final boolean convertToBoolean(JsonElement element, String target) {
		if (element.isJsonPrimitive()) {
			return element.getAsBoolean();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Boolean.class.getSimpleName(), getType(element)));
		}
	}

	public static final byte convertToByte(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsByte();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Byte.class.getSimpleName(), getType(element)));
		}
	}

	public static final short convertToShort(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsShort();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Short.class.getSimpleName(), getType(element)));
		}
	}

	public static final int convertToInt(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsInt();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Integer.class.getSimpleName(), getType(element)));
		}
	}

	public static final long convertToLong(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsLong();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Long.class.getSimpleName(), getType(element)));
		}
	}

	public static final float convertToFloat(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsFloat();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Float.class.getSimpleName(), getType(element)));
		}
	}

	public static final double convertToDouble(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsDouble();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Double.class.getSimpleName(), getType(element)));
		}
	}

	public static String convertToString(JsonElement element, String target) {
		if (element.isJsonPrimitive()) {
			return element.getAsString();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, String.class.getSimpleName(), getType(element)));
		}
	}

	public static final UUID convertToUUID(JsonElement element, String target) {
		return UUID.fromString(convertToString(element, target));
	}

	public static final String[] decompose(String[] def, String str, char c) {
		String[] asString = Arrays.copyOf(def, def.length);
		int i = str.indexOf(c);
		if (i >= 0) {
			asString[1] = str.substring(i + 1, str.length());
			if (i >= 1) {
				asString[0] = str.substring(0, i);
			}
		}

		return asString;
	}

	public static JsonObject convertToJsonObject(JsonElement element, String target) {
		if (element.isJsonObject()) {
			return element.getAsJsonObject();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, JsonObject.class.getSimpleName(), getType(element)));
		}
	}

	public static JsonArray convertToJsonArray(JsonElement element, String target) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, JsonArray.class.getSimpleName(), getType(element)));
		}
	}

	public static final <T> T convertToObject(@Nullable JsonElement element, JsonDeserializationContext context, Class<? extends T> type) {
		String last = "";
		try {
			last = lastSubMessage().toString();
		} catch (Exception ignored) {}
		return convertToObject(element, last, context, type);
	}

	public static <T> T convertToObject(@Nullable JsonElement element, String arg, JsonDeserializationContext context, Class<? extends T> type) {
		if (element != null) {
			return context.deserialize(element, type);
		} else {
			throw new JsonSyntaxException("Missing " + arg);
		}
	}

	/*========================================= readObject ======================================*/
	public static final Boolean readBoolean(Logger logger, JsonObject j, String target, Boolean def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToBoolean, "boolean", log, canBeNull);
	}

	public static final Byte readByte(Logger logger, JsonObject j, String target, Byte def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToByte, "byte", log, canBeNull);
	}

	public static final Short readShort(Logger logger, JsonObject j, String target, Short def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToShort, "short", log, canBeNull);
	}

	public static final Integer readInt(Logger logger, JsonObject j, String target, Integer def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToInt, "int", log, canBeNull);
	}

	public static final Long readLong(Logger logger, JsonObject j, String target, Long def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToLong, "int", log, canBeNull);
	}

	public static final Float readFloat(Logger logger, JsonObject j, String target, Float def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToFloat, "float", log, canBeNull);
	}

	public static final Double readDouble(Logger logger, JsonObject j, String target, Double def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToDouble, "double", log, canBeNull);
	}

	public static final String readString(Logger logger, JsonObject j, String target, String def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToString, "String", log, canBeNull);
	}

	public static final UUID readUUID(Logger logger, JsonObject j, String target, UUID def, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToUUID, "UUID", log, canBeNull);
	}

	public static final <T extends Enum<T>> T readEnum(Logger logger, JsonObject j, String target, T def, Function<String, T> function, String type, boolean log, boolean canBeNull) {
		String arg = BJsonUtil.readString(logger, j, target, def != null ? def.toString() : null, false, true);
		try {
			return function.apply(arg);
		} catch (Exception e) {
			if (log) {
				throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, type, arg)));
			}
			return def;
		}
	}

	public static final JsonArray readJsonArray(Logger logger, JsonObject j, String target, JsonArray def, String type, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToJsonArray, type, log, canBeNull);
	}

	public static final JsonElement readJsonElement(Logger logger, JsonObject j, String target, JsonElement def, String type, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, (e, s) -> Objects.requireNonNull(e), type, log, canBeNull);
	}

	public static final JsonObject readJsonObject(Logger logger, JsonObject j, String target, JsonObject def, String type, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToJsonObject, type, log, canBeNull);
	}

	public static final <T> T readObject(Logger logger, JsonObject j, String target, T def, BiFunction<JsonElement, String, T> function, String type, boolean log, boolean canBeNull) {
		addSubMessage(target);
		if (j.has(target)) {
			JsonElement arg = j.get(target);
			if (canBeNull && arg.isJsonNull()) {
				removeSubMessage();
				return null;
			}
			try {
				T ret = function.apply(arg, target);
				removeSubMessage();
				return ret;
			} catch (Exception e) {
				throwMessage(logger, e);
			}
		} else {
			if (canBeNull) {
				removeSubMessage();
				return def;
			} else if (log) {
				throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED_SET.createMsg(target, type, null, def)));
			}
		}
		removeSubMessage();
		return def;
	}

	/*========================================= convertObject ======================================*/
	public static final Boolean convertBoolean(Logger logger, JsonElement j, Boolean def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToBoolean, "boolean", log, canBeNull);
	}

	public static final Byte convertByte(Logger logger, JsonElement j, Byte def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToByte, "byte", log, canBeNull);
	}

	public static final Short convertShort(Logger logger, JsonElement j, Short def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToShort, "short", log, canBeNull);
	}

	public static final Integer convertInt(Logger logger, JsonElement j, Integer def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToInt, "int", log, canBeNull);
	}

	public static final Long convertLong(Logger logger, JsonElement j, Long def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToLong, "int", log, canBeNull);
	}

	public static final Float convertFloat(Logger logger, JsonElement j, Float def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToFloat, "float", log, canBeNull);
	}

	public static final Double convertDouble(Logger logger, JsonElement j, Double def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToDouble, "double", log, canBeNull);
	}

	public static final String convertString(Logger logger, JsonElement j, String def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToString, "String", log, canBeNull);
	}

	public static final UUID convertUUID(Logger logger, JsonElement j, UUID def, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToUUID, "UUID", log, canBeNull);
	}

	public static final <T extends Enum<T>> T convertEnum(Logger logger, JsonElement j, T def, Function<String, T> function, String type, boolean log, boolean canBeNull) {
		String arg = BJsonUtil.convertString(logger, j, def != null ? def.toString() : null, false, true);
		try {
			return function.apply(arg);
		} catch (Exception e) {
			if (log) {
				throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(j, type, arg)));
			}
			return def;
		}
	}

	public static final JsonArray convertJsonArray(Logger logger, JsonElement j, JsonArray def, String arg, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToJsonArray, arg, log, canBeNull);
	}

	public static final JsonElement convertJsonElement(Logger logger, JsonElement j, JsonElement def, String arg, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, (e, s) -> Objects.requireNonNull(e), arg, log, canBeNull);
	}

	public static final JsonObject convertJsonObject(Logger logger, JsonElement j, JsonObject def, String arg, boolean log, boolean canBeNull) {
		return convertObject(logger, j, def, BJsonUtil::convertToJsonObject, arg, log, canBeNull);
	}

	public static final <T> T convertObject(Logger logger, JsonElement j, T def, BiFunction<JsonElement, String, T> function, String type, boolean log, boolean canBeNull) {
		boolean nill = (j == null || j.isJsonNull());
		if (nill) {
			return canBeNull ? null : def;
		}
		try {
			return function.apply(j, "");
		} catch (Exception e) {
			throwMessage(logger, e);
		}
		return def;
	}

	/*========================================= readCollection ======================================*/
	public static final <T, V extends Collection<T>> V readCollection(V list, Logger logger, JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		addSubMessage(target);
		JsonElement element = j.get(target);
		if (element != null) {
			if (element instanceof JsonArray) {
				int index = 0;
				for (JsonElement jsonElement : element.getAsJsonArray()) {
					addSubMessageCollection(index);
					index++;
					try {
						T el = function.apply(jsonElement);
						if (!list.add(el)) {
							throwMessage(logger, MessageUtil.KEY_DUPLICATE.createMsg(BStringUtil.toString(list), el));
						}
					} catch (Exception e) {
						throwMessage(logger, e);
					}
					removeSubMessage();
				}
			} else {
				try {
					list.add(function.apply(element));
				} catch (Exception e) {
					throwMessage(logger, e);
				}
			}
		} else {
			if (log) {
				throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED_SET.createMsg(target, type, null, BStringUtil.toString(list))));
			}
		}
		removeSubMessage();
		return list;
	}

	public static final <T> List<T> readList(Logger logger, JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return readCollection(new ArrayList<>(), logger, j, target, function, type, log);
	}

	public static final <T> Set<T> readSet(Logger logger, JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return readCollection(new HashSet<>(), logger, j, target, function, type, log);
	}

	public static final <T> ArraySet<T> readArraySet(Logger logger, JsonObject j, String target, Function<JsonElement, T> function, String type, boolean log) {
		return readCollection(new ArraySet<>(), logger, j, target, function, type, log);
	}

	@SuppressWarnings("unchecked")
	public static final <T, V extends Collection<T>> V readCollection(V list, Logger logger, JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		addSubMessage(key);
		JsonElement target = j.get(key);
		if (target != null) {
			Class<T> tClass = (Class<T>)t.getClass().getComponentType();
			if (target instanceof JsonArray) {
				int index = 0;
				for (JsonElement jsonElement : target.getAsJsonArray()) {
					addSubMessageCollection(index);
					index++;
					try {
						T el = convertToObject(jsonElement, key, context, tClass);
						if (!list.add(el)) {
							throwMessage(logger, MessageUtil.KEY_DUPLICATE.createMsg(BStringUtil.toString(list), el));
						}
					} catch (Exception e) {
						throwMessage(logger, e);
					}
					removeSubMessage();
				}
			} else {
				try {
					list.add(convertToObject(target, key, context, tClass));
				} catch (Exception e) {
					throwMessage(logger, e);
				}
			}
		} else if (log) {
			throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED_SET.createMsg(target, (Class<T[]>)t.getClass(), null, "[]")));
		}

		removeSubMessage();
		return list;
	}

	public static final <T> List<T> readList(Logger logger, JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return readCollection(new ArrayList<>(), logger, j, key, t, context, log);
	}

	public static final <T> Set<T> readSet(Logger logger, JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return readCollection(new HashSet<>(), logger, j, key, t, context, log);
	}

	public static final <T> ArraySet<T> readArraySet(Logger logger, JsonObject j, String key, T[] t, JsonDeserializationContext context, boolean log) {
		return readCollection(new ArraySet<>(), logger, j, key, t, context, log);
	}

	public static final <T, V, R extends Map<T, V>> R readMap(R map, Logger logger, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType, boolean log) {
		addSubMessage(key);
		JsonElement target = j.get(key);
		if (target != null) {
			if (target instanceof JsonObject object) {
				int index = 0;
				T k;
				V v;
				for (Entry<String, JsonElement> entry : object.entrySet()) {
					addSubMessageCollection(index);
					index++;
					try {
						k = keyFunction.apply(entry.getKey());
					} catch (Exception e) {
						if (e instanceof JsonSyntaxException) {
							throwMessage(logger, e);
						} else {
							throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(entry.getKey(), keyType, "unknown")));
						}
						removeSubMessage();
						continue;
					}
					try {
						v = valueFunction.apply(entry.getValue());
					} catch (Exception e) {
						JsonElement element = entry.getValue();
						if (e instanceof JsonSyntaxException) {
							throwMessage(logger, e);
						} else {
							throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(element, valueType, getType(element))));
						}
						removeSubMessage();
						continue;
					}
					if (map.containsKey(k)) {
						throwMessage(logger, new JsonSyntaxException(MessageUtil.KEY_DUPLICATE.createMsg(k, map.keySet())));
					} else {
						map.put(k, v);
					}
					removeSubMessage();
				}
			} else {
				throwMessage(logger, MessageUtil.EXPECTED.createMsg(key, JsonObject.class.getSimpleName(), getType(target)));
			}
		} else if (log) {
			throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED_SET.createMsg(target, JsonObject.class.getSimpleName(), null, "{}")));
		}
		removeSubMessage();
		return map;
	}

	public static final <T, V> HashMap<T, V> readHashMap(Logger logger, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType, boolean log) {
		return readMap(new HashMap<>(), logger, j, key, keyFunction, valueFunction, keyType, valueType, log);
	}

	public static final <T, V> LinkedHashMap<T, V> readLinkedHashMap(Logger logger, JsonObject j, String key, Function<String, T> keyFunction, Function<JsonElement, V> valueFunction, String keyType, String valueType, boolean log) {
		return readMap(new LinkedHashMap<>(), logger, j, key, keyFunction, valueFunction, keyType, valueType, log);
	}

	/*========================================= convertCollection ======================================*/
	public static final <T, V extends Collection<T>> V convertCollection(V list, Logger logger, JsonElement j, Function<JsonElement, T> function, String type, boolean log) {
		if (j != null) {
			if (j instanceof JsonArray) {
				int index = 0;
				for (JsonElement jsonElement : j.getAsJsonArray()) {
					addSubMessageCollection(index);
					index++;
					try {
						T el = function.apply(jsonElement);
						if (!list.add(el)) {
							throwMessage(logger, "cant add " + j + " to " + list);
						}
					} catch (Exception e) {
						throwMessage(logger, e);
					}
					removeSubMessage();
				}
			} else {
				try {
					list.add(function.apply(j));
				} catch (Exception e) {
					throwMessage(logger, e);
				}
			}
		} else {
			if (log) {
				throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED_SET.createMsg(j, type, null, "[]")));
			}
		}
		return list;
	}

	public static final <T> List<T> convertList(Logger logger, JsonElement j, Function<JsonElement, T> function, String type, boolean log) {
		return convertCollection(new ArrayList<>(), logger, j, function, type, log);
	}

	public static final <T> Set<T> convertSet(Logger logger, JsonElement j, Function<JsonElement, T> function, String type, boolean log) {
		return convertCollection(new HashSet<>(), logger, j, function, type, log);
	}

	public static final <T> ArraySet<T> convertArraySet(Logger logger, JsonElement j, Function<JsonElement, T> function, String type, boolean log) {
		return convertCollection(new ArraySet<>(), logger, j, function, type, log);
	}

	@SuppressWarnings("unchecked")
	public static final <T, V extends Collection<T>> V convertCollection(V list, Logger logger, JsonElement j, T[] t, JsonDeserializationContext context, boolean log) {
		if (j != null) {
			Class<T> tClass = (Class<T>)t.getClass().getComponentType();
			if (j instanceof JsonArray) {
				int index = 0;
				for (JsonElement jsonElement : j.getAsJsonArray()) {
					addSubMessageCollection(index);
					index++;
					try {
						T el = convertToObject(jsonElement, context, tClass);
						if (!list.add(el)) {
							throwMessage(logger, MessageUtil.KEY_DUPLICATE.createMsg(BStringUtil.toString(list), el));
						}
					} catch (Exception e) {
						throwMessage(logger, e);
					}
					removeSubMessage();
				}
			} else {
				addSubMessageCollection(0);
				try {
					list.add(convertToObject(j, context, tClass));
				} catch (Exception e) {
					throwMessage(logger, e);
				}
			}
		} else {
			if (log) {
				throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED_SET.createMsg(j, (Class<T[]>)t.getClass(), null, "[]")));
			}
		}
		return list;
	}

	public static final <T> List<T> convertList(Logger logger, JsonElement j, T[] t, JsonDeserializationContext context, boolean log) {
		return convertCollection(new ArrayList<>(), logger, j, t, context, log);
	}

	public static final <T> Set<T> convertSet(Logger logger, JsonElement j, T[] t, JsonDeserializationContext context, boolean log) {
		return convertCollection(new HashSet<>(), logger, j, t, context, log);
	}

	public static final <T> ArraySet<T> convertArraySet(Logger logger, JsonElement j, T[] t, JsonDeserializationContext context, boolean log) {
		return convertCollection(new ArraySet<>(), logger, j, t, context, log);
	}

	/*========================================= string ======================================*/

	protected static final String tab = "	";

	public static final String toPrettyString(JsonElement element) {
		return toPrettyString(element, "");
	}

	public static final String toPrettyString(JsonElement element, String d) {
		if (element.isJsonPrimitive()) {
			return element.toString();
		}
		String prev = d;
		d += tab;
		if (element.isJsonObject()) {
			String string = "{";
			Set<Entry<String, JsonElement>> set = element.getAsJsonObject().entrySet();
			Iterator<Entry<String, JsonElement>> iterator = set.iterator();

			boolean bl = iterator.hasNext() && set.size() != 1;

			if (bl) {
				string += "\n";
			}

			while (iterator.hasNext()) {
				Entry<String, JsonElement> next = iterator.next();
				JsonElement value = next.getValue();
				if (!bl && !value.isJsonNull() && !value.isJsonPrimitive()) {
					string += "\n";
					bl = true;
				}
				string += (bl ? d : "") + "\"" + next.getKey() + "\": " + toPrettyString(value, d);
				if (iterator.hasNext()) {
					string += ",";
				}
				if (bl) {
					string += "\n";
				}
			}

			if (bl) {
				string += prev;
			}

			string += "}";

			return string;
		}
		if (element.isJsonArray()) {
			String string = "[";

			JsonArray array = element.getAsJsonArray();
			Iterator<JsonElement> iterator = array.iterator();

			if (iterator.hasNext()) {
				boolean primitiveFlags[] = {false, false, false};

				boolean primitive = true;
				for (JsonElement jsonElement : array) {
					if (jsonElement instanceof JsonPrimitive js) {
						if (js.isBoolean()) {
							primitiveFlags[0] = true;
						} else if (js.isNumber()) {
							primitiveFlags[1] = true;
						} else if (js.isString()) {
							primitiveFlags[2] = true;
						}
					} else {
						primitive = false;
						break;
					}
				}
				if (primitive) {
					int i = (primitiveFlags[0] ? 1 : 0) + (primitiveFlags[1] ? 1 : 0) + (primitiveFlags[2] ? 1 : 0);
					if (i != 1) {
						primitive = false;
					}
				}
				if (!primitive) {
					string += "\n";
				}

				while (iterator.hasNext()) {
					if (!primitive) {
						string += d;
					}
					string += toPrettyString(iterator.next(), d);
					if (iterator.hasNext()) {
						string += ",";
					}
					if (primitive) {
						if (iterator.hasNext()) {
							string += " ";
						}
					} else {
						string += "\n";
					}
				}

				if (!primitive) {
					string += prev;
				}
			}

			string += "]";
			return string;
		}
		if (element.isJsonNull()) {
			return null;
		}
		return "w3e";
	}

	/*========================================= array ======================================*/
	public static final <T> T[] readArray(Logger logger, JsonObject j, String name, Class<T> clazz, T[] def, BiFunction<JsonElement, String, T> function, boolean log) {
		if (j.has(name)) {
			return readArray(logger, j.getAsJsonArray(name), clazz, name, function, log);
		} else {
			return def;
		}
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] readArray(Logger logger, JsonArray jsonArray, Class<T> clazz, String type, BiFunction<JsonElement, String, T> function, boolean log) {
		T[] array = (T[])Array.newInstance(clazz, jsonArray.size());

		int i = 0;
		for (JsonElement element : jsonArray) {
			addSubMessageCollection(i);
			try {
				array[i] = function.apply(element, type);
			} catch (Exception e) {
				if (log) {
					throwMessage(logger, e);
				}
			}
			i++;
			removeSubMessage();
		}

		return array;
	}


	public static final double[] readDoubleArrayUnsetSize(JsonObject j, String name, int length) {
		return readDoubleArrayUnsetSize(j, name, length, true);
	}

	public static final double[] readDoubleArrayUnsetSize(JsonObject j, String name, int length, boolean log) {
		return readDoubleArrayUnsetSize(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final double[] readDoubleArrayUnsetSize(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			JsonArray array = j.getAsJsonArray(name);
			return readDoubleArray(logger, array, array.size(), log);
		} else {
			return (double[])Array.newInstance(double.class, length);
		}
	}

	public static final double[] readDoubleArray(JsonObject j, String name, int length) {
		return readDoubleArray(j, name, length, true);
	}

	public static final double[] readDoubleArray(JsonObject j, String name, int length, boolean log) {
		return readDoubleArray(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final double[] readDoubleArray(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			return readDoubleArray(logger, j.getAsJsonArray(name), length, log);
		} else {
			return (double[])Array.newInstance(double.class, length);
		}
	}

	public static final double[] readDoubleArray(JsonArray jsonArray, int length) {
		return readDoubleArray(jsonArray, length, true);
	}

	public static final double[] readDoubleArray(JsonArray jsonArray, int length, boolean log) {
		return readDoubleArray(DEFAULT_LOGGER, jsonArray, length, log);
	}

	public static final double[] readDoubleArray(Logger logger, JsonArray jsonArray, int length, boolean log) {
		double[] array = new double[length];
		Double[] arr = readArray(logger, jsonArray, Double.class, "double", BJsonUtil::convertToDouble, log);
		int i = 0;
		for (Double d : arr) {
			array[i] = d.doubleValue();
			i++;
		}
		if (array.length != length) {
			array = Arrays.copyOf(array, length);
		}
		return array;
	}


	public static final byte[] readByteArrayUnsetSize(JsonObject j, String name, int length) {
		return readByteArrayUnsetSize(j, name, length, true);
	}

	public static final byte[] readByteArrayUnsetSize(JsonObject j, String name, int length, boolean log) {
		return readByteArrayUnsetSize(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final byte[] readByteArrayUnsetSize(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			JsonArray array = j.getAsJsonArray(name);
			return readByteArray(logger, array, array.size(), log);
		} else {
			return (byte[])Array.newInstance(byte.class, length);
		}
	}

	public static final byte[] readByteArray(JsonObject j, String name, int length) {
		return readByteArray(j, name, length, true);
	}

	public static final byte[] readByteArray(JsonObject j, String name, int length, boolean log) {
		return readByteArray(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final byte[] readByteArray(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			return readByteArray(logger, j.getAsJsonArray(name), length, log);
		} else {
			return (byte[])Array.newInstance(int.class, length);
		}
	}

	public static final byte[] readByteArray(JsonArray jsonArray, int length) {
		return readByteArray(jsonArray, length, true);
	}

	public static final byte[] readByteArray(JsonArray jsonArray, int length, boolean log) {
		return readByteArray(DEFAULT_LOGGER, jsonArray, length, log);
	}

	public static final byte[] readByteArray(Logger logger, JsonArray jsonArray, int length, boolean log) {
		byte[] array = new byte[length];
		Byte[] arr = readArray(logger, jsonArray, Byte.class, "int", BJsonUtil::convertToByte, log);
		int i = 0;
		for (Byte d : arr) {
			array[i] = d.byteValue();
			i++;
		}
		if (array.length != length) {
			array = Arrays.copyOf(array, length);
		}
		return array;
	}


	public static final int[] readIntArrayUnsetSize(JsonObject j, String name, int length) {
		return readIntArrayUnsetSize(j, name, length, true);
	}

	public static final int[] readIntArrayUnsetSize(JsonObject j, String name, int length, boolean log) {
		return readIntArrayUnsetSize(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final int[] readIntArrayUnsetSize(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			JsonArray array = j.getAsJsonArray(name);
			return readIntArray(logger, array, array.size(), log);
		} else {
			return (int[])Array.newInstance(int.class, length);
		}
	}

	public static final int[] readIntArray(JsonObject j, String name, int length) {
		return readIntArray(j, name, length, true);
	}

	public static final int[] readIntArray(JsonObject j, String name, int length, boolean log) {
		return readIntArray(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final int[] readIntArray(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			return readIntArray(logger, j.getAsJsonArray(name), length, log);
		} else {
			return (int[])Array.newInstance(int.class, length);
		}
	}

	public static final int[] readIntArray(JsonArray jsonArray, int length) {
		return readIntArray(jsonArray, length, true);
	}

	public static final int[] readIntArray(JsonArray jsonArray, int length, boolean log) {
		return readIntArray(DEFAULT_LOGGER, jsonArray, length, log);
	}

	public static final int[] readIntArray(Logger logger, JsonArray jsonArray, int length, boolean log) {
		int[] array = new int[length];
		Integer[] arr = readArray(logger, jsonArray, Integer.class, "int", BJsonUtil::convertToInt, log);
		int i = 0;
		for (Integer d : arr) {
			array[i] = d.intValue();
			i++;
		}
		if (array.length != length) {
			array = Arrays.copyOf(array, length);
		}
		return array;
	}


	public static final float[] readFloatArrayUnsetSize(JsonObject j, String name, int length) {
		return readFloatArrayUnsetSize(j, name, length, true);
	}

	public static final float[] readFloatArrayUnsetSize(JsonObject j, String name, int length, boolean log) {
		return readFloatArrayUnsetSize(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final float[] readFloatArrayUnsetSize(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			JsonArray array = j.getAsJsonArray(name);
			return readFloatArray(logger, array, array.size(), log);
		} else {
			return (float[])Array.newInstance(float.class, length);
		}
	}

	public static final float[] readFloatArray(JsonObject j, String name, int length) {
		return readFloatArray(j, name, length, true);
	}

	public static final float[] readFloatArray(JsonObject j, String name, int length, boolean log) {
		return readFloatArray(DEFAULT_LOGGER, j, name, length, log);
	}

	public static final float[] readFloatArray(Logger logger, JsonObject j, String name, int length, boolean log) {
		if (j.has(name)) {
			return readFloatArray(logger, j.getAsJsonArray(name), length, log);
		} else {
			return (float[])Array.newInstance(int.class, length);
		}
	}

	public static final float[] readFloatArray(JsonArray jsonArray, int length) {
		return readFloatArray(jsonArray, length, true);
	}

	public static final float[] readFloatArray(JsonArray jsonArray, int length, boolean log) {
		return readFloatArray(DEFAULT_LOGGER, jsonArray, length, log);
	}

	public static final float[] readFloatArray(Logger logger, JsonArray jsonArray, int length, boolean log) {
		float[] array = new float[length];
		Float[] arr = readArray(logger, jsonArray, Float.class, "float", BJsonUtil::convertToFloat, log);
		int i = 0;
		for (Float d : arr) {
			array[i] = d.intValue();
			i++;
		}
		if (array.length != length) {
			array = Arrays.copyOf(array, length);
		}
		return array;
	}

	public static final int[] toArray(int[] base, BJsonHelper helper, JsonObject jsonObject, String key, JsonDeserializationContext context) {
		return toArray(base, helper.readList(jsonObject, key, new Integer[0], context, false));
	}

	public static final int[] toArray(int[] base, List<Integer> list) {
		if (list.contains(null)) {
			list = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
		}
		if (!list.isEmpty()) {
			base = new int[list.size()];
			for (int i = 0; i < base.length; i++) {
				base[i] = list.get(i);
			}
			return base;
		}
		return Arrays.copyOf(base, base.length);
	}

	/*
	public static boolean isStringValue(JsonObject p_13814_, String p_13815_) {
		return !isValidPrimitive(p_13814_, p_13815_) ? false : p_13814_.getAsJsonPrimitive(p_13815_).isString();
	}

	public static boolean isStringValue(JsonElement p_13804_) {
		return !p_13804_.isJsonPrimitive() ? false : p_13804_.getAsJsonPrimitive().isString();
	}

	public static boolean isNumberValue(JsonObject p_144763_, String p_144764_) {
		return !isValidPrimitive(p_144763_, p_144764_) ? false : p_144763_.getAsJsonPrimitive(p_144764_).isNumber();
	}

	public static boolean isNumberValue(JsonElement p_13873_) {
		return !p_13873_.isJsonPrimitive() ? false : p_13873_.getAsJsonPrimitive().isNumber();
	}

	public static boolean isBooleanValue(JsonObject p_13881_, String p_13882_) {
		return !isValidPrimitive(p_13881_, p_13882_) ? false : p_13881_.getAsJsonPrimitive(p_13882_).isBoolean();
	}

	public static boolean isBooleanValue(JsonElement p_144768_) {
		return !p_144768_.isJsonPrimitive() ? false : p_144768_.getAsJsonPrimitive().isBoolean();
	}

	public static boolean isArrayNode(JsonObject p_13886_, String p_13887_) {
		return !isValidNode(p_13886_, p_13887_) ? false : p_13886_.get(p_13887_).isJsonArray();
	}

	public static boolean isObjectNode(JsonObject p_144773_, String p_144774_) {
		return !isValidNode(p_144773_, p_144774_) ? false : p_144773_.get(p_144774_).isJsonObject();
	}

	public static boolean isValidPrimitive(JsonObject p_13895_, String p_13896_) {
		return !isValidNode(p_13895_, p_13896_) ? false : p_13895_.get(p_13896_).isJsonPrimitive();
	}

	public static boolean isValidNode(JsonObject p_13901_, String p_13902_) {
		if (p_13901_ == null) {
			return false;
		} else {
			return p_13901_.get(p_13902_) != null;
		}
	}

	@Contract("_,_,!null->!null;_,_,null->_")
	public static String getAsString(JsonObject p_13852_, String p_13853_, @Nullable String p_13854_) {
		return p_13852_.has(p_13853_) ? convertToString(p_13852_.get(p_13853_), p_13853_) : p_13854_;
	}

	public static boolean getAsBoolean(JsonObject p_13856_, String p_13857_, boolean p_13858_) {
		return p_13856_.has(p_13857_) ? convertToBoolean(p_13856_.get(p_13857_), p_13857_) : p_13858_;
	}

	public static double getAsDouble(JsonObject p_144743_, String p_144744_, double p_144745_) {
		return p_144743_.has(p_144744_) ? convertToDouble(p_144743_.get(p_144744_), p_144744_) : p_144745_;
	}

	public static float getAsFloat(JsonObject p_13821_, String p_13822_, float p_13823_) {
		return p_13821_.has(p_13822_) ? convertToFloat(p_13821_.get(p_13822_), p_13822_) : p_13823_;
	}

	public static long getAsLong(JsonObject p_13829_, String p_13830_, long p_13831_) {
		return p_13829_.has(p_13830_) ? convertToLong(p_13829_.get(p_13830_), p_13830_) : p_13831_;
	}

	public static int getAsInt(JsonObject p_13825_, String p_13826_, int p_13827_) {
		return p_13825_.has(p_13826_) ? convertToInt(p_13825_.get(p_13826_), p_13826_) : p_13827_;
	}

	public static byte getAsByte(JsonObject p_13817_, String p_13818_, byte p_13819_) {
		return p_13817_.has(p_13818_) ? convertToByte(p_13817_.get(p_13818_), p_13818_) : p_13819_;
	}

	@SuppressWarnings("deprecation")
	public static char convertToCharacter(JsonElement p_144776_, String p_144777_) {
		if (p_144776_.isJsonPrimitive() && p_144776_.getAsJsonPrimitive().isNumber()) {
			return p_144776_.getAsCharacter();
		} else {
			throw new JsonSyntaxException("Expected " + p_144777_ + " to be a Character, was " + getType(p_144776_));
		}
	}

	public static char getAsCharacter(JsonObject p_144794_, String p_144795_) {
		if (p_144794_.has(p_144795_)) {
			return convertToCharacter(p_144794_.get(p_144795_), p_144795_);
		} else {
			throw new JsonSyntaxException("Missing " + p_144795_ + ", expected to find a Character");
		}
	}

	public static char getAsCharacter(JsonObject p_144739_, String p_144740_, char p_144741_) {
		return p_144739_.has(p_144740_) ? convertToCharacter(p_144739_.get(p_144740_), p_144740_) : p_144741_;
	}

	public static BigDecimal convertToBigDecimal(JsonElement p_144779_, String p_144780_) {
		if (p_144779_.isJsonPrimitive() && p_144779_.getAsJsonPrimitive().isNumber()) {
			return p_144779_.getAsBigDecimal();
		} else {
			throw new JsonSyntaxException("Expected " + p_144780_ + " to be a BigDecimal, was " + getType(p_144779_));
		}
	}

	public static BigDecimal getAsBigDecimal(JsonObject p_144797_, String p_144798_) {
		if (p_144797_.has(p_144798_)) {
			return convertToBigDecimal(p_144797_.get(p_144798_), p_144798_);
		} else {
			throw new JsonSyntaxException("Missing " + p_144798_ + ", expected to find a BigDecimal");
		}
	}

	public static BigDecimal getAsBigDecimal(JsonObject p_144751_, String p_144752_, BigDecimal p_144753_) {
		return p_144751_.has(p_144752_) ? convertToBigDecimal(p_144751_.get(p_144752_), p_144752_) : p_144753_;
	}

	public static BigInteger convertToBigInteger(JsonElement p_144782_, String p_144783_) {
		if (p_144782_.isJsonPrimitive() && p_144782_.getAsJsonPrimitive().isNumber()) {
			return p_144782_.getAsBigInteger();
		} else {
			throw new JsonSyntaxException("Expected " + p_144783_ + " to be a BigInteger, was " + getType(p_144782_));
		}
	}

	public static BigInteger getAsBigInteger(JsonObject p_144800_, String p_144801_) {
		if (p_144800_.has(p_144801_)) {
			return convertToBigInteger(p_144800_.get(p_144801_), p_144801_);
		} else {
			throw new JsonSyntaxException("Missing " + p_144801_ + ", expected to find a BigInteger");
		}
	}

	public static BigInteger getAsBigInteger(JsonObject p_144755_, String p_144756_, BigInteger p_144757_) {
		return p_144755_.has(p_144756_) ? convertToBigInteger(p_144755_.get(p_144756_), p_144756_) : p_144757_;
	}

	public static short getAsShort(JsonObject p_144803_, String p_144804_) {
		if (p_144803_.has(p_144804_)) {
			return convertToShort(p_144803_.get(p_144804_), p_144804_);
		} else {
			throw new JsonSyntaxException("Missing " + p_144804_ + ", expected to find a Short");
		}
	}

	public static short getAsShort(JsonObject p_144759_, String p_144760_, short p_144761_) {
		return p_144759_.has(p_144760_) ? convertToShort(p_144759_.get(p_144760_), p_144760_) : p_144761_;
	}

	public static JsonObject convertToJsonObject(JsonElement p_13919_, String p_13920_) {
		if (p_13919_.isJsonObject()) {
			return p_13919_.getAsJsonObject();
		} else {
			throw new JsonSyntaxException("Expected " + p_13920_ + " to be a JsonObject, was " + getType(p_13919_));
		}
	}

	public static JsonObject getAsJsonObject(JsonObject p_13931_, String p_13932_) {
		if (p_13931_.has(p_13932_)) {
			return convertToJsonObject(p_13931_.get(p_13932_), p_13932_);
		} else {
			throw new JsonSyntaxException("Missing " + p_13932_ + ", expected to find a JsonObject");
		}
	}

	@Nullable
	@Contract("_,_,!null->!null;_,_,null->_")
	public static JsonObject getAsJsonObject(JsonObject p_13842_, String p_13843_, @Nullable JsonObject p_13844_) {
		return p_13842_.has(p_13843_) ? convertToJsonObject(p_13842_.get(p_13843_), p_13843_) : p_13844_;
	}

	public static JsonArray convertToJsonArray(JsonElement p_13925_, String p_13926_) {
		if (p_13925_.isJsonArray()) {
			return p_13925_.getAsJsonArray();
		} else {
			throw new JsonSyntaxException("Expected " + p_13926_ + " to be a JsonArray, was " + getType(p_13925_));
		}
	}

	public static JsonArray getAsJsonArray(JsonObject p_13934_, String p_13935_) {
		if (p_13934_.has(p_13935_)) {
			return convertToJsonArray(p_13934_.get(p_13935_), p_13935_);
		} else {
			throw new JsonSyntaxException("Missing " + p_13935_ + ", expected to find a JsonArray");
		}
	}

	@Nullable
	@Contract("_,_,!null->!null;_,_,null->_")
	public static JsonArray getAsJsonArray(JsonObject p_13833_, String p_13834_, @Nullable JsonArray p_13835_) {
		return p_13833_.has(p_13834_) ? convertToJsonArray(p_13833_.get(p_13834_), p_13834_) : p_13835_;
	}

	public static <T> T convertToObject(@Nullable JsonElement p_13809_, String p_13810_,
			JsonDeserializationContext p_13811_, Class<? extends T> p_13812_) {
		if (p_13809_ != null) {
			return p_13811_.deserialize(p_13809_, p_13812_);
		} else {
			throw new JsonSyntaxException("Missing " + p_13810_);
		}
	}

	public static <T> T getAsObject(JsonObject p_13837_, String p_13838_, JsonDeserializationContext p_13839_,
			Class<? extends T> p_13840_) {
		if (p_13837_.has(p_13838_)) {
			return convertToObject(p_13837_.get(p_13838_), p_13838_, p_13839_, p_13840_);
		} else {
			throw new JsonSyntaxException("Missing " + p_13838_);
		}
	}

	@Nullable
	@Contract("_,_,!null,_,_->!null;_,_,null,_,_->_")
	public static <T> T getAsObject(JsonObject p_13846_, String p_13847_, @Nullable T p_13848_,
			JsonDeserializationContext p_13849_, Class<? extends T> p_13850_) {
		return (T) (p_13846_.has(p_13847_) ? convertToObject(p_13846_.get(p_13847_), p_13847_, p_13849_, p_13850_)
				: p_13848_);
	}

	public static String getType(@Nullable JsonElement p_13884_) {
		String s = abbreviateMiddle(String.valueOf((Object) p_13884_), "...", 10);
		if (p_13884_ == null) {
			return "null (missing)";
		} else if (p_13884_.isJsonNull()) {
			return "null (json)";
		} else if (p_13884_.isJsonArray()) {
			return "an array (" + s + ")";
		} else if (p_13884_.isJsonObject()) {
			return "an object (" + s + ")";
		} else {
			if (p_13884_.isJsonPrimitive()) {
				JsonPrimitive jsonprimitive = p_13884_.getAsJsonPrimitive();
				if (jsonprimitive.isNumber()) {
					return "a number (" + s + ")";
				}

				if (jsonprimitive.isBoolean()) {
					return "a boolean (" + s + ")";
				}
			}

			return s;
		}
	}

	private static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	private static String abbreviateMiddle(String str, String middle, int length) {
		if (isEmpty(str) || isEmpty(middle)) {
			return str;
		}

		if (length >= str.length() || length < (middle.length()+2)) {
			return str;
		}

		int targetSting = length-middle.length();
		int startOffset = targetSting/2+targetSting%2;
		int endOffset = str.length()-targetSting/2;

		StringBuilder builder = new StringBuilder(length);
		builder.append(str.substring(0,startOffset));
		builder.append(middle);
		builder.append(str.substring(endOffset));

		return builder.toString();
	}

	@Nullable
	public static <T> T fromNullableJson(Gson p_13781_, Reader p_13782_, Class<T> p_13783_, boolean p_13784_) {
		try {
			JsonReader jsonreader = new JsonReader(p_13782_);
			jsonreader.setLenient(p_13784_);
			return p_13781_.getAdapter(p_13783_).read(jsonreader);
		} catch (IOException ioexception) {
			throw new JsonParseException(ioexception);
		}
	}

	public static <T> T fromJson(Gson p_263516_, Reader p_263522_, Class<T> p_263539_, boolean p_263489_) {
		T t = fromNullableJson(p_263516_, p_263522_, p_263539_, p_263489_);
		if (t == null) {
			throw new JsonParseException("JSON data was null or empty");
		} else {
			return t;
		}
	}

	@Nullable
	public static <T> T fromNullableJson(Gson p_13772_, Reader p_13773_, TypeToken<T> p_13774_, boolean p_13775_) {
		try {
			JsonReader jsonreader = new JsonReader(p_13773_);
			jsonreader.setLenient(p_13775_);
			return p_13772_.getAdapter(p_13774_).read(jsonreader);
		} catch (IOException ioexception) {
			throw new JsonParseException(ioexception);
		}
	}

	public static <T> T fromJson(Gson p_263499_, Reader p_263527_, TypeToken<T> p_263525_, boolean p_263507_) {
		T t = fromNullableJson(p_263499_, p_263527_, p_263525_, p_263507_);
		if (t == null) {
			throw new JsonParseException("JSON data was null or empty");
		} else {
			return t;
		}
	}

	@Nullable
	public static <T> T fromNullableJson(Gson p_13790_, String p_13791_, TypeToken<T> p_13792_, boolean p_13793_) {
		return fromNullableJson(p_13790_, new StringReader(p_13791_), p_13792_, p_13793_);
	}

	public static <T> T fromJson(Gson p_263492_, String p_263488_, Class<T> p_263503_, boolean p_263506_) {
		return fromJson(p_263492_, new StringReader(p_263488_), p_263503_, p_263506_);
	}

	@Nullable
	public static <T> T fromNullableJson(Gson p_13799_, String p_13800_, Class<T> p_13801_, boolean p_13802_) {
		return fromNullableJson(p_13799_, new StringReader(p_13800_), p_13801_, p_13802_);
	}

	public static <T> T fromJson(Gson p_13768_, Reader p_13769_, TypeToken<T> p_13770_) {
		return fromJson(p_13768_, p_13769_, p_13770_, false);
	}

	@Nullable
	public static <T> T fromNullableJson(Gson p_13786_, String p_13787_, TypeToken<T> p_13788_) {
		return fromNullableJson(p_13786_, p_13787_, p_13788_, false);
	}

	public static <T> T fromJson(Gson p_13777_, Reader p_13778_, Class<T> p_13779_) {
		return fromJson(p_13777_, p_13778_, p_13779_, false);
	}

	public static <T> T fromJson(Gson p_13795_, String p_13796_, Class<T> p_13797_) {
		return fromJson(p_13795_, p_13796_, p_13797_, false);
	}

	public static JsonObject parse(String p_13870_, boolean p_13871_) {
		return parse(new StringReader(p_13870_), p_13871_);
	}

	public static JsonObject parse(Reader p_13862_, boolean p_13863_) {
		return fromJson(GSON, p_13862_, JsonObject.class, p_13863_);
	}

	public static JsonObject parse(String p_13865_) {
		return parse(p_13865_, false);
	}

	public static JsonObject parse(Reader p_13860_) {
		return parse(p_13860_, false);
	}

	public static JsonArray parseArray(String p_216215_) {
		return parseArray(new StringReader(p_216215_));
	}

	public static JsonArray parseArray(Reader p_144766_) {
		return fromJson(GSON, p_144766_, JsonArray.class, false);
	}

	public static String toStableString(JsonElement p_216217_) {
		StringWriter stringwriter = new StringWriter();
		JsonWriter jsonwriter = new JsonWriter(stringwriter);

		try {
			writeValue(jsonwriter, p_216217_, Comparator.naturalOrder());
		} catch (IOException ioexception) {
			throw new AssertionError(ioexception);
		}

		return stringwriter.toString();
	}

	public static void writeValue(JsonWriter p_216208_, @Nullable JsonElement p_216209_,
			@Nullable Comparator<String> p_216210_) throws IOException {
		if (p_216209_ != null && !p_216209_.isJsonNull()) {
			if (p_216209_.isJsonPrimitive()) {
				JsonPrimitive jsonprimitive = p_216209_.getAsJsonPrimitive();
				if (jsonprimitive.isNumber()) {
					p_216208_.value(jsonprimitive.getAsNumber());
				} else if (jsonprimitive.isBoolean()) {
					p_216208_.value(jsonprimitive.getAsBoolean());
				} else {
					p_216208_.value(jsonprimitive.getAsString());
				}
			} else if (p_216209_.isJsonArray()) {
				p_216208_.beginArray();

				for (JsonElement jsonelement : p_216209_.getAsJsonArray()) {
					writeValue(p_216208_, jsonelement, p_216210_);
				}

				p_216208_.endArray();
			} else {
				if (!p_216209_.isJsonObject()) {
					throw new IllegalArgumentException("Couldn't write " + p_216209_.getClass());
				}

				p_216208_.beginObject();

				for (Map.Entry<String, JsonElement> entry : sortByKeyIfNeeded(p_216209_.getAsJsonObject().entrySet(),
						p_216210_)) {
					p_216208_.name(entry.getKey());
					writeValue(p_216208_, entry.getValue(), p_216210_);
				}

				p_216208_.endObject();
			}
		} else {
			p_216208_.nullValue();
		}

	}

	private static Collection<Map.Entry<String, JsonElement>> sortByKeyIfNeeded(
			Collection<Map.Entry<String, JsonElement>> p_216212_, @Nullable Comparator<String> p_216213_) {
		if (p_216213_ == null) {
			return p_216212_;
		} else {
			List<Map.Entry<String, JsonElement>> list = new ArrayList<>(p_216212_);
			list.sort(Entry.comparingByKey(p_216213_));
			return list;
		}
	}
	 */
}
