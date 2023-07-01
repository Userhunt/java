package net.home.random_generator.property;

import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.PropertyType;

public class GenNormalProperty extends GenProperty {

	private final GenRange chance;

	public GenNormalProperty(PropertyType type, GenRange levelRange, GenRange weight, GenRange valueRange, GenRange chance) {
		super(type, levelRange, weight, valueRange);
		this.chance = chance;
	}

	@Override
	public final boolean required() {
		return false;
	}

	@Override
	public GenRange chance() {
		return this.chance;
	}
}
