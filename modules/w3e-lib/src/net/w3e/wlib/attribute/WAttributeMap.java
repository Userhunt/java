package net.w3e.wlib.attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class WAttributeMap<V extends WAttributeInstance, R extends WAttributeSupplier<V>> {

	protected final Map<WAttribute, V> attributes = new HashMap<>();
	protected final R supplier;

	public WAttributeMap(R attributeSupplier) {
		this.supplier = attributeSupplier;
	}

	public final V getInstance(WAttribute location) {
		return this.attributes.computeIfAbsent(location, this.supplier::createInstance);
	}

	public final V get(WAttribute location) {
		return this.attributes.get(location);
	}

	public final V remove(WAttribute location) {
		return this.attributes.remove(location);
	}

	public final boolean hasAttribute(WAttribute attribute) {
		return this.attributes.get(attribute) != null || this.supplier.hasAttribute(attribute);
	}

	public final boolean hasModifier(WAttribute attribute, String name) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getModifier(name) != null : this.supplier.hasModifier(attribute, name);
	}

	public final double getValue(WAttribute attribute) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getValue() : this.supplier.getValue(attribute);
	}

	public final double getBaseValue(WAttribute attribute) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getBaseValue() : this.supplier.getBaseValue(attribute);
	}

	public final double getModifierValue(WAttribute attribute, String name) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getModifier(name).getAmount() : this.supplier.getModifierValue(attribute, name);
	}

	/*public final void removeAttributeModifiers(Multimap<WAttribute, WAttributeModifier> multimap) {
		multimap.asMap().forEach((attribute, collection) -> {
			V attributeInstance = this.attributes.get(attribute);
			if (attributeInstance != null) {
				collection.forEach(attributeInstance::removeModifier);
			}
		});
	}*/

	/*public final void addTransientAttributeModifiers(Multimap<WAttribute, WAttributeModifier> multimap) {
		multimap.forEach((attribute, attributeModifier) -> {
			V attributeInstance = this.getInstance(attribute);
			if (attributeInstance != null) {
				attributeInstance.removeModifier(attributeModifier);
				attributeInstance.addTransientModifier(attributeModifier);
			}
		});
	}*/

	public final void assignValues(WAttributeMap<V, R> attributeMap) {
		attributeMap.attributes.values().forEach(attributeInstance -> {
			V attributeInstance2 = this.getInstance(attributeInstance.getAttribute());
			if (attributeInstance2 != null) {
				attributeInstance2.replaceFrom(attributeInstance);
			}
		});
	}

	protected final Collection<V> values() {
		return this.attributes.values();
	}

	public final boolean tryClear(V instance) {
		if (instance == null) {
			return false;
		} else {
			return this.tryClear(instance.attribute);
		}
	}

	public final boolean tryClear(WAttribute location) {
		if (!this.hasAttribute(location)) {
			return false;
		} else {
			V instance = this.getInstance(location);
			if (instance.isEmpty()) {
				this.attributes.remove(location);
				return true;
			}
		}
		return false;
	}
}

