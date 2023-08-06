package net.home.random_generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.JButton;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.home.random_generator.GenRegistry.PropertyTypeEnum;
import net.w3e.base.api.window.FrameWin;
import net.w3e.base.generator.IProperty;
import net.w3e.base.generator.RandomGenerator;
import net.w3e.base.generator.RandomGenerator.RandGenRes;
import net.w3e.base.generator.RandomGenerator.RandomGeneratorPrint;
import net.w3e.base.generator.RandomGenerator.RandGenRes.PoolBuilderComparator;
import net.w3e.base.generator.dungeon.CubeDungeonGenerator;
import net.w3e.base.generator.dungeon.Room;
import net.w3e.base.generator.dungeon.Room.RoomBuilder;
import net.w3e.base.generator.property.GenNormalProperty;
import net.w3e.base.generator.property.GenRequiredProperty;
import net.w3e.base.json.FileUtil;

public class GenScreen extends FrameObject {

	private  List<IProperty> LAST = new ArrayList<>();

	private final PoolBuilderComparator COMPARATOR = new PoolBuilderComparator() {
		@Override
		protected int compareNonRequired(IProperty o1, IProperty o2) {
			int p1 = 0;
			int p2 = 0;
			int i = 0;
			for (IProperty property : GenScreen.this.LAST) {
				if (property == o1) {
					p1 = i;
				} else if (property == o2) {
					p2 = i;
				}
				i++;
			}
			return p1 - p2;
		}
	};

	public static void main(String[] args) {
		MainFrame.register(new GenScreen());
		MainFrame.run(args);
	}

	private final Flags FLAGS = new Flags();
	private final List<Item> ITEMS = Arrays.asList(new Sword(), new Axe());
	private final Dungeon DUNGEON = new Dungeon();

	private int count;

	protected void init(FrameWin fw, List<String> args) {
		GenRegistry.register();

		this.count = 12;
		XPos x = new XPos();
		YPos y = new YPos();
		int maxX = 0;
		int maxY = 0;

		// Flags
		this.FLAGS.init(fw, x, y);
		// Items
		for (Item item : ITEMS) {
			item.init(fw, x, y);
			maxX = Math.max(maxX, x.get());
			maxY = Math.max(maxY, y.get());
		}
		// Dungeon
		this.DUNGEON.init(fw, x, y);

		maxX = Math.max(maxX, x.get());
		maxY = Math.max(maxY, y.get());

		fw.setSize(maxX + 10, maxY + 10);
	}

	private final void addButton(String key, BiFunction<Boolean, RandomGenerator, RandGenRes> function, int x, YPos y, FrameWin fw) {
		int value = y.get();
		super.addButton(key, (e) -> 
			MainFrame.LOGGER.warn(key + " " + function.apply(true, this.generator(true)).toString(true, COMPARATOR))
		, x, y, fw);
		y.set(value);
		super.addButton(key + "_n", (e) -> 
			MainFrame.LOGGER.warn(key + "_" + this.count + " " + run(this.count, function))
		, x + dX() - 5, y, fw);
	}

	private final String run(int count, BiFunction<Boolean, RandomGenerator, RandGenRes> function) {
		List<RandGenRes> list = new ArrayList<>();
		RandomGenerator generator = this.generator(false);
		for (int i = 0; i < count; i++) {
			list.add(function.apply(i == 0, generator));
		}
		return RandGenRes.poolBuilder(COMPARATOR, list);
	}

	private final RandomGenerator generator(boolean print) {
		return new RandomGenerator(print ? RandomGeneratorPrint.ALL : RandomGeneratorPrint.NONE, GenRegistry.RANDOM::nextDouble, this.FLAGS.nerfLvl, this.FLAGS.clampRandom, RandomGenerator.FUNCTIONS);
	}


	private final Map<String, Function<JsonObject, IProperty>> readMap() {
		Map<String, Function<JsonObject, IProperty>> map = new LinkedHashMap<>();
		map.put(GenNormalProperty.class.getSimpleName(), GenNormalProperty::new);
		map.put(GenRequiredProperty.class.getSimpleName(), GenRequiredProperty::new);
		return map;
	}

	private class Flags {

		private boolean nerfLvl;
		private boolean clampRandom;
		private boolean refill;
		private boolean filter;

		public void init(FrameWin fw, XPos x, YPos y) {
			this.nerfLvl = true;
			this.clampRandom = false;
			this.refill = true;
			this.filter = false;
			int x1 = 5;
			addCheckBox("nerfLvl", e -> this.nerfLvl = e.isSelected(), x1, y, fw).setSelected(true);
			y.back();
			x1 += dX() - 5;
			addCheckBox("clampRandom", e -> this.clampRandom = e.isSelected(), x1, y, fw);
			y.back();
			x1 += dX();
			addCheckBox("refill", e -> this.refill = e.isSelected(), x1, y, fw).setSelected(true);
			y.back();
			x1 += dX() - 5;
			addCheckBox("filter", e -> this.filter = e.isSelected(), x1, y, fw);
		}
	}

	private abstract class Type {
		public abstract void init(FrameWin fw, XPos x, YPos y);
		protected abstract PropertyTypeEnum type();

		protected final void addButton(String key, BiFunction<Boolean, RandomGenerator, RandGenRes> function, int x, YPos y, FrameWin fw) {
			GenScreen.this.addButton(key, (update, generator) -> {
				if (update && GenScreen.this.FLAGS.refill) {
					this.type().refill();
				}
				return function.apply(update, generator);
			}, x, y, fw);
		}

		protected final List<IProperty> getList(boolean update) {
			if (update) {
				if (GenScreen.this.FLAGS.filter) {
					List<IProperty> list = new ArrayList<>(this.type().list());
					MainFrame.LOGGER.debug("filter");
					MainFrame.LOGGER.info(list);
					list.removeIf(this.type().selector()::inversedTest);
					MainFrame.LOGGER.info(list);
					return list;
				} else {
					return this.type().list();
				}
			} else {
				return GenScreen.this.LAST;
			}
		}

		protected void initSystemBtn(FrameWin fw, XPos x, YPos y) {
			String type = this.type().displayName();
			y.add(5);
			GenScreen.this.addButton("Save " + type, this::save, x.get(), y, fw);
			y.back();
			GenScreen.this.addButton("Read " + type, this::read, x.get() + dX() - 5, y, fw);
			GenScreen.this.addButton("Refill " + type, this::refill, x.get(), y, fw);
		}

		private final void save(JButton e) {
			if (GenScreen.this.LAST.isEmpty()) {
				GenScreen.this.LAST = getList(true);
			}
			GenScreen.this.save(this.type().name());
		}

		private final void read(JButton e) {
			GenScreen.this.read(this.type().name());
		}

		private final void refill(JButton e) {
			this.type().refill();
		}
	}

	private abstract class Item extends Type {
		@Override
		public final void init(FrameWin fw, XPos x, YPos y) {
			PropertyTypeEnum type = type();
			y.set(5);
			y.next();
			addButton("Wooden " + type.displayName(), this::wooden, x.get(), y, fw);
			addButton("Stone " + type.displayName(), this::stone, x.get(), y, fw);
			addButton("Iron " + type.displayName(), this::iron, x.get(), y, fw);
			addButton("Golden " + type.displayName(), this::golden, x.get(), y, fw);

			this.initSystemBtn(fw, x, y);

			x.next().next().remove(5);
			y.next();
		}

		private RandGenRes wooden(boolean update, RandomGenerator generator) {
			return GenScreen.this.generate(generator, 0.1, 0, 0, 0, 0, getList(update));
		}

		private RandGenRes stone(boolean update, RandomGenerator generator) {
			return GenScreen.this.generate(generator, 1, 0, 3, 0, 9, getList(update));
		}

		private RandGenRes iron(boolean update, RandomGenerator generator) {
			return GenScreen.this.generate(generator, 3, 1, 5, 0, 12, getList(update));
		}

		private RandGenRes golden(boolean update, RandomGenerator generator) {
			return GenScreen.this.generate(generator, 4, 3, 4, 6, 12, getList(update));
		}
	}

	private class Sword extends Item {
		@Override
		protected PropertyTypeEnum type() {
			return PropertyTypeEnum.sword;
		}
	}

	private class Axe extends Item {
		@Override
		protected PropertyTypeEnum type() {
			return PropertyTypeEnum.axe;
		}
	}

	private class Dungeon {
		public void init(FrameWin fw, XPos x, YPos y) {
			y.set(5);
			y.next();
			addButton("Small Dungeon", this::small, x.get(), y, fw);

			x.next();
		}

		private void small(JButton button) {
			List<Room> rooms = new ArrayList<>();
			rooms.add(new RoomBuilder(GenRegistry.DungeonRegistry.ENTERANCE.getRegistryName()).build());
			CubeDungeonGenerator generator = new CubeDungeonGenerator(rooms);

			System.out.println(generator);
		}
	}

	private final void save(String key) {
		FileUtil.write(new File(key + ".json"), IProperty.save(this.LAST));
	}

	private final void read(String key) {
		this.LAST = IProperty.read((JsonArray)FileUtil.read(new File(key + ".json")), this.readMap());
	}

	private final RandGenRes generate(RandomGenerator generator, double lvl, int minCount, int maxCount, double minWeight, double maxWeight, List<IProperty> list) {
		GenScreen.this.LAST = list;
		return generator.generate(lvl, minCount, maxCount, minWeight, maxWeight, list);
	}

	@Override
	public final String getName() {
		return "Random Generator";
	}

	@Override
	public final String fastKey() {
		return "rand_gen";
	}

	@Override
	public final int[] version() {
		return new int[]{1,0,0};
	}
}
