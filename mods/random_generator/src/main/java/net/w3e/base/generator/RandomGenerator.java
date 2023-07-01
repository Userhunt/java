package net.w3e.base.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.home.main.MainFrame;
import net.w3e.base.collection.IdentityLinkedHashMap;
import net.w3e.base.generator.collection.RandomCollection;

public class RandomGenerator {

	private final RandomGeneratorPrint print;
	private final DoubleSupplier nextDouble;
	private final Int2IntFunction nextInt;
	private final boolean nerfLvl;

	/**
	 * 
	 * @param print - debug
	 * @param nextDouble - random
	 * @param nerfLvl - lvlRange(5,10), valueRange(10,15,x), value = 10 + x * (lvl - (nerfLvl ? 5 : 0))
	 */
	public RandomGenerator(RandomGeneratorPrint print, DoubleSupplier nextDouble, Int2IntFunction nextInt, boolean nerfLvl) {
		this.print = print;
		this.nextDouble = nextDouble;
		this.nextInt = nextInt;
		this.nerfLvl = nerfLvl;
	}

	public static record RandomGeneratorPrint(boolean run, boolean lvl, boolean required, boolean chance, boolean count, boolean value) {
		public static final RandomGeneratorPrint EMPTY = new RandomGeneratorPrint(false, false, false, false, false, false);
		public static final RandomGeneratorPrint ALL = new RandomGeneratorPrint(true, true, true, true, true, true);

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
			info("pool: " + ext.pool);
		}

		private final void chance(RandomGeneratorExt ext) {
			title("chance");
			info("list: " + ext.list);
			info("pool: " + ext.pool);
			info("chances: " + ext.chances);
		}

		private final void count(RandomGeneratorExt ext) {
			title("count");
			info("list: " + ext.list);
			info("pool: " + ext.pool);
			info("chances: " + ext.chances);
		}

		private final void value(RandomGeneratorExt ext) {
			title("value");
			info(ext.values);
		}

		private final void error(Object object) {
			MainFrame.LOGGER.error(object);
		}

		private final void title(Object object) {
			MainFrame.LOGGER.debug(object);
		}

		private final void info(Object object) {
			MainFrame.LOGGER.info(" " + object);
		}
	}

	public final void generate(double lvl, int minCount, int maxCount, int minWeight, int maxWeight, Collection<IProperty> list) {
		new RandomGeneratorExt(lvl, minCount, maxCount, minWeight, maxWeight, list).run();
	}

	private class RandomGeneratorExt {

		private final double lvl;
		private final int minCount, maxCount;
		private final int minWeight, maxWeight;
		private final List<IProperty> list;
		private final List<IProperty> pool = new ArrayList<>();
		private final Map<IProperty, Chance> chances = new IdentityLinkedHashMap<>();
		private final Map<IProperty, double[]> values = new IdentityLinkedHashMap<>();

		private RandomGeneratorExt(double lvl, int minCount, int maxCount, int minWeight, int maxWeight, Collection<IProperty> list) {
			this.lvl = lvl;
			this.minCount = minCount;
			this.maxCount = maxCount;
			this.minWeight = minWeight;
			this.maxWeight = maxWeight;
			this.list = new ArrayList<>(list);
		}

		private record Chance(double value, double range) {}

		public void run() {
			if (print.run) {
				print.run(this);
			}
			lvl();
			required();
			chance();
			count();
			value();
			//fix weight

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
					pool.add(next);
				}
			}
		}

		private final void foreachPool(Consumer<IProperty> consumer) {
			Iterator<IProperty> iterator = this.pool.iterator();
			while (iterator.hasNext()) {
				consumer.accept(iterator.next());
			}
		}

		private final void lvl() {
			foreachList(property -> property.levelRange().isIn(this.lvl));
			this.list.clear();
			this.list.addAll(this.pool);
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
					this.chances.put(property, new Chance(rand, value));
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
			int required = (int)this.pool.stream().filter(IProperty::required).count();
			while(true) {
				int count = this.pool.size() - required;
				if (count < minCount || count > maxCount) {
					while(count < minCount && !this.list.isEmpty()) {
						int size = this.list.size();
						if (size == 1) {
							this.pool.add(this.list.get(0));
							this.list.clear();
						} else {
							RandomCollection<IProperty> collection = new RandomCollection<IProperty>(nextDouble);
							for (IProperty property : list) {
								collection.add(chance(property), property);
							}
							IProperty property = collection.next();
							this.list.remove(property);
							this.pool.add(property);
							double chance = chance(property);
							this.chances.put(property, new Chance(chance, chance));
						}
						count++;
					}
					while(count > maxCount && !this.chances.isEmpty()) {
						int size = this.chances.size();
						if (size == 1) {
							this.pool.add(this.chances.keySet().iterator().next());
							this.chances.clear();
						} else {
							RandomCollection<IProperty> collection = new RandomCollection<IProperty>(nextDouble);
							for (Entry<IProperty, Chance> entry : this.chances.entrySet()) {
								Chance value = entry.getValue();
								collection.add(value.range / value.value, entry.getKey());
							}
							IProperty property = collection.next();
							this.list.add(property);
							this.pool.remove(property);
							this.chances.remove(property);
						}
						count--;
					}
				}
				break;
			}

			if (print.count) {
				print.count(this);
			}
		}

		private final void value() {
			foreachPool(property -> {
				values.put(property, property.valueRange(this.lvl, RandomGenerator.this.nerfLvl, RandomGenerator.this.nextDouble));
			});
			if (print.value) {
				print.value(this);
			}
		}
	}
}
