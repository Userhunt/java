package net.w3e.base.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.w3e.base.collection.IdentityLinkedHashMap;
import net.w3e.base.collection.cache.CacheKeys;
import net.w3e.base.collection.cache.CacheKeys.CacheKeysEmpty;
import net.w3e.base.registry.RegistryEntryIns.RegistryValue;

public class Registry<T> {

	public static final IllegalStateException redifine(String name, String old) {
		return new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + old);
	}

	private final Map<String, RegistryEntry<T>> map = this.map();

	private List<RegistryEntry<T>> entrySet = null;
	private List<String> keys = null;
	private List<T> values = null;

	public final String key;
	public final String def;

	public Registry(String key, String def) {
		this.key = key;
		this.def = def;
	}

	protected Map<String, RegistryEntry<T>> map() {
		return new LinkedHashMap<>();
	}

	@SuppressWarnings("unchecked")
	public final RegistryEntry<T> register(String key, T value) {
		RegistryEntry<T> entry;
		if (value instanceof RegistryEntry<?> reg) {
			entry = (RegistryEntry<T>)reg;
		} else {
			entry = new RegistryValue<>(value);
		}
		registerKey(key, entry);
		if (map.containsKey(entry.getRegistryName())) {
			throw new IllegalStateException("duplicate key " + entry.getRegistryName() + " in " + this.key + " registry");
		}
		this.map.put(entry.getRegistryName(), entry);
		this.entrySet = null;
		this.keys = null;
		this.values = null;
		return entry;
	}

	protected void registerKey(String key, RegistryEntry<T> entry) {
		entry.setRegistryName(key);
	}

	public T get(String key) {
		RegistryEntry<T> value = this.map.getOrDefault(key, this.def == null ? null : this.map.get(this.def));
		return value == null ? null : value.get();
	}

	public final List<RegistryEntry<T>> entrySet() {
		if (this.entrySet == null) {
			this.entrySet = ImmutableList.copyOf(this.map.values());
		}
		return this.entrySet;
	}

	public final List<String> keys() {
		if (this.keys == null) {
			this.keys = ImmutableList.copyOf(this.map.keySet());
		}
		return this.keys;
	}

	public final List<T> values() {
		if (this.values == null) {
			this.values = ImmutableList.copyOf(this.map.values().stream().map(RegistryEntry::get).toList());
		}
		return this.values;
	}

	public static class CacheRegistry<T> extends Registry<T> {

		private final CacheKeys<String> keys = new CacheKeysEmpty<>();

		public CacheRegistry(String key, String def) {
			super(key, def);
			new CacheKeysEmpty<>();
		}

		@Override
		protected final Map<String, RegistryEntry<T>> map() {
			return new IdentityLinkedHashMap<>();
		}

		@Override
		protected final void registerKey(String key, RegistryEntry<T> entry) {
			super.registerKey(this.keys.get(key), entry);
		}

		@Override
		public final T get(String key) {
			return super.get(this.keys.get(key));
		}
	}
}
