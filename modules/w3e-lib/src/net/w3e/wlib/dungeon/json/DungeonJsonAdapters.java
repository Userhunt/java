package net.w3e.wlib.dungeon.json;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.utils.json.JsonUtils;
import net.w3e.wlib.dungeon.DungeonLayer;

public class DungeonJsonAdapters {

	public static final DungeonJsonAdapters INSTANCE = new DungeonJsonAdapters();

	private final Map<String, Adapter<?>> ADAPTERS = new HashMap<>();

	protected DungeonJsonAdapters() {
		if (this.getClass() != DungeonJsonAdapters.class) {
			this.ADAPTERS.putAll(ADAPTERS);
		}
	}

	public final Adapter<?> getConfigType(String keyName) {
		return ADAPTERS.get(keyName);
	}

	public final void registerConfigType(Adapter<?> configType) {
		ADAPTERS.computeIfAbsent(configType.keyName(), e -> {
			configType.registerJson();
			return configType;
		});
	}

	public final void register() {
		JsonUtils.addTypedAdapter(DungeonLayer.class, this.ADAPTERS);
		for (Adapter<?> adapter : this.ADAPTERS.values()) {
			adapter.registerJson();
		}
	}

	@AllArgsConstructor
	public static class Adapter<CT> implements ConfigType<CT> {

		private final String keyName;
		@Getter(onMethod_ = {@Override})
		private final Class<CT> typeClass;

		@Override
		public final String keyName() {
			return this.keyName;
		}

		protected void registerJson() {}
	}
}
