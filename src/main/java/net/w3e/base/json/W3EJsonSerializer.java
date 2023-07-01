package net.w3e.base.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.w3e.base.message.MessageUtil;

/**
 * 12.04.23
 */
@SuppressWarnings("deprecation")
public abstract class W3EJsonSerializer<T> implements JsonDeserializer<T>, JsonSerializer<T> {

	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		return JsonNull.INSTANCE;
	}

	public final <V> V deserialize(BJsonHelper helper, V def, JsonObject jsonObject, String key, Class<V> typeOfT, JsonDeserializationContext context) {
		return deserialize(helper, def, jsonObject, key, true, typeOfT, context);
	}

	public final <V> V deserialize(BJsonHelper helper, V def, JsonObject jsonObject, String key, boolean log, Class<V> typeOfT, JsonDeserializationContext context) {
		return deserialize(helper, def, jsonObject, key, key, log, typeOfT, context);
	}

	public final <V> V deserialize(BJsonHelper helper, V def, JsonObject jsonObject, String key, String type, boolean log, Class<V> typeOfT, JsonDeserializationContext context) {
		JsonElement json = helper.readJsonElement(jsonObject, key, null, type, log, true);
		if (json == null) {
			if (log) {
				helper.throwMessage(MessageUtil.EXPECTED_SET.createMsg(key, type, null, def));
			}
			return def;
		}

		return context.deserialize(json, typeOfT);
	}
}