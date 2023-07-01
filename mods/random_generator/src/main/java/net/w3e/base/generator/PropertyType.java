package net.w3e.base.generator;

import net.home.random_generator.GenRegistry;
import net.w3e.base.api.registry.RegistryEntry;

public class PropertyType extends RegistryEntry<PropertyType> {
	
	public PropertyType(String key) {
		GenRegistry.REGISTRY.register(key, this);
	}
}
