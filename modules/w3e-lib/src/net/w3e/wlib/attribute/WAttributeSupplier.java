package net.w3e.wlib.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class WAttributeSupplier<V extends WAttributeInstance> {
	protected final Map<WAttribute, V> instances = new LinkedHashMap<>();

	public WAttributeSupplier(Map<WAttribute, V> map) {
		this.instances.putAll(map);
	}

	public WAttributeSupplier() {}

	public final WAttributeSupplier<V> registerAttribute(V instance) {
		if (instance != null && this.instances.get(instance.getAttribute()) == null) {
			this.instances.put(instance.getAttribute(), instance);
		}
		return this;
	}

	public final WAttributeSupplier<V> registerAttribute(WAttribute attribute) {
		if (attribute != null && this.instances.get(attribute) == null) {
			this.instances.put(attribute, createAttributeInstance(attribute));
		}
		return this;
	}

	private final V getAttributeInstance(WAttribute attribute) {
		V attributeInstance = this.instances.get(attribute);
		if (attributeInstance == null) {
			throw new IllegalArgumentException("Can't find attribute " + attribute);
		}
		return attributeInstance;
	}

	public final double getValue(WAttribute attribute) {
		return this.getAttributeInstance(attribute).getValue();
	}

	public final double getBaseValue(WAttribute attribute) {
		return this.getAttributeInstance(attribute).getBaseValue();
	}

	public final double getModifierValue(WAttribute attribute, String name) {
		WAttributeModifier attributeModifier = this.getAttributeInstance(attribute).getModifier(name);
		if (attributeModifier == null) {
			throw new IllegalArgumentException("Can't find modifier \"" + name + "\" on attribute " + attribute);
		}
		return attributeModifier.getAmount();
	}

	public final V createInstance(WAttribute attribute) {
		V attributeInstance = this.instances.get(attribute);
		if (attributeInstance == null) {
			return null;
		}
		V attributeInstance2 = createAttributeInstance(attribute);
		attributeInstance2.replaceFrom(attributeInstance);
		return attributeInstance2;
	}

	protected abstract V createAttributeInstance(WAttribute attribute);

	public boolean hasAttribute(WAttribute attribute) {
		return this.instances.containsKey(attribute);
	}

	public boolean hasModifier(WAttribute attribute, String name) {
		V attributeInstance = this.instances.get(attribute);
		return attributeInstance != null && attributeInstance.getModifier(name) != null;
	}
}
