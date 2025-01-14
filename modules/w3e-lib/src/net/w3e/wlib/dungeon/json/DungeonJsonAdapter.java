package net.w3e.wlib.dungeon.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.codec.typed.ConfigType;

@AllArgsConstructor
public class DungeonJsonAdapter<CT> implements ConfigType<CT> {

	private final String keyName;
	@Getter(onMethod_ = {@Override})
	private final Class<CT> typeClass;

	@Override
	public final String keyName() {
		return this.keyName;
	}

	public void registerJson() {}

	public static class DungeonJsonAdaptersMap {

		private final Map<String, DungeonJsonAdapter<?>> map = new HashMap<>();

		public DungeonJsonAdaptersMap(Class<?> clazz) {
			JsonUtils.addTypedAdapter(clazz, this.map);
		}
		
		public final void registerConfigType(DungeonJsonAdapter<?> configType) {
			this.map.computeIfAbsent(configType.keyName(), e -> {
				configType.registerJson();
				this.registerAdapter(configType);
				return configType;
			});
		}

		protected void registerAdapter(DungeonJsonAdapter<?> configType) {}

		public final DungeonJsonAdapter<?> getConfigType(String keyName) {
			return Objects.requireNonNull(this.map.get(keyName), keyName);
		}
	}
}
