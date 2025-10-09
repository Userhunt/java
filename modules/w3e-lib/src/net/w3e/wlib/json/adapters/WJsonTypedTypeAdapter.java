package net.w3e.wlib.json.adapters;

import java.util.Objects;

import net.skds.lib2.io.codec.typed.ConfigType;

public record WJsonTypedTypeAdapter<CT>(String keyName, Class<CT> getTypeClass) implements ConfigType<CT> {

	public WJsonTypedTypeAdapter {
		Objects.requireNonNull(keyName, "keyName");
		Objects.requireNonNull(getTypeClass, "typeClass");
	}

	public boolean isInstance(Object object) {
		return this.getTypeClass().isInstance(object);
	}

	public boolean isAssignableFrom(Class<?> cls) {
		return this.getTypeClass().isAssignableFrom(cls);
	}
}
