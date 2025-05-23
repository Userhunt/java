package net.w3e.wlib.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.codec.typed.ConfigType;

public record WJsonTypedTypeAdapter<CT>(String keyName, Class<CT> getTypeClass) implements ConfigType<CT> {

	public static class WJsonAdaptersMap<V extends WJsonRegistryElement> {

		private final Map<String, WJsonTypedTypeAdapter<? extends V>> map = new HashMap<>();
		private final Set<String> keySet = Collections.unmodifiableSet(this.map.keySet());
		private Set<Class<? extends V>> values;
		private WJsonRegistryElement empty;
		private boolean isEmptyInit = true;

		public WJsonAdaptersMap(Class<?> clazz) {
			JsonUtils.addTypedAdapter(clazz, this.map);
		}

		protected V createEmpty() {
			return null;
		}

		public final Set<String> keySet() {
			return this.keySet;
		}

		@SuppressWarnings("unchecked")
		public final Set<Class<? extends V>> values() {
			if (this.values == null) {
				this.values = Set.of(this.map.values().stream().map(e -> e.getTypeClass).toArray(Class[]::new));
			}
			return this.values;
		}

		@SuppressWarnings("unchecked")
		public final <T extends V> T getEmpty() {
			if (this.isEmptyInit) {
				this.isEmptyInit = false;
				this.empty = this.createEmpty();
			}
			return (T)empty;
		}

		public final <T extends V> WJsonTypedTypeAdapter<T> registerConfigType(String keyName, Class<T> configClass) {
			return registerConfigType(new WJsonTypedTypeAdapter<>(keyName, configClass));
		}

		public final <T extends V> WJsonTypedTypeAdapter<T> registerConfigType(WJsonTypedTypeAdapter<T> configType) {
			this.map.computeIfAbsent(configType.keyName(), _ -> {
				this.values = null;
				this.registerAdapter(configType);
				return configType;
			});
			return configType;
		}

		protected void registerAdapter(WJsonTypedTypeAdapter<? extends V> configType) {}

		@SuppressWarnings("unchecked")
		public final WJsonTypedTypeAdapter<V> getConfigType(String keyName) {
			return (WJsonTypedTypeAdapter<V>)Objects.requireNonNull(this.map.get(keyName), keyName);
		}
	}

}
