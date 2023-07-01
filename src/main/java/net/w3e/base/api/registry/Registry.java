package net.w3e.base.api.registry;

import java.util.LinkedHashMap;
import java.util.Map;

public class Registry<T extends RegistryEntry<T>> {

	private final Map<String, T> map = new LinkedHashMap<>();

	public final String key;
	public final String def;

	public Registry(String key, String def) {
		this.key = key;
		this.def = def;
	}
	
	public final T register(String key, T value) {
		value.setRegistryName(key);
		if (map.containsKey(key)) {
			throw new IllegalStateException("duplicate key " + key + " in " + this.key + " registry");
		}
		return value;
	}

	public final T get(String key) {
		return this.map.getOrDefault(key, this.def == null ? null : this.map.get(this.def));
	}
}
