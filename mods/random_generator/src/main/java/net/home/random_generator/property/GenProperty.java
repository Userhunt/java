package net.home.random_generator.property;

import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.IProperty;
import net.w3e.base.generator.PropertyType;

public abstract class GenProperty implements IProperty {

	private final PropertyType type;
	private final GenRange levelRange;
	private final GenRange weight;
	private final GenRange valueRange;

	protected GenProperty(PropertyType type, GenRange levelRange, GenRange weight, GenRange valueRange) {
		this.type = type;
		this.levelRange = levelRange;
		this.weight = weight;
		this.valueRange = valueRange;
	}
	
	@Override
	public final PropertyType type() {
		return this.type;
	}

	@Override
	public final GenRange levelRange() {
		return this.levelRange;
	}

	@Override
	public final GenRange weight() {
		return this.weight;
	}

	@Override
	public final GenRange valueRange() {
		return this.valueRange;
	}

	@Override
	public String toString() {
		return type().getRegistryName();
	}
}
