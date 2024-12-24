package net.w3e.wlib.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public abstract class WAttributeSupplier<T extends WAttribute, V extends WAttributeInstance<T>> {
	protected final Map<T, V> instances = new LinkedHashMap<>();

	public WAttributeSupplier(Map<T, V> map) {
		this.instances.putAll(map);
	}

	public WAttributeSupplier() {}

	public final WAttributeSupplier<T, V> registerAttribute(V instance) {
		if (instance != null && this.instances.get(instance.getAttribute()) == null) {
			this.instances.put(instance.getAttribute(), instance);
		}
		return this;
	}

	public final WAttributeSupplier<T, V> registerAttribute(T attribute) {
		if (attribute != null && this.instances.get(attribute) == null) {
			this.instances.put(attribute, createAttributeInstance(attribute));
		}
		return this;
	}

	private final V getAttributeInstance(T attribute) {
		V attributeInstance = this.instances.get(attribute);
		if (attributeInstance == null) {
			throw new IllegalArgumentException("Can't find attribute " + attribute);
		}
		return attributeInstance;
	}

	public final double getValue(T attribute) {
		return this.getAttributeInstance(attribute).getValue();
	}

	public final double getBaseValue(T attribute) {
		return this.getAttributeInstance(attribute).getBaseValue();
	}

	public final double getModifierValue(T attribute, String name) {
		WAttributeModifier attributeModifier = this.getAttributeInstance(attribute).getModifier(name);
		if (attributeModifier == null) {
			throw new IllegalArgumentException("Can't find modifier \"" + name + "\" on attribute " + attribute);
		}
		return attributeModifier.getAmount();
	}

	@Nullable
	public final V createInstance(T attribute) {
		V attributeInstance = this.instances.get(attribute);
		if (attributeInstance == null) {
			return null;
		}
		V attributeInstance2 = createAttributeInstance(attribute);
		attributeInstance2.replaceFrom(attributeInstance);
		return attributeInstance2;
	}

	protected abstract V createAttributeInstance(T attribute);

	public boolean hasAttribute(T attribute) {
		return this.instances.containsKey(attribute);
	}

	public boolean hasModifier(T attribute, String name) {
		V attributeInstance = this.instances.get(attribute);
		return attributeInstance != null && attributeInstance.getModifier(name) != null;
	}
}
