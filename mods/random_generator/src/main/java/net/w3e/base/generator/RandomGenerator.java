package net.w3e.base.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import it.unimi.dsi.fastutil.doubles.Double2ObjectArrayMap;
import net.home.main.MainFrame;
import net.w3e.base.BStringUtil;
import net.w3e.base.LogicUtil;
import net.w3e.base.collection.IdentityLinkedHashMap;
import net.w3e.base.collection.ArraySet.ArraySetStrict;
import net.w3e.base.generator.GenRange.ValueData;
import net.w3e.base.generator.RandomGenerator.RandomGeneratorFunctionsBuilder.*;
import net.w3e.base.generator.collection.RandomCollection;
import net.w3e.base.math.BMatUtil;

public class RandomGenerator {

	public static final RandomGeneratorFunctions FUNCTIONS = new RandomGeneratorFunctionsBuilder().build();

	private final RandomGeneratorPrint print;
	private final DoubleSupplier nextDouble;
	private final boolean nerfLvl;
	private final boolean clampRandom;
	private final RandomGeneratorFunctions functions;

	/**
	 * 
	 * @param print - debug
	 * @param nextDouble - random
	 * @param clampRandom - valueRange(min,max), base, random, value = clamp ? (min < (base + random) < 10) : (min < (base) < 10) + random
	 * @param nerfLvl - lvlRange(minLvl,?), valueRange(min,max,step), value = (min < (min + step * (lvl - (nerfLvl ? minLvl : 0))) < max)
	 */
	public RandomGenerator(RandomGeneratorPrint print, DoubleSupplier nextDouble, boolean nerfLvl, Boolean clampRandom, RandomGeneratorFunctions functions) {
		this.print = print;
		this.nextDouble = nextDouble;
		this.nerfLvl = nerfLvl;
		this.clampRandom = clampRandom;
		this.functions = functions;
	}

	public static record RandomGeneratorPrint(boolean run, boolean lvl, boolean required, boolean chance, boolean count, boolean value, boolean weight, boolean result) {
		public static final RandomGeneratorPrint NONE = new RandomGeneratorPrint(false, false, false, false, false, false, false, false);
		public static final RandomGeneratorPrint ALL = new RandomGeneratorPrint(true, true, true, true, true, true, true, true);

		private final void run(RandomGeneratorExt ext) {
			title("run");
			info("lvl: " + ext.lvl);
			info(String.format("count: [%s,%s]", ext.minCount, ext.maxCount));
			info(String.format("weight: [%s,%s]", ext.minWeight, ext.maxWeight));
			info("list: " + ext.list);
		}

		private final void lvl(RandomGeneratorExt ext) {
			title("lvl");
			info("list: " + ext.list);
		}

		private final void required(RandomGeneratorExt ext) {
			title("required");
			info("list: " + ext.list);
			info("pool: " + ext.pool.keySet());
		}

		private final void chance(RandomGeneratorExt ext) {
			title("chance");
			info("list: " + ext.list);
			info("pool: " + ext.pool.keySet());
			info("chances: " + this.chanceList(ext));
		}

		private final void count(RandomGeneratorExt ext) {
			title("count");
			info("list: " + ext.list);
			info("pool: " + ext.pool.keySet());
			info("chances: " + this.chanceList(ext));
		}

		private final void value(RandomGeneratorExt ext) {
			title("value");
			info(valueList(ext));
		}

		private final void weight(RandomGeneratorExt ext) {
			title("weight");
			info("values: " + valueList(ext));
			info("weight: " + ext.calculateWight());
			info("weights: " + weightList(ext));
		}

		private final void result(RandomGeneratorExt ext) {
			title("result");
			info("\n" + RandGenRes.create(ext.generator(), ext.lvl, ext.pool).toString(false, null));
		}

		private final String chanceList(RandomGeneratorExt ext) {
			return ext.chanceList().stream().map(data -> data.property + "=" + data.chance).toList().toString();
		}

		private final String valueList(RandomGeneratorExt ext) {
			return ext.valueList().stream().map(data -> data.property + "=" + data.value).toList().toString();
		}

		private final String weightList(RandomGeneratorExt ext) {
			return ext.weightList().stream().map(data -> data.property + "=" + data.weight + "/" + data.getWeight()).toList().toString();
		}

		private final void title(Object object) {
			MainFrame.LOGGER.debug(object);
		}

		private final void info(Object object) {
			MainFrame.LOGGER.info(" " + object);
		}
	}

	public final RandGenRes generate(double lvl, int minCount, int maxCount, double minWeight, double maxWeight, Collection<IProperty> list) {
		return new RandomGeneratorExt(lvl, minCount, maxCount, minWeight, maxWeight, list).run();
	}

	public static record RandomGeneratorFunctions(MinMaxWeight minWeight, MinMaxWeight maxWeight) {}

	public static class RandomGeneratorFunctionsBuilder {

		public static interface MinMaxWeight {
			void apply(boolean min, double weight, double limit, double scale, Map<IProperty, RandomGeneratorData> pool, Consumer<RandomGeneratorData> remove);
			static List<RandomGeneratorData> list(Map<IProperty, RandomGeneratorData> pool, boolean filter) {
				Stream<RandomGeneratorData> steram = pool.values().stream();
				if (filter) {
					steram = steram.filter(RandomGeneratorData::hasWeight);
				}
				return steram.toList();
			}
			static void print(List<RandomGeneratorData> list) {
				MainFrame.LOGGER.info(" " + list.stream().map(data -> String.format("{name:\"%s\",value:%s,weight:%s/%s}", data.property, data.value, data.weight, data.getWeight())).toList());
			}

			public static final MinMaxWeight NONE = (min, weight, limit, scale, pool, remove) -> {};
			public static final MinMaxWeight ALL = (min, weight, limit, scale, pool, remove) -> {
				List<RandomGeneratorData> list = list(pool, false);
				for (RandomGeneratorData data : list) {
					data.applyScale(scale);
				}
			};
			@Deprecated
			public static final MinMaxWeight WEIGHT_RANDOM = (min, weight, limit, scale, pool, remove) -> {};
		}

		private MinMaxWeight minWeight = MinMaxWeight.ALL;
		private MinMaxWeight maxWeight = MinMaxWeight.ALL;

		public final RandomGeneratorFunctionsBuilder minWeight(MinMaxWeight minWeight) {
			if (minWeight != null) {
				this.minWeight = minWeight;
			}
			return this;
		}

		public final RandomGeneratorFunctionsBuilder maxWeight(MinMaxWeight maxWeight) {
			if (maxWeight != null) {
				this.maxWeight = maxWeight;
			}
			return this;
		}

		public final RandomGeneratorFunctions build() {
			return new RandomGeneratorFunctions(this.minWeight, this.maxWeight);
		}
	}

	private record Chance(double value, double range) {

		private static Chance create(double value, double range) {
			return new Chance(BMatUtil.round(value, 1), BMatUtil.round(range, 1));
		}

		@Override
		public String toString() {
			return String.format("{value:%s,range:%s}", this.value, this.range);
		}
	}

	private class RandomGeneratorExt {

		private final double lvl;
		private final int minCount, maxCount;
		private final double minWeight, maxWeight;
		private final List<IProperty> list;
		private final Map<IProperty, RandomGeneratorData> pool = new IdentityLinkedHashMap<>();

		private RandomGeneratorExt(double lvl, int minCount, int maxCount, double minWeight, double maxWeight, Collection<IProperty> list) {
			this.lvl = lvl;
			this.minCount = minCount;
			this.maxCount = maxCount;
			this.minWeight = minWeight;
			this.maxWeight = maxWeight;
			this.list = new ArrayList<>(list);
		}

		public final RandomGenerator generator() {
			return RandomGenerator.this;
		}

		@Deprecated // rarity
		public final RandGenRes run() {
			if (print.run) {
				print.run(this);
			}

			lvl();
			required();
			chance();
			count();
			value();
			weight();

			if (print.result) {
				print.result(this);
			}

			return RandGenRes.create(RandomGenerator.this, this.lvl, this.pool);
			//rarity
		}

		private final double chance(IProperty property) {
			return property.chance(this.lvl, RandomGenerator.this.nerfLvl);
		}

		private final void foreachList(Predicate<IProperty> predicate) {
			Iterator<IProperty> iterator = this.list.iterator();
			while (iterator.hasNext()) {
				IProperty next = iterator.next();
				if (predicate.test(next)) {
					iterator.remove();
					this.put(next);
				}
			}
		}

		private final RandomGeneratorData put(IProperty property) {
			RandomGeneratorData data;
			if (!this.pool.containsKey(property)) {
				data = new RandomGeneratorData(property);
				this.pool.put(property, data);
			} else {
				data = this.pool.get(property);
			}
			return data;
		}

		private final void remove(RandomGeneratorData data) {
			this.remove(data.property);
		}

		private final RandomGeneratorData remove(IProperty property) {
			return this.pool.remove(property);
		}

		private final void foreachPool(Consumer<RandomGeneratorData> consumer) {
			Iterator<RandomGeneratorData> iterator = this.pool.values().iterator();
			while (iterator.hasNext()) {
				consumer.accept(iterator.next());
			}
		}

		private List<RandomGeneratorData> chanceList() {
			return this.dataList(RandomGeneratorData::hasChance);
		}

		private List<RandomGeneratorData> valueList() {
			return this.dataList(RandomGeneratorData::hasValue);
		}

		private List<RandomGeneratorData> weightList() {
			return this.dataList(RandomGeneratorData::hasWeight);
		}

		private List<RandomGeneratorData> dataList(Predicate<RandomGeneratorData> predicate) {
			return this.pool.values().stream().filter(predicate).toList();
		}

		private final void lvl() {
			foreachList(property -> property.levelRange().isIn(this.lvl));
			this.list.clear();
			this.list.addAll(this.pool.keySet());
			this.pool.clear();
			if (print.lvl) {
				print.lvl(this);
			}
		}

		private final void required() {
			foreachList(IProperty::required);
			if (print.required) {
				print.required(this);
			}
		}

		private final void chance() {
			foreachList(property -> {
				double rand = nextDouble.getAsDouble() * 100;
				double value = chance(property);

				if (rand <= value) {
					this.put(property).setChance(Chance.create(rand, value));
					return true;
				} else {
					return false;
				}
			});
			if (print.chance) {
				print.chance(this);
			}
		}

		private final void count() {
			int required = (int)this.pool.keySet().stream().filter(IProperty::required).count();
			int count = this.pool.size() - required;
			if (count < minCount || count > maxCount) {
				while (count < minCount && !this.list.isEmpty()) {
					int size = this.list.size();
					IProperty result;
					if (size == 1) {
						result = list.get(0);
						this.list.clear();
					} else {
						RandomCollection<IProperty> collection = new RandomCollection<IProperty>(nextDouble);
						for (IProperty property : this.list) {
							collection.add(chance(property), property);
						}
						result = collection.next();
						this.list.remove(result);
					}
					double chance = chance(result);
					this.put(result).setChance(Chance.create(chance, chance));
					count++;
				}

				List<RandomGeneratorData> chances = new ArrayList<>(chanceList());
				while (count > maxCount && !chances.isEmpty()) {
					if (chances.size() == 1) {
						this.remove(chances.remove(0));
					} else {
						RandomCollection<RandomGeneratorData> randomizer = new RandomCollection<>(nextDouble);
						for (RandomGeneratorData data : chances) {
							Chance chance = data.chance;
							randomizer.add(chance.value / chance.range, data);
						}
						RandomGeneratorData data = randomizer.next();
						this.list.add(data.property);
						this.remove(data);
					}
					count--;
				}
			}

			if (print.count) {
				print.count(this);
			}
		}

		private final void value() {
			foreachPool(data -> {
				data.setValue(data.property.valueRange(this.lvl, RandomGenerator.this.nerfLvl, RandomGenerator.this.nextDouble));
			});
			if (print.value) {
				print.value(this);
			}
		}

		private final void weight() {
			foreachPool(data -> {
				data.setWeight(data.property.weight(this.lvl, RandomGenerator.this.nerfLvl));
			});
			double weight = calculateWight();
			if (weight < this.minWeight || weight > this.maxWeight) {
				if (weight < this.minWeight) {
					RandomGenerator.this.functions.minWeight.apply(true, weight, this.minWeight, this.calculateWeightScale(weight, this.minWeight, true), new IdentityLinkedHashMap<>(this.pool), this::remove);
					weight = calculateWight();
				}
				if (weight > this.maxWeight) {
					RandomGenerator.this.functions.minWeight.apply(false, weight, this.maxWeight, this.calculateWeightScale(weight, this.maxWeight, false), new IdentityLinkedHashMap<>(this.pool), this::remove);
				}
			}

			if (print.weight) {
				print.weight(this);
			}
		}

		private final double calculateWeightScale(double weight, double limit, boolean min) {
			double value;
			if (weight == 0) {
				if (min) {
					value = limit;
				} else {
					value = 1d / limit;
				}
			} else {
				value = limit / weight;
			}
			return BMatUtil.round(value, 4);
		}

		private final double calculateWight() {
			double weight = 0;
			for (Map.Entry<IProperty, RandomGeneratorData> entry : this.pool.entrySet()) {
				weight += entry.getValue().getWeight();
			}
			return BMatUtil.round(weight, 2);
		}
	}

	public class RandomGeneratorData {

		public final IProperty property;
		private Chance chance = null;
		private ValueData value = null;
		private double weight = 0;

		public RandomGeneratorData(IProperty property) {
			this.property = property;
		}

		public final void applyScale(double scale) {
			if (this.hasValue()) {
				this.value.applyScale(scale);
			}
			this.weight *= scale;
		}

		public final Chance getChance() {
			return this.chance;
		}
		protected final void setChance(Chance chance) {
			this.chance = chance;
		}
		public final boolean hasChance() {
			return this.chance != null;
		}

		protected final ValueData getValue() {
			return this.value;
		}
		protected final void setValue(ValueData value) {
			this.value = value;
		}
		public final boolean hasValue() {
			return this.value != null;
		}

		public final double getWeight() {
			return this.weight;
		}
		protected final void setWeight(double weight) {
			this.weight = weight;
		}
		public final boolean hasWeight() {
			return this.weight != 0;
		}

		@Override
		public final String toString() {
			StringBuilder builder = new StringBuilder("{name:\"" + property + "\"");
			if (this.hasChance()) {
				builder.append(",chance:");
				builder.append(this.chance);
			}
			if (this.hasValue()) {
				builder.append(",value:");
				builder.append(this.value);
			}
			if (this.weight != 0.0) {
				builder.append(",weight:");
				builder.append(this.weight);
			}
			builder.append("}");
			return builder.toString();
		}

		@Override
		public final boolean equals(Object object) {
			if (object == null) {
				return false;
			} else if (object == this) {
				return true;
			} else if (!(object instanceof RandomGeneratorData data)) {
				return false;
			} else {
				return this.property == data.property;
			}
		}

		@Override
		public final int hashCode() {
			return this.property.hashCode();
		}
	}

	public static class RandGenRes {

		private final RandomGenerator generator;
		public final double lvl;
		public final Map<IProperty, RandGenResData> pool;

		private RandGenRes(RandomGenerator generator, double lvl, Map<IProperty, RandGenResData> pool) {
			this.generator = generator;
			this.lvl = lvl;
			this.pool = ImmutableMap.copyOf(pool);
		}

		private static final RandGenRes create(RandomGenerator generator, double lvl, Map<IProperty, RandomGeneratorData> pool) {
			return create(generator, false, lvl, pool);
		}

		public static final RandGenRes create(RandomGenerator generator, boolean clampRandom, double lvl, Map<IProperty, RandomGeneratorData> pool) {
			if (generator != null) {
				clampRandom = generator.clampRandom;
			}
			Map<IProperty, RandGenResData> map = new IdentityLinkedHashMap<>();
			for (Entry<IProperty, RandomGeneratorData> entry : pool.entrySet()) {
				IProperty key = entry.getKey();
				RandomGeneratorData value = entry.getValue();
				Double num = null;
				if (value.hasValue()) {
					num = value.getValue().value(LogicUtil.valueOrDefault(value.property.isClampRandom(), clampRandom));
				}
				map.put(key, new RandGenResData(key, value.getChance(), num));
			}
			return new RandGenRes(generator, lvl, map);
		}

		private static final String TAB_1 = "\n  ";
		private static final String TAB_2 = "\n    ";

		public static class PoolBuilderComparator implements Comparator<IProperty> {
			@Override
			public final int compare(IProperty o1, IProperty o2) {
				boolean bl1 = o1.required();
				if (bl1) {
					return bl1 == o2.required() ? compareNonRequired(o1, o2) : -1;
				} else {
					return compareNonRequired(o1, o2);
				}
			}

			protected int compareNonRequired(IProperty o1, IProperty o2) {
				return 0;
			}
		}

		public static final String poolBuilder(List<RandGenRes> list) {
			return poolBuilder(list.toArray(new RandGenRes[0]));
		}

		public static final String poolBuilder(Comparator<IProperty> comparator, List<RandGenRes> list) {
			return poolBuilder(comparator, list.toArray(new RandGenRes[0]));
		}

		public static String poolBuilder(RandGenRes... list) {
			return poolBuilder(new PoolBuilderComparator(), list);
		}

		public static String poolBuilder(Comparator<IProperty> comparator, RandGenRes... list) {
			StringBuilder builder = new StringBuilder("\n");
			Map<RandomGenerator, Double2ObjectArrayMap<List<RandGenRes>>> map = new IdentityLinkedHashMap<>();
			for (RandGenRes res : list) {
				map.computeIfAbsent(res.generator, (key) -> new Double2ObjectArrayMap<>()).computeIfAbsent(res.lvl, (key) -> new ArraySetStrict<>()).add(res);
			}
			for (Entry<RandomGenerator, Double2ObjectArrayMap<List<RandGenRes>>> entry : map.entrySet()) {
				builder.append(entry.getKey());
				Double2ObjectArrayMap<List<RandGenRes>> value = entry.getValue();
				if (value.size() == 1) {
					builder.append(" ");
					it.unimi.dsi.fastutil.doubles.Double2ObjectMap.Entry<List<RandGenRes>> next = value.double2ObjectEntrySet().iterator().next();
					builder.append(next.getDoubleKey());
					poolBuilder(comparator, TAB_1, builder, next.getValue());
				} else {
					for (it.unimi.dsi.fastutil.doubles.Double2ObjectMap.Entry<List<RandGenRes>> dEntry : value.double2ObjectEntrySet()) {
						builder.append(TAB_1);
						builder.append(dEntry.getDoubleKey());
						poolBuilder(comparator, TAB_2, builder, dEntry.getValue());
					}
				}
			}

			return builder.toString();
		}

		@SuppressWarnings("unchecked")
		private static final void poolBuilder(Comparator<IProperty> comparator, String tab, StringBuilder builder, List<RandGenRes> list) {
			if (list.size() == 1) {
				for (String value : list.get(0).poolString(true, comparator).split("\n")) {
					builder.append(tab);
					builder.append(value);
				}
			} else {
				// init
				List<IProperty> properties = new ArraySetStrict<>();
				for (RandGenRes res : list) {
					for (RandGenResData entry : res.pool.values()) {
						properties.add(entry.property);
					}
				}
				properties.sort(comparator);

				// names and requireds
				List<IProperty> names = new ArrayList<>();
				List<String> requireds = new ArrayList<>();

				for (IProperty property : properties) {
					names.add(property);
					if (property.required()) {
						requireds.add("y");
					} else {
						requireds.add(null);
					}
				}

				//format init
				List<List<?>> format = new ArrayList<>();
				format.add(names);
				format.add(requireds);

				int length = 0;

				//values
				for (RandGenRes res : list) {
					List<Double> values = new ArrayList<>();
					Collection<RandGenResData> pool = res.pool.values();
					for (IProperty property : properties) {
						boolean found = false;
						for (RandGenResData data : pool) {
							if (data.property == property) {
								if (data.hasValue()) {
									values.add(data.value());
									found = true;
								}
								break;
							}
						}
						if (!found) {
							values.add(null);
						}
					}
					format.add(values);
					length = Math.max(values.size(), length);
				}

				//static info
				int size = format.size() - 3;

				List<Double> mins = new ArrayList<>();
				List<Double> maxs = new ArrayList<>();
				List<Double> averages = new ArrayList<>();
				List<Integer> counts = new ArrayList<>();

				for (int i = 0; i < length; i++) {
					boolean found = false;
					double min = Double.MAX_VALUE;
					double max = Double.MIN_VALUE;
					double average = 0;
					int count = 0;
					for (int j = 2; j < size; j++) {
						Double d = ((List<Double>)format.get(j)).get(i);
						if (d != null) {
							found = true;
							min = Math.min(min, d);
							max = Math.max(max, d);
							average += d;
							count++;
						}
					}
					if (found) {
						mins.add(BMatUtil.round(min, 2));
						maxs.add(BMatUtil.round(max, 2));
						averages.add(BMatUtil.round(average / count, 2));
						counts.add(count);
					} else {
						mins.add(null);
						maxs.add(null);
						averages.add(null);
						counts.add(null);
					}
				}

				format.add(new ArrayList<>());
				format.add(mins);
				format.add(maxs);
				format.add(averages);
				format.add(counts);

				size += 3;

				for (List<?> l : format) {
					((List<Object>)l).add(0, null);
				}
				((List<Object>)format.get(0)).set(0, "name");
				((List<Object>)format.get(1)).set(0, "r");
				for (int i = 2; i < size; i++) {
					List<Object> array = ((List<Object>)format.get(i));
					array.set(0, i -1);
					array.add(array.stream().filter(Objects::nonNull).count() - 1);
				}
				((List<Object>)format.get(format.size() - 4)).set(0, "min");
				((List<Object>)format.get(format.size() - 3)).set(0, "max");
				((List<Object>)format.get(format.size() - 2)).set(0, "mid");
				((List<Object>)format.get(format.size() - 1)).set(0, "count");

				//print

				List<StringBuilder> values = BStringUtil.tableFormat(" || ", format.toArray(new List<?>[0]));
				size = properties.size() + 2;
				for (int i = 0; i < size; i++) {
					builder.append(tab);
					builder.append(values.get(i));
				}
			}
		}

		private final String poolString(boolean pretty, Comparator<IProperty> comparator) {
			StringBuilder poolBuilder = new StringBuilder();

			List<IProperty> names = new ArrayList<>();
			List<String> requireds = new ArrayList<>();
			boolean haveRequire = false;
			List<Double> values = new ArrayList<>();

			List<IProperty> properties = new ArrayList<>(this.pool.keySet());
			if (comparator != null) {
				properties.sort(comparator);
			} 

			for (IProperty key : properties) {
				RandGenResData data = this.pool.get(key);
				names.add(data.property);
				if (!data.hasChance()) {
					requireds.add("r");
					haveRequire = true;
				} else {
					requireds.add(null);
				}
				if (data.hasValue()) {
					values.add(data.value());
				} else {
					values.add(null);
				}
			}

			if (pretty) {
				if (haveRequire) {
					requireds = null;
				}
				Iterator<StringBuilder> iterator = BStringUtil.tableFormat(" || ", names, requireds, values).iterator();
				while(iterator.hasNext()) {
					poolBuilder.append(iterator.next());
					if (iterator.hasNext()) {
						poolBuilder.append("\n");
					}
				}
			} else {
				poolBuilder.append("[");
				int size = names.size();
				for (int i = 0; i < size; i++) {
					StringBuilder builder = new StringBuilder("{name:");

					builder.append(names.get(i));

					if (haveRequire && requireds.get(i) != null) {
						builder.append(",required");
					}
					Double value = values.get(i);
					if (value != null && value != 0.0) {
						builder.append(",value:");
						builder.append(value);
					}

					builder.append("}");
					if (i != size - 1) {
						builder.append(",");
					}
					poolBuilder.append(builder);
				}
				poolBuilder.append("]");
			}

			return poolBuilder.toString();
		}

		@Override
		public String toString() {
			return this.toString(false, null);
		}

		public String toString(boolean pretty, Comparator<IProperty> comparator) {
			String pool = this.poolString(pretty, comparator);
			if (pretty) {
				pool = String.format("}\n%s\n", pool);
			} else {
				pool = String.format(",pool:%s}", pool);
			}
			return String.format("{generator:%s,lvl:%s%s", this.generator, this.lvl, pool);
		}
	}

	public static record RandGenResData(IProperty property, Chance chance, Double value) {
		public boolean hasChance() {
			return this.chance != null;
		}
		public boolean hasValue() {
			return this.value != null;
		}
	}
}
