package net.w3e.base.generator.property;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.PropertyType;

public class GenNormalProperty extends GenProperty {

	private final GenRange chance;

	public GenNormalProperty(PropertyType type, GenRange levelRange, GenRange weight, GenRange valueRange, GenRange chance, Boolean nerfLvl, Boolean clampRandom) {
		super(type, levelRange, weight, valueRange, nerfLvl, clampRandom);
		this.chance = chance;
	}

	public GenNormalProperty(JsonObject json) {
		super(json);
		if (json.has("chance")) {
			this.chance = GenRange.read(json.get("chance"), GenRange.EMPTY_CHANCE);
		} else {
			this.chance = GenRange.EMPTY_CHANCE;
		}
	}

	@Override
	public final boolean required() {
		return false;
	}

	@Override
	public GenRange chance() {
		return this.chance;
	}

	public static class GenNormalBuilder extends AbstractGenBuilder<GenNormalBuilder> {

		protected GenRange chance = GenRange.EMPTY_CHANCE;

		public GenNormalBuilder(@NotNull PropertyType type) {
			super(type);
		}

		public final GenNormalBuilder chance(GenRange chance) {
			if (chance != null) {
				this.chance = chance;
			}
			return this;
		}

		@Override
		public GenNormalProperty build() {
			return new GenNormalProperty(this.type, this.levelRange, this.weight, this.valueRange, this.chance, this.nerfLvl, this.clampRandom);
		}
	}
}
