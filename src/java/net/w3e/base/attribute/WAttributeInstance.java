package net.w3e.base.attribute;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.w3e.base.attribute.WAttributeModifier.Operation;
import net.w3e.base.math.BMatUtil;

public class WAttributeInstance<T extends WAttribute> {

	protected final T attribute;
	private final Map<Operation, Set<WAttributeModifier>> modifiersByOperation = Maps.newEnumMap(Operation.class);
	private final Map<String, WAttributeModifier> modifierById = new Object2ObjectArrayMap<>();
	private final Set<WAttributeModifier> permanentModifiers = new ObjectArraySet<>();
	private final double defaultValue;
	private double baseValue;
	private boolean dirty = true;
	private double cachedValue;

	public WAttributeInstance(T attribute) {
		this.attribute = attribute;
		this.defaultValue = this.attribute.getDefValue();
		this.baseValue = this.defaultValue;
	}

	public final T getAttribute() {
		return this.attribute;
	}

	public final boolean isEmptyBase() {
		return (this.defaultValue == baseValue || BMatUtil.round(this.defaultValue, 5) == BMatUtil.round(this.baseValue, 5));
	}

	public final boolean isEmptyModifier() {
		return this.modifierById.isEmpty();
	}

	public final boolean isEmpty() {
		return this.isEmptyBase() && this.isEmptyModifier();
	}

	public final void clear() {
		this.clearBase();
		this.clearModifier();
	}

	public final void clearBase() {
		this.setBaseValue(this.defaultValue);
	}

	public final void clearModifier() {
		if (!this.modifierById.isEmpty()) {
			this.modifiersByOperation.clear();
			this.modifierById.clear();
			this.permanentModifiers.clear();
			this.setDirty();
		}
	}

	public final double getBaseValue() {
		return this.baseValue;
	}

	public final void setBaseValue(double d) {
		if (d == this.baseValue) {
			return;
		}
		this.baseValue = d;
		this.setDirty();
	}

	public final Set<WAttributeModifier> getModifiers(Operation operation) {
		return this.modifiersByOperation.computeIfAbsent(operation, operation2 -> Sets.newHashSet());
	}

	public final Set<WAttributeModifier> getModifiers() {
		return ImmutableSet.copyOf(this.modifierById.values());
	}

	@Nullable
	public final WAttributeModifier getModifier(String name) {
		return this.modifierById.get(name);
	}

	public final boolean hasModifier(WAttributeModifier attributeModifier) {
		return hasModifier(attributeModifier.getName());
	}

	public final boolean hasModifier(String name) {
		return this.getModifier(name) != null;
	}

	private final WAttributeModifier addModifier(WAttributeModifier attributeModifier) {
		attributeModifier = new WAttributeModifier(attributeModifier.getName(), attributeModifier.getAmount(), attributeModifier.getOperation());
		WAttributeModifier attributeModifier2 = this.modifierById.putIfAbsent(attributeModifier.getName(), attributeModifier);
		if (attributeModifier2 != null) {
			throw new IllegalArgumentException("Modifier is already applied on this attribute!");
		}
		this.getModifiers(attributeModifier.getOperation()).add(attributeModifier);
		this.setDirty();
		return attributeModifier;
	}

	public final void addTransientModifier(WAttributeModifier attributeModifier) {
		this.addModifier(attributeModifier);
	}

	public final void addPermanentModifier(WAttributeModifier attributeModifier) {
		attributeModifier = this.addModifier(attributeModifier);
		this.permanentModifiers.add(attributeModifier);
	}

	public final void removeModifier(WAttributeModifier attributeModifier) {
		boolean remove = this.modifierById.remove(attributeModifier.getName()) != null;
		if (remove) {
			this.getModifiers(attributeModifier.getOperation()).remove(attributeModifier);
			this.permanentModifiers.remove(attributeModifier);
			this.setDirty();
		}
	}

	public final void setModifierValue(String name, double amount) {
		this.setModifierValue(this.getModifier(name), amount);
	}

	@SuppressWarnings("deprecation")
	public final void setModifierValue(WAttributeModifier modifier, double amount) {
		if (modifier.getAmount() != amount) {
			modifier.setAmount(amount);
			this.setDirty();
		}
	}

	public final void removeModifier(String name) {
		WAttributeModifier attributeModifier = this.getModifier(name);
		if (attributeModifier != null) {
			this.removeModifier(attributeModifier);
		}
	}

	public final boolean removePermanentModifier(String name) {
		WAttributeModifier attributeModifier = this.getModifier(name);
		if (attributeModifier != null && this.permanentModifiers.contains(attributeModifier)) {
			this.removeModifier(attributeModifier);
			return true;
		}
		return false;
	}

	public final void removeModifiers() {
		for (WAttributeModifier attributeModifier : this.getModifiers()) {
			this.removeModifier(attributeModifier);
		}
	}

	public final double getValue() {
		if (this.dirty) {
			this.cachedValue = this.calculateValue();
			this.dirty = false;
		}
		return this.cachedValue;
	}

	private final double calculateValue() {
		double d = this.getBaseValue();
		for (WAttributeModifier attributeModifier : this.getModifiersOrEmpty(Operation.ADD_VALUE)) {
			d += attributeModifier.getAmount();
		}
		double e = d;
		for (WAttributeModifier attributeModifier2 : this.getModifiersOrEmpty(Operation.ADD_MULTIPLIED_BASE)) {
			e += d * attributeModifier2.getAmount();
		}
		for (WAttributeModifier attributeModifier2 : this.getModifiersOrEmpty(Operation.ADD_MULTIPLIED_TOTAL)) {
			e *= 1.0 + attributeModifier2.getAmount();
		}
		return this.attribute.calculateValue(e);
	}

	private final Collection<WAttributeModifier> getModifiersOrEmpty(Operation operation) {
		return this.modifiersByOperation.getOrDefault(operation, Collections.emptySet());
	}

	public final void replaceFrom(WAttributeInstance<T> attributeInstance) {
		this.baseValue = attributeInstance.baseValue;
		this.modifierById.clear();
		this.modifierById.putAll(attributeInstance.modifierById);
		this.permanentModifiers.clear();
		this.permanentModifiers.addAll(attributeInstance.permanentModifiers);
		this.modifiersByOperation.clear();
		attributeInstance.modifiersByOperation.forEach((operation, set) -> this.getModifiers(operation).addAll(set));
		this.setDirty();
	}

	protected final void setDirty() {
		this.dirty = true;
	}

	protected final Set<WAttributeModifier> getPermanentModifiers() {
		return this.permanentModifiers;
	}
}
