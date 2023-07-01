package net.w3e.base.generator;

import java.util.function.DoubleSupplier;

public interface IProperty {
	/**
	 * type of property
	 */
	PropertyType type();
	/**
	 * what level for, when spawned
	 */
	GenRange levelRange();
	/**
	 * how many points spent
	 */
	GenRange weight();
	/**
	 * is it 100% applied
	 */
	boolean required();
	/**
	 * chance for spawn
	 */
	GenRange chance();
	/**
	 * @see net.w3e.base.generator.RandomGenerator#RandomGenerator
	 * @param lvl
	 * @param nerfLvl
	 * @return
	 */
	default double chance(double lvl, boolean nerfLvl) {
		return this.chance().valueStep(lvl, this.nerflvl(nerfLvl));
	}
	/**
	 * values range
	 */
	GenRange valueRange();
	/**
	 * @param lvl
	 * @param nerfLvl
	 * @return
	 */
	default double[] valueRange(double lvl, boolean nerfLvl, DoubleSupplier nextDouble) {
		return this.valueRange().valueStepData(lvl, this.nerflvl(nerfLvl), nextDouble);
	}

	default double nerflvl(boolean nerfLvl) {
		return nerfLvl ? this.levelRange().min() : 0;
	}

	public static interface IRequiredProperty extends IProperty {
		@Override
		default boolean required() {
			return true;
		}
		@Override
		default GenRange chance() {
			return GenRange.EMPTY;
		}
	}
}
