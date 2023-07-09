package net.w3e.base.generator.property;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.IProperty;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.json.BJsonUtil;

public abstract class GenProperty implements IProperty {

	private final PropertyType type;
	private final GenRange levelRange;
	private final GenRange weight;
	private final GenRange valueRange;
	private final Boolean nerfLvl;
	private final Boolean clampRandom;

	protected GenProperty(PropertyType type, GenRange levelRange, GenRange weight, GenRange valueRange, Boolean nerfLvl, Boolean clampRandom) {
		this.type = type;
		this.levelRange = levelRange;
		this.weight = weight;
		this.valueRange = valueRange;
		this.nerfLvl = nerfLvl;
		this.clampRandom = clampRandom;
	}

	protected GenProperty(JsonObject json) {
		this.type = PropertyType.REGISTRY.get(json.get("type").getAsString());
		this.levelRange = GenRange.read(json.get("levelRange"), GenRange.LEVEL_RANGE);
		this.weight = GenRange.read(json.get("weight"), GenRange.EMPTY);
		this.valueRange = GenRange.read(json.get("valueRange"), GenRange.EMPTY);
		this.nerfLvl = BJsonUtil.readBoolean(BJsonUtil.LOGGER(), json, "nerfLvl", null, false, true);
		this.clampRandom = BJsonUtil.readBoolean(BJsonUtil.LOGGER(), json, "clampRandom", null, false, true);
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
	public final Boolean isNerfLvl() {
		return this.nerfLvl;
	}

	@Override
	public final Boolean isClampRandom() {
		return this.clampRandom;
	}

	@Override
	public String toString() {
		return type().key;
	}

	public abstract static class AbstractGenBuilder<T extends AbstractGenBuilder<T>> {

		protected PropertyType type;
		protected GenRange levelRange = GenRange.LEVEL_RANGE;
		protected GenRange weight = GenRange.EMPTY;
		protected GenRange valueRange = GenRange.EMPTY;
		protected Boolean nerfLvl = null;
		protected Boolean clampRandom = null;

		protected AbstractGenBuilder(@NotNull PropertyType type) {
			this.type = type;
		}

		@SuppressWarnings("unchecked")
		public T type(PropertyType type) {
			if (type != null) {
				this.type = type;
			}
			return (T)this;
		}

		@SuppressWarnings("unchecked")
		public T levelRange(GenRange levelRange) {
			if (levelRange != null) {
				this.levelRange = levelRange;
			}
			return (T)this;
		}

		@SuppressWarnings("unchecked")
		public T weight(GenRange weight) {
			if (weight != null) {
				this.weight = weight;
			}
			return (T)this;
		}

		@SuppressWarnings("unchecked")
		public T valueRange(GenRange valueRange) {
			if (valueRange != null) {
				this.valueRange = valueRange;
			}
			return (T)this;
		}

		@SuppressWarnings("unchecked")
		public T nerfLvl(Boolean nerfLvl) {
			this.nerfLvl = nerfLvl;
			return (T)this;
		}

		@SuppressWarnings("unchecked")
		public T clampRandom(Boolean clampRandom) {
			this.clampRandom = clampRandom;
			return (T)this;
		}

		public abstract GenProperty build();
	}
}
