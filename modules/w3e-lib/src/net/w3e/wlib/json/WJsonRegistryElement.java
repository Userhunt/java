package net.w3e.wlib.json;

import lombok.Getter;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;

public class WJsonRegistryElement implements TypedConfig {

	@Getter(onMethod_ = @Override)
	protected final transient ConfigType<?> configType;
	
	protected WJsonRegistryElement(String keyName, WJsonTypedTypeAdapter.WJsonAdaptersMap<? extends WJsonRegistryElement> map) {
		this.configType = map.getConfigType(keyName);
	}

	protected WJsonRegistryElement(WJsonTypedTypeAdapter<?> configType) {
		this.configType = configType;
	}

	public final String keyName() {
		return this.configType.keyName();
	}
}
