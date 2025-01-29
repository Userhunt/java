package net.w3e.wlib.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.codec.typed.ConfigType;

public class WJsonTypedTypeAdapter<CT> implements ConfigType<CT> {

	private final String keyName;
	@Getter(onMethod_ = {@Override})
	private final Class<CT> typeClass;

	public WJsonTypedTypeAdapter(String keyName, Class<CT> typeClass) {
		this.keyName = keyName;
		this.typeClass = typeClass;
	}

	@Override
	public final String keyName() {
		return this.keyName;
	}

	public void registerJson() {}

	public static class WJsonAdaptersMap<V extends WJsonRegistryElement> {

		private final Map<String, WJsonTypedTypeAdapter<? extends V>> map = new HashMap<>();
		private WJsonRegistryElement empty;
		private boolean isEmptyInit = true;

		public WJsonAdaptersMap(Class<?> clazz) {
			JsonUtils.addTypedAdapter(clazz, this.map);
		}

		protected V createEmpty() {
			return null;
		}

		@SuppressWarnings("unchecked")
		public final <T extends V> T getEmpty() {
			if (this.isEmptyInit) {
				this.isEmptyInit = false;
				this.empty = this.createEmpty();
			}
			return (T)empty;
		}
		
		public final <T extends V> WJsonTypedTypeAdapter<T> registerConfigType(WJsonTypedTypeAdapter<T> configType) {
			this.map.computeIfAbsent(configType.keyName(), e -> {
				configType.registerJson();
				this.registerAdapter(configType);
				return configType;
			});
			return configType;
		}

		protected void registerAdapter(WJsonTypedTypeAdapter<? extends V> configType) {}

		@SuppressWarnings("unchecked")
		public final <T extends WJsonTypedTypeAdapter<V>> T getConfigType(String keyName) {
			return (T)Objects.requireNonNull(this.map.get(keyName), keyName);
		}
	}

}
