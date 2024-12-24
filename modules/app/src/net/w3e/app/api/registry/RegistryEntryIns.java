package net.w3e.app.api.registry;

import org.jetbrains.annotations.Nullable;

public class RegistryEntryIns<T> implements RegistryEntry<T> {

	private String registryName = null;

	public final RegistryEntry<T> setRegistryName(String name) {
		String old = getRegistryName();
		if (old != null && old != name)
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

		@Override
		public String toString() {
			return String.format("%s=%s", getRegistryName(), this.value);
		}
	}
}
