package net.w3e.wlib.json;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.io.codec.typed.TypedConfig;
import net.w3e.wlib.json.adapters.WJsonAdaptersMap;

@RequiredArgsConstructor
public class WJsonRegistryElement implements TypedConfig {

	@Getter(onMethod_ = @Override)
	protected final transient ConfigType<?> configType;

	protected WJsonRegistryElement(String keyName, WJsonAdaptersMap<? extends WJsonRegistryElement> map) {
		this.configType = map.getConfigType(keyName);
	}

	public final String keyName() {
		return this.configType.keyName();
	}
}
