package net.w3e.base.json.adapters;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.message.MessageUtil;

public abstract class WTypedJsonAdapter<K, V> extends WJsonAdapter<V> {

	protected final Map<K, SimpleJsonAdapter<V>> map = new HashMap<>();

	public static interface SimpleJsonAdapter<V> {
		V apply(JsonObject json, JsonDeserializationContext context) throws Exception;
	}

	public static interface JsonAdapterA<V, A> {
		V apply(JsonObject json, A a, JsonDeserializationContext context) throws Exception;
	}

	public static interface JsonAdapterB<V, A, B> {
		V apply(JsonObject json, A a, B b, JsonDeserializationContext context) throws Exception;
	}

	protected String getName() {
		return this.getClass().getSimpleName();
	}
	protected abstract K parseKey(String key);
	protected V defValue() {
		return null;
	}

	@Override
	public final V deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = BJsonUtil.convertToJsonObject(json, this.getName());
		try {
			String keyString = jsonObject.get("type").getAsString();
			K key = this.parseKey(keyString);
			SimpleJsonAdapter<V> factory = null;
			for (Entry<K, SimpleJsonAdapter<V>> entry : this.map.entrySet()) {
				if (entry.getKey().equals(key)) {
					key = entry.getKey();
					factory = entry.getValue();
					break;
				}
			}
			if (factory != null) {
				return withKey(keyString, factory.apply(jsonObject, context));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.defValue();
	}

	public final void register(K key, SimpleJsonAdapter<V> factory) {
		if (!this.canReplace(key) && this.map.containsKey(key)) {
			throw new IllegalStateException(MessageUtil.KEY_DUPLICATE.createMsg(this.map.keySet(), key));
		} else {
			this.map.put(key, factory);
		}
	}

	public final void register(K key, Class<?> clazz) {
		this.register(key, (json, context) -> {
			return context.deserialize(json, clazz);
		});
	}

	public final <A> void register(K key, Class<A> dataA, JsonAdapterA<V, A> factory) {
		this.register(key, (json, context) -> {
			A a = context.deserialize(json, dataA);
			return factory.apply(json, a, context);
		});
	}

	public final <A, B> void register(K key, Class<A> dataA, Class<B> dataB, JsonAdapterB<V, A, B> factory) {
		this.register(key, (json, context) -> {
			A a = context.deserialize(json, dataA);
			B b = context.deserialize(json, dataB);
			return factory.apply(json, a, b, context);
		});
	}

	protected final V withKey(String key, V value) {
		if (value instanceof WJsonKeyHolder keyHolder) {
			return keyHolder.setTypeKey(key);
		}
		return value;
	}

	public final Set<K> keySet() {
		return this.map.keySet();
	}

	protected boolean canReplace(K key) {
		return false;
	}

	public static interface WJsonKeyHolder {
		<VALUE> VALUE setTypeKey(String key);
	}

	public final <M, T extends WTypedJsonAdapter<M, V>> T mapKeyTo(T adapter, Function<K, M> function) {
		for (Entry<K, SimpleJsonAdapter<V>> entry : this.map.entrySet()) {
			adapter.register(function.apply(entry.getKey()), entry.getValue());
		}
		return adapter;
	}

	public WTypedJsonAdapter<K, V> copy() {
		WTypedJsonAdapter<K, V> self = this;
		WTypedJsonAdapter<K, V> copy = new WTypedJsonAdapter<K, V>() {
			@Override
			protected final K parseKey(String key) {
				return self.parseKey(key);
			}

			@Override
			protected final boolean canReplace(K key) {
				return self.canReplace(key);
			}
		};
		copy.map.putAll(this.map);
		return copy;
	}
}
