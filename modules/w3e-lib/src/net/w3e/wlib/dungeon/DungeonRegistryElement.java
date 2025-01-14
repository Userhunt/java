package net.w3e.wlib.dungeon;

import lombok.Getter;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapter.DungeonJsonAdaptersMap;

public class DungeonRegistryElement implements TypedConfig {

	@Getter(onMethod_ = @Override)
	protected final transient ConfigType<?> configType;
	
	protected DungeonRegistryElement(String keyName, DungeonJsonAdaptersMap map) {
		this.configType = map.getConfigType(keyName);
	}
}
