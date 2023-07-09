package net.w3e.base.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.w3e.base.LogicUtil;
import net.w3e.base.generator.GenRange.ValueData;

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
	 * @param lvl
	 * @param nerfLvl
	 * @return
	 */
	default double weight(double lvl, boolean nerfLvl) {
		return this.weight().valueStep(lvl, this.nerflvl(nerfLvl));
	}
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
	default ValueData valueRange(double lvl, boolean nerfLvl, DoubleSupplier nextDouble) {
		return this.valueRange().valueStepData(lvl, this.nerflvl(nerfLvl), nextDouble);
	}
	/**
	 * @see net.w3e.base.generator.RandomGenerator#RandomGenerator
	 * @return null - apply from generator, true/false - mode
	 */
	Boolean isClampRandom();
	/**
	 * @see net.w3e.base.generator.RandomGenerator#RandomGenerator
	 * @return null - apply from generator, true/false - mode
	 */
	Boolean isNerfLvl();

	default double nerflvl(boolean nerfLvl) {
		return LogicUtil.valueOrDefault(isNerfLvl(), nerfLvl) ? this.levelRange().min() : 0;
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

	default JsonObject save() {
		JsonObject json = new JsonObject();
		json.addProperty("class", this.getClass().getSimpleName());
		json.addProperty("type", this.type().key);
		GenRange levelRange = this.levelRange();
		if (!levelRange.equals(GenRange.LEVEL_RANGE)) {
			json.add("levelRange", levelRange.save());
		}
		GenRange weight = this.weight();
		if (!weight.equals(GenRange.EMPTY)) {
			json.add("weight", weight.save());
		}
		if (this.required()) {
			json.addProperty("required", true);
		} else {
			GenRange chance = this.chance();
			if (!chance.equals(GenRange.EMPTY_CHANCE)) {
				json.add("chance", chance.save());
			}
		}
		GenRange valueRange = this.valueRange();
		if (!valueRange.equals(GenRange.EMPTY)) {
			json.add("valueRange", valueRange.save());
		}
		Boolean isClampRandom = this.isClampRandom();
		if (isClampRandom != null) {
			json.addProperty("isClampRandom", isClampRandom);
		}
		Boolean isNerfLvl = this.isClampRandom();
		if (isNerfLvl != null) {
			json.addProperty("isNerfLvl", isNerfLvl);
		}
		save(json);

		return json;
	}

	default void save(JsonObject json) {}

	static JsonArray save(List<IProperty> last) {
		JsonArray array = new JsonArray();

		for (IProperty property : last) {
			array.add(property.save());
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	static List<IProperty> read(JsonArray array, Map<String, Function<JsonObject, IProperty>> map) {
		List<IProperty> list = new ArrayList<>();

		Iterator<JsonObject> iterator = (Iterator<JsonObject>)(Iterator<?>)array.iterator();

		while(iterator.hasNext()) {
			JsonObject json = iterator.next();
			Function<JsonObject, IProperty> function = map.get(json.get("class").getAsString());
			if (function != null) {
				iterator.remove();
				list.add(function.apply(json));
			}
		}
		list.removeIf(Objects::isNull);

		return list;
	}
}
