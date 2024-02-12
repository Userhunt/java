package net.w3e.base.attribute;

import java.util.Objects;
import java.util.UUID;

import net.w3e.base.math.BMatUtil;

public class WAttributeModifier {
	private double amount;
	private final Operation operation;
	private final String name;
	private final UUID id;

	public WAttributeModifier(String string, double amount, Operation operation) {
		this(BMatUtil.createInsecureUUID(), string, amount, operation);
	}

	public WAttributeModifier(UUID uUID, String name, double amount, Operation operation) {
		this.id = uUID;
		this.name = name;
		this.amount = amount;
		this.operation = operation;
	}

	public final UUID getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public final double getAmount() {
		return this.amount;
	}

	/**
	 * @deprecated system
	 */
	public final void setAmount(double amount) {
		this.amount = amount;
	}

	public final Operation getOperation() {
		return this.operation;
	}

	public final boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || this.getClass() != object.getClass()) {
			return false;
		}
		WAttributeModifier attributeModifier = (WAttributeModifier)object;
		return Objects.equals(this.id, attributeModifier.id);
	}

	public final int hashCode() {
		return this.id.hashCode();
	}

	public final String toString() {
		String name = this.name != null ? ", name='" + this.name + "'" : null;
		return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + name + ", id=" + this.id + "}";
	}

	public static enum Operation {
		ADDITION(0),
		MULTIPLY_BASE(1),
		MULTIPLY_TOTAL(2);

		private final int value;

		private Operation(int j) {
			this.value = j;
		}

		public int toValue() {
			return this.value;
		}

		public static Operation fromValue(int i) {
			for (Operation operation : values()) {
				if (operation.value == i) {
					return operation;
				}
			}
			throw new IllegalArgumentException("No operation with value " + i);
		}
	}
}