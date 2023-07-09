package net.w3e.base.generator.property;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.generator.IProperty.IRequiredProperty;

public class GenRequiredProperty extends GenProperty implements IRequiredProperty {

	public GenRequiredProperty(PropertyType type, GenRange levelRange, GenRange weight, GenRange valueRange, Boolean nerfLvl, Boolean clampRandom) {
		super(type, levelRange, weight, valueRange, nerfLvl, clampRandom);
	}

	public GenRequiredProperty(JsonObject json) {
		super(json);
	}

	@Override
	public final boolean required() {
		return IRequiredProperty.super.required();
	}

	@Override
	public final GenRange chance() {
		return IRequiredProperty.super.chance();
	}

	public static class GenRequiredBuilder extends AbstractGenBuilder<GenRequiredBuilder> {

		public GenRequiredBuilder(@NotNull PropertyType type) {
			super(type);
		}

		@Override
		public final GenRequiredProperty build() {
			return new GenRequiredProperty(this.type, this.levelRange, this.weight, this.valueRange, this.nerfLvl, this.clampRandom);
		}
	}
}
