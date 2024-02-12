package net.w3e.base.attribute;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

public class WAttributeMap<T extends WAttribute, V extends WAttributeInstance<T>, R extends WAttributeSupplier<T, V>> {

	protected final Map<T, V> attributes = Maps.newHashMap();
	protected final R supplier;

	public WAttributeMap(R attributeSupplier) {
		this.supplier = attributeSupplier;
	}

	@Nullable
	public final V getInstance(T location) {
		return this.attributes.computeIfAbsent(location, attribute -> this.supplier.createInstance(attribute));
	}

	public final V get(T location) {
		return this.attributes.get(location);
	}

	public final V remove(T location) {
		return this.attributes.remove(location);
	}

	public final boolean hasAttribute(T attribute) {
		return this.attributes.get(attribute) != null || this.supplier.hasAttribute(attribute);
	}

	public final boolean hasModifier(T attribute, UUID uUID) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getModifier(uUID) != null : this.supplier.hasModifier(attribute, uUID);
	}

	public final double getValue(T attribute) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getValue() : this.supplier.getValue(attribute);
	}

	public final double getBaseValue(T attribute) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getBaseValue() : this.supplier.getBaseValue(attribute);
	}

	public final double getModifierValue(T attribute, UUID uUID) {
		V attributeInstance = this.attributes.get(attribute);
		return attributeInstance != null ? attributeInstance.getModifier(uUID).getAmount() : this.supplier.getModifierValue(attribute, uUID);
	}

	public final void removeAttributeModifiers(Multimap<T, WAttributeModifier> multimap) {
		multimap.asMap().forEach((attribute, collection) -> {
			V attributeInstance = this.attributes.get(attribute);
			if (attributeInstance != null) {
				collection.forEach(attributeInstance::removeModifier);
			}
		});
	}

	public final void addTransientAttributeModifiers(Multimap<T, WAttributeModifier> multimap) {
		multimap.forEach((attribute, attributeModifier) -> {
			V attributeInstance = this.getInstance(attribute);
			if (attributeInstance != null) {
				attributeInstance.removeModifier(attributeModifier);
				attributeInstance.addTransientModifier(attributeModifier);
			}
		});
	}

	public final void assignValues(WAttributeMap<T, V, R> attributeMap) {
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

	public final boolean tryClear(T location) {
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

