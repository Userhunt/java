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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.w3e.base.PrintWrapper;
import net.w3e.base.ReflectionUtils;
import net.w3e.base.api.GsonHelper;
import net.w3e.base.collection.ArraySet;
import net.w3e.base.message.BMessageLoggerHelper;
import net.w3e.base.message.MessageUtil;
import net.w3e.base.tuple.WTuple2;

public class BJsonUtil extends GsonHelper {

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
		return MSG_UTIL();
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

	/*========================================= toString ======================================*/

	private static final Map<Class<?>, Function<Object, String>> TO_STRING = new LinkedHashMap<>();

	@SuppressWarnings("unchecked")
	public static final <T> boolean registerToString(Class<T> cl, Function<T, String> function) {
		if (TO_STRING.containsKey(cl)) {
			return false;
		} else {
			TO_STRING.put(cl, (Function<Object, String>)function);
			return true;
		}
	}

	static {
		registerToString(String.class, (obj) -> obj);
	}

	private static boolean customString;

	public static final String applyCustomString(@NotNull Object object) {
		if (customString) {
			Function<Object, String> function = TO_STRING.get(object.getClass());
			if (function != null) {
				return function.apply(object);
			}
		}
		return null;
	}

	public static final String toString(Object object, boolean custom) {
		customString = custom;
		String string = toString(object);
		customString = false;
		return string;
	}

	public static String toString(Object object) {
		if (object == null) {
			return "null";
		}
		if (customString) {
			Function<Object, String> function = TO_STRING.get(object.getClass());
			if (function != null) {
				return function.apply(object);
			}
		}
		if (object instanceof Collection) {
			return toString((Collection<?>)object);
		}
		if (object instanceof Object[]) {
			return toString((Object[])object);
		}
		if (object.getClass().isArray()) {
			if (object instanceof boolean[]) {
				return toString((boolean[])object);
			}
			if (object instanceof byte[]) {
				return toString((byte[])object);
			}
			if (object instanceof short[]) {
				return toString((short[])object);
			}
			if (object instanceof int[]) {
				return toString((int[])object);
			}
			if (object instanceof long[]) {
				return toString((long[])object);
			}
			if (object instanceof float[]) {
				return toString((float[])object);
			}
			if (object instanceof double[]) {
				return toString((double[])object);
			}
		}
		if (object instanceof Map) {
			return toString((Map<?, ?>)object);
		}

		return object.toString();
	}

	public static final <T> String toString(Collection<T> collection) {
		return toString(collection, BJsonUtil::toString);
	}

	public static final <T> String toString(Collection<T> collection, Function<T, String> function) {
		if (collection == null) {
			return null;
		}
		Iterator<T> iterator = collection.iterator();
		String str = "[";
		while (iterator.hasNext()) {
			try {
				String next = function.apply(iterator.next());
				if (next == null) {
					str += "null";
				} else {
					str += next;
				}
				if (iterator.hasNext()) {
					str += ",";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return str + "]";
	}

	public static final <T, V> String toString(Map<T, V> map) {
		return toString(map, (e) -> new WTuple2<>(e.getKey(), e.getValue()));
	}

	public static final <T, V, R, F> String toString(Map<T, V> map, Function<Entry<T, V>, WTuple2<R, F>> function) {
		Map<R, F> m = new HashMap<>();
		for (Entry<T, V> entry : map.entrySet()) {
			WTuple2<R, F> tuple = function.apply(entry);
			m.put(tuple.a, tuple.b);
		}
		String str = "{";
		Iterator<Entry<R, F>> iterator = m.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<R, F> next = iterator.next();
			str += toString(next.getKey()) + ":" + toString(next.getValue());
			if (iterator.hasNext()) {
				str += ",";
			}
		}
		return str + "}";
	}

	public static final String toString(Object[] objects) {
		String str = "[";
		Iterator<Object> iterator = Arrays.asList(objects).iterator();
		while (iterator.hasNext()) {
			str += toString(iterator.next());
			if (iterator.hasNext()) {
				str += ",";
			}
		}
		return str + "]";
	}

	public static final String toString(boolean[] objects) {
		List<Boolean> output = new ArrayList<Boolean>();
		for (boolean value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(byte[] objects) {
		List<Byte> output = new ArrayList<Byte>();
		for (byte value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(short[] objects) {
		List<Short> output = new ArrayList<Short>();
		for (short value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(int[] objects) {
		return toString(Arrays.stream(objects).boxed().collect(Collectors.toList()));
	}

	public static final String toString(long[] objects) {
		return toString(Arrays.stream(objects).boxed().collect(Collectors.toList()));
	}

	public static final String toString(float[] objects) {
		List<Float> output = new ArrayList<Float>();
		for (float value : objects) {
			output.add(value);
		}

		return toString(output);
	}

	public static final String toString(double[] objects) {
		return toString(Arrays.stream(objects).boxed().collect(Collectors.toList()));
	}

	/*========================================= Converter ======================================*/
	public static final short convertToShort(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsShort();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Short.class.getSimpleName(), getType(element)));
		}
	}

	public static final double convertToDouble(JsonElement element, String target) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsDouble();
		} else {
			throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(target, Double.class.getSimpleName(), getType(element)));
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

	public static final <T> T convertToObject(@Nullable JsonElement p_188179_0_, JsonDeserializationContext p_188179_2_, Class<? extends T> p_188179_3_) {
		String last = "";
		try {
			last = lastSubMessage().toString();
		} catch (Exception ignored) {}
		return convertToObject(p_188179_0_, last, p_188179_2_, p_188179_3_);
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

	public static final JsonArray readJsonArray(Logger logger, JsonObject j, String target, JsonArray def, String arg, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToJsonArray, arg, log, canBeNull);
	}

	public static final JsonElement readJsonElement(Logger logger, JsonObject j, String target, JsonElement def, String arg, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, (e, s) -> Objects.requireNonNull(e), arg, log, canBeNull);
	}

	public static final JsonObject readJsonObject(Logger logger, JsonObject j, String target, JsonObject def, String arg, boolean log, boolean canBeNull) {
		return readObject(logger, j, target, def, BJsonUtil::convertToJsonObject, arg, log, canBeNull);
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
							throwMessage(logger, MessageUtil.KEY_DUPLICATE.createMsg(toString(list), el));
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
				throwMessage(logger, new JsonSyntaxException(MessageUtil.EXPECTED_SET.createMsg(target, type, null, toString(list))));
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
							throwMessage(logger, MessageUtil.KEY_DUPLICATE.createMsg(toString(list), el));
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
							throwMessage(logger, MessageUtil.KEY_DUPLICATE.createMsg(toString(list), el));
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

	@SuppressWarnings("deprecation")
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
}
