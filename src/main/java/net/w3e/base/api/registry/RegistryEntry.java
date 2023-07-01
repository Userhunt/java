package net.w3e.base.api.registry;

import javax.annotation.Nullable;

public class RegistryEntry<V extends RegistryEntry<V>> {

	private String registryName = null;

	@SuppressWarnings("unchecked")
	public final V setRegistryName(String name) {
		if (getRegistryName() != null)
			throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
		this.registryName = name;
		return (V)this;
	}

	@Nullable
	public final String getRegistryName() {
		return this.registryName;
	}
}
