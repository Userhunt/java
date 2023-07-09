package net.w3e.base.registry;

import javax.annotation.Nullable;

public class RegistryEntryIns<T> implements RegistryEntry<T> {

	private String registryName = null;

	public final RegistryEntry<T> setRegistryName(String name) {
		if (getRegistryName() != null)
			throw Registry.redifine(name, this.getRegistryName());
		this.registryName = name;
		return this;
	}

	@Nullable
	public final String getRegistryName() {
		return this.registryName;
	}

	public static class RegistryValue<T> extends RegistryEntryIns<T> {

		private final T value;

		public RegistryValue(T value) {
			this.value = value;
		}

		public final T get() {
			return this.value;
		}
	}
}
