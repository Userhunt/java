package net.w3e.app.api.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.w3e.app.api.registry.RegistryEntryIns.RegistryValue;
import net.w3e.wlib.collection.IdentityLinkedHashMap;
import net.w3e.wlib.collection.cache.CacheKeys;
import net.w3e.wlib.collection.cache.CacheKeys.CacheKeysEmpty;

public class Registry<T> {

	public static final IllegalStateException redifine(String name, String old) {
		return new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + old);
	}

	private final Map<String, RegistryEntry<T>> map = this.map();

	private Collection<RegistryEntry<T>> entrySet = null;
	private Collection<String> keys = null;
	private Collection<T> values = null;

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

	@SuppressWarnings("unchecked")
	protected final RegistryEntry<T> toEntry(T value) {
		RegistryEntry<T> entry;
		if (value instanceof RegistryEntry<?> reg) {
			entry = (RegistryEntry<T>)reg;
		} else {
			entry = new RegistryValue<>(value);
		}
		return entry;
	}

	@SuppressWarnings("unchecked")
	protected final RegistryEntry<T> toEntry(String key, T value) {
		RegistryEntry<T> entry;
		if (value instanceof RegistryEntry<?> reg) {
			entry = (RegistryEntry<T>)reg;
		} else {
			entry = new RegistryValue<>(value);
		}
		return entry;
	}

	protected void registerKey(String key, RegistryEntry<T> entry) {
		entry.setRegistryName(key);
	}

	public final T get(String key) {
		RegistryEntry<T> value = getEntry(key);
		return value == null ? null : value.get();
	}

	protected RegistryEntry<T> getEntry(String key) {
		return this.map.getOrDefault(key, this.def == null ? null : this.map.get(this.def));
	}

	protected final T remove(String key) {
		RegistryEntry<T> value = this.map.remove(key);
		if (value != null) {
			this.entrySet = null;
			this.keys = null;
			this.values = null;
		}
		return value == null ? null : value.get();
	}

	public final Collection<RegistryEntry<T>> entrySet() {
		if (this.entrySet == null) {
			this.entrySet = Collections.unmodifiableCollection(this.map.values());
		}
		return this.entrySet;
	}

	public final Collection<String> keys() {
		if (this.keys == null) {
			this.keys = Collections.unmodifiableCollection(this.map.keySet());
		}
		return this.keys;
	}

	public final Collection<T> values() {
		if (this.values == null) {
			this.values = Collections.unmodifiableCollection(this.map.values().stream().map(RegistryEntry::get).toList());
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
		protected final RegistryEntry<T> getEntry(String key) {
			return super.getEntry(this.keys.getNoCache(key));
		}
	}
}
