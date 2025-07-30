package net.w3e.wlib.attribute;

import java.util.Objects;

public class WAttributeModifier {
	private double amount;
	private final Operation operation;
	private final String name;

	public WAttributeModifier(String name, double amount, Operation operation) {
		this.name = name;
		this.amount = amount;
		this.operation = operation;
	}

	public final String getName() {
		return this.name;
	}

	public final double getAmount() {
		return this.amount;
	}

	protected final void setAmount(double amount) {
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
		return Objects.equals(this.name, attributeModifier.name);
	}

	public final int hashCode() {
		return this.name.hashCode();
	}

	public final String toString() {
		String name = this.name != null ? ", name='" + this.name + "'" : null;
		return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + name + ", id=" + this.name + "}";
	}

	public static enum Operation {
		ADD_VALUE(0),
		ADD_MULTIPLIED_BASE(1),
		ADD_MULTIPLIED_TOTAL(2);

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