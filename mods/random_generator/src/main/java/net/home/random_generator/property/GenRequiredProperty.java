package net.home.random_generator.property;

import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.generator.IProperty.IRequiredProperty;

public class GenRequiredProperty extends GenProperty implements IRequiredProperty {

	public GenRequiredProperty(PropertyType type, GenRange levelRange, GenRange weight, GenRange valueRange) {
		super(type, levelRange, weight, valueRange);
	}

	@Override
	public final boolean required() {
		return IRequiredProperty.super.required();
	}

	@Override
	public final GenRange chance() {
		return IRequiredProperty.super.chance();
	}
}
