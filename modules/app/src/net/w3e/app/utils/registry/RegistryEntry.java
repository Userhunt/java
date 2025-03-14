package net.w3e.app.utils.registry;

public interface RegistryEntry<T> {
	RegistryEntry<T> setRegistryName(String name);
	String getRegistryName();
	@SuppressWarnings("unchecked")
	default T get() {
		return (T)this;
	}
}
