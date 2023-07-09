package net.home.random_generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import net.home.main.MainFrame;
import net.home.random_generator.ItemRegistry.*;
import net.w3e.base.collection.ArraySet.ArraySetStrict;
import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.IProperty;
import net.w3e.base.generator.PropertySelector;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.generator.RandomGenerator;
import net.w3e.base.generator.RandomGenerator.RandomGeneratorPrint;
import net.w3e.base.generator.dungeon.DungeonGenerator;
import net.w3e.base.generator.dungeon.Room;
import net.w3e.base.generator.dungeon.Room.RoomBuilder;

public abstract class GenRegistry {

	public static final List<IProperty> LIST = new ArraySetStrict<>();

	public static class PropertyList<T extends IProperty> extends ArrayList<T> {
		@Override
		public final void clear() {
			LIST.removeAll(this);
			super.clear();
		}

		@Override
		public final boolean add(T e) {
			LIST.add(e);
			return super.add(e);
		}

		@Override
		public final boolean addAll(Collection<? extends T> c) {
			LIST.addAll(c);
			return super.addAll(c);
		}

		@Override
		public final void add(int index, T element) {
			LIST.add(index, element);
			super.add(index, element);
		}

		@Override
		public final boolean addAll(int index, Collection<? extends T> c) {
			LIST.addAll(index, c);
			return super.addAll(index, c);
		}

		@Override
		public final boolean remove(Object o) {
			LIST.remove(o);
			return super.remove(o);
		}

		@Override
		public final T remove(int index) {
			T property = super.remove(index);
			LIST.remove(property);
			return property;
		}

		@Override
		public final boolean removeAll(Collection<?> c) {
			LIST.removeAll(c);
			return super.removeAll(c);
		}

		@Override
		public final boolean removeIf(Predicate<? super T> filter) {
			boolean remove = false;
			Iterator<T> iterator = this.iterator();
			while(iterator.hasNext()) {
				T next = iterator.next();
				if (filter.test(next)) {
					this.remove(next);
					remove = true;
				}
			}
			return remove;
		}

		@Override
		protected final void removeRange(int fromIndex, int toIndex) {}
	}

	public static enum PropertyTypeEnum {
		sword(new SwordRegistry()),
		axe(new AxeRegistry()),
		dungeon(new DungeonRegistry());

		private final GenRegistry registry;

		private PropertyTypeEnum(GenRegistry registry) {
			this.registry = registry;
		}

		public void refill() {
			this.registry.refill();
		}
		public List<IProperty> list() {
			return this.registry.list();
		}
		public PropertySelector selector() {
			return this.registry.selector();
		}

		public final String displayName() {
			String name = this.name();
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}

	protected final List<IProperty> list = new PropertyList<>();

	public abstract void refill();
	public List<IProperty> list() {
		return this.list;
	}
	public abstract PropertySelector selector();

	public static class DungeonRegistry extends GenRegistry {

		public final List<Room> LIST = new PropertyList<>();

		public static final PropertyType ENTERANCE = DungeonGenerator.registerRoomType("enterance", null);

		@Override
		public final void refill() {
			LIST.clear();

			LIST.add(new RoomBuilder(ENTERANCE.getRegistryName()).required().valueRange(GenRange.VALUE_1).build());
		}
		@Override
		@SuppressWarnings("unchecked")
		public List<IProperty> list() {
			return (List<IProperty>)(List<?>)this.LIST;
		}

		@Override
		public PropertySelector selector() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'selector'");
		}
	}

	public static void register() {}

	public static final Random RANDOM = MainFrame.RANDOM;
	public static final RandomGenerator GENERATOR = new RandomGenerator(RandomGeneratorPrint.ALL, RANDOM::nextDouble, true, false, RandomGenerator.FUNCTIONS);

	public static PropertyType register(String type, String subType, String id, String... flags) {
		return PropertyType.register(new PropertyType(type, subType, id, flags));
	}

	public static PropertyType register(String type, String subType, String id, Map<String, List<String>> flags) {
		return PropertyType.register(new PropertyType(type, subType, id, flags));
	}

	static {
		for (PropertyTypeEnum property : PropertyTypeEnum.values()) {
			property.refill();
		}
	}
}
