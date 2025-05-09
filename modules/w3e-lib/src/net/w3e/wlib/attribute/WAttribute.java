package net.w3e.wlib.attribute;

public interface WAttribute {
	double getDefValue();
	double calculateValue(double value);

	public interface WAttributeSimple extends WAttribute {
		@Override
		default double calculateValue(double value) {
			return value;
		}
	}

	public static WAttributeSimple createSimple(double baseValue) {
		return new WAttributeSimple() {
			@Override
			public double getDefValue() {
				return baseValue;
			}
		};
	}
}
