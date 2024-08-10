package net.w3e.base.json.adapters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.message.MessageUtil;

public abstract class WTypedJsonAdapter<K, V> extends WJsonAdapter<V> {

	private final Map<K, AdapterFactory<V>> MAP = new HashMap<>();

	public static interface AdapterFactory<V> {
		V apply(JsonObject json) throws Exception;
	}

	protected String getName() {
		return this.getClass().getSimpleName();
	}
	protected abstract K parseKey(String key);
	protected V defValue() {
		return null;
	}

	@Override
	public V deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = BJsonUtil.convertToJsonObject(json, this.getName());
		try {
			AdapterFactory<V> factory = this.MAP.get(this.parseKey(jsonObject.get("type").getAsString()));
			if (factory != null) {
				return factory.apply(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.defValue();
	}

	public final void register(K key, AdapterFactory<V> factory) {
		if (this.MAP.containsKey(key)) {
			throw new IllegalStateException(MessageUtil.KEY_DUPLICATE.createMsg(this.MAP.keySet(), key));
		} else {
			this.MAP.put(key, factory);
		}
	}

	public final Set<K> keySet() {
		return this.MAP.keySet();
	}
}
