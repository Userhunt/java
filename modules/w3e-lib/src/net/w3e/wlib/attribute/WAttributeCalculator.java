package net.w3e.wlib.attribute;

public class WAttributeCalculator extends WAttributeInstance {

	private int opName;

	public WAttributeCalculator(WAttribute attribute) {
		super(attribute);
	}

	public WAttributeCalculator(float baseValue) {
		this(WAttribute.createSimple(baseValue));
	}

	public final WAttributeModifier addOperation(float value, WAttributeModifier.Operation operation) {
		return this.addOperation(String.valueOf(this.opName++), value, operation);
	}

	public final WAttributeModifier addOperation(String key, float value, WAttributeModifier.Operation operation) {
		return this.addOperation(new WAttributeModifier(key, value, operation));
	}

	public final WAttributeModifier addOperation(WAttributeModifier modifier) {
		return this.addTransientModifier(modifier);
	}
}
