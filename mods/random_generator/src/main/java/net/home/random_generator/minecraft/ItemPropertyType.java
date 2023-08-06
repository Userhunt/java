package net.home.random_generator.minecraft;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.w3e.base.generator.PropertyType;

import static net.home.random_generator.minecraft.ItemPropertyType.ParameterItemFlag.*;
import static net.home.random_generator.minecraft.ItemPropertyType.TypeItemFlag.*;
import static net.home.random_generator.minecraft.ItemPropertyType.PropertyTypeItemFlag.*;
import static net.home.random_generator.minecraft.ItemPropertyType.WeaponRangeItemFlag.*;
import static net.home.random_generator.minecraft.ItemPropertyType.IdItemFlag.*;

public class ItemPropertyType extends PropertyType {

	public static final String FLAG_ITEM = PropertyType.registerFlag("item");

	public static final ItemPropertyType ATTRIBUTE_ATTACK_DAMAGE = register("item", "attribute", "attack_damage", new ItemFlagsBuilder().parameter(ATTRIBUTE).itemType(WEAPON, TOOL).propertyType(ATTACK).weaponRange(MELEE).item(SWORD, AXE).build());
	public static final ItemPropertyType ATTRIBUTE_ATTACK_SPEED = register("item", "attribute", "attack_speed", new ItemFlagsBuilder().parameter(ATTRIBUTE).itemType(WEAPON, TOOL).propertyType(ATTACK).weaponRange(MELEE).item(SWORD, AXE).build());
	public static final ItemPropertyType ATTRIBUTE_ARMOR = register("item", "attribute", "armor", new ItemFlagsBuilder().parameter(ATTRIBUTE).itemType(WEAPON, ARMOR).propertyType(DEFENCE).weaponRange(MELEE).item(SWORD).build());
	public static final ItemPropertyType ATTRIBUTE_MOVEMENT_SPEED = register("item", "attribute", "movement_speed", new ItemFlagsBuilder().parameter(ATTRIBUTE).propertyType(MISC).weaponRange(MELEE, RANGE).item(SWORD, AXE).build());
	public static final ItemPropertyType ATTRIBUTE_MAX_HEALTH = register("item", "attribute", "max_health", new ItemFlagsBuilder().parameter(ATTRIBUTE).itemType(ARMOR).propertyType(DEFENCE).weaponRange(MELEE, RANGE).item(SWORD, AXE).build());
	public static final ItemPropertyType ATTRIBUTE_KNOCKBACK_RESITANCE = register("item", "attribute", "knockback_resitance", new ItemFlagsBuilder().parameter(ATTRIBUTE).itemType(ARMOR).propertyType(DEFENCE).weaponRange(MELEE).item(SWORD).build());
	public static final ItemPropertyType ENCHANT_UNBREAKING = register("item", "enchant", "unbreaking", new ItemFlagsBuilder().parameter(ENCHANT).itemType(ARMOR, TOOL, WEAPON).propertyType(MISC).weaponRange(MELEE, RANGE).item(SWORD, AXE).build());
	public static final ItemPropertyType ENCHANT_MENDING = register("item", "enchant", "mending", new ItemFlagsBuilder().parameter(ENCHANT).itemType(ARMOR, TOOL, WEAPON).propertyType(MISC).weaponRange(MELEE, RANGE).item(SWORD, AXE).build());
	public static final ItemPropertyType ENCHANT_FIRE_ASPECT = register("item", "enchant", "fire_aspect", new ItemFlagsBuilder().parameter(ENCHANT).itemType(WEAPON).propertyType(ATTACK).weaponRange(MELEE).item(SWORD).build());

	@Deprecated
	public static String registerFlag(String flag) {
		return PropertyType.registerFlag(flag);
	}

	public static final ItemFlag registerItemFlag(String flag) {
		return registerItemFlag(flag, ItemFlag::new);
	}

	public static final <T extends ItemFlag> T registerItemFlag(String flag, Function<String, T> factory) {
		return factory.apply(PropertyType.registerFlag("item/" + flag));
	}

	public static ItemPropertyType register(String type, String subType, String id, String... flags) {
		return PropertyType.register(new ItemPropertyType(type, subType, id, flags));
	}

	public static ItemPropertyType register(String type, String subType, String id, Map<String, List<String>> flags) {
		return PropertyType.register(new ItemPropertyType(type, subType, id, flags));
	}

	public ItemPropertyType(String type, String attribute, String id, Map<String, List<String>> flags) {
		super(type, attribute, id, flags);
	}

	public ItemPropertyType(String type, String attribute, String id, String[] flags) {
		super(type, attribute, id, flags);
	}

	public static class ItemFlagsBuilder extends FlagsTypedBuilder {

		private String parameter;
		private String[] itemType;
		private String propertyType;
		private String[] weaponRange;
		private String[] item;

		public ItemFlagsBuilder() {
			super(FLAG_ITEM);
		}

		protected final String push(String previous, ItemFlag flag) {
			return this.push(previous, flag.flag);
		}

		protected final String[] push(String[] previous, ItemFlag[] flags) {
			return this.push(previous, Arrays.stream(flags).map(f -> f.flag).toArray(String[]::new));
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType.ParameterItemFlag#ATTRIBUTE
		 * @see net.home.random_generator.minecraft.ItemPropertyType.ParameterItemFlag#ENCHANT
		 */
		public final ItemFlagsBuilder parameter(ParameterItemFlag flag) {
			this.parameter = push(this.parameter, flag.flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType.TypeItemFlag#ARMOR
		 * @see net.home.random_generator.minecraft.ItemPropertyType.TypeItemFlag#TOOL
		 * @see net.home.random_generator.minecraft.ItemPropertyType.TypeItemFlag#WEAPON
		 */
		public final ItemFlagsBuilder itemType(TypeItemFlag... flag) {
			this.itemType = push(this.itemType, flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType.PropertyTypeItemFlag#ATTACK
		 * @see net.home.random_generator.minecraft.ItemPropertyType.PropertyTypeItemFlag#DEFENCE
		 * @see net.home.random_generator.minecraft.ItemPropertyType.PropertyTypeItemFlag#MISC
		 */
		public ItemFlagsBuilder propertyType(PropertyTypeItemFlag flag) {
			this.propertyType = push(this.propertyType, flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType.WeaponRangeItemFlag#MELEE
		 * @see net.home.random_generator.minecraft.ItemPropertyType.WeaponRangeItemFlag#RANGE
		 */
		public ItemFlagsBuilder weaponRange(WeaponRangeItemFlag... flag) {
			this.weaponRange = push(this.weaponRange, flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType.IdItemFlag#SWORD
		 * @see net.home.random_generator.minecraft.ItemPropertyType.IdItemFlag#AXE
		 */
		public ItemFlagsBuilder item(IdItemFlag... flag) {
			this.item = push(this.item, flag);
			return this;
		}
	}

	public static class ItemFlag {

		public final String flag;

		protected ItemFlag(String flag) {
			this.flag = flag;
		}
	}

	public static class ParameterItemFlag extends ItemFlag {

		public static final ParameterItemFlag ATTRIBUTE = registerFlag("attribute");
		public static final ParameterItemFlag ENCHANT = registerFlag("enchant");

		public ParameterItemFlag(String flag) {
			super(flag);
		}

		public static final ParameterItemFlag registerFlag(String flag) {
			return registerItemFlag(flag, ParameterItemFlag::new);
		}
	}

	public static class TypeItemFlag extends ItemFlag {

		public static final TypeItemFlag ARMOR = registerFlag("armor");
		public static final TypeItemFlag TOOL = registerFlag("tool");
		public static final TypeItemFlag WEAPON = registerFlag("weapon");

		public TypeItemFlag(String flag) {
			super(flag);
		}

		public static final TypeItemFlag registerFlag(String flag) {
			return registerItemFlag(flag, TypeItemFlag::new);
		}
	}

	public static class PropertyTypeItemFlag extends ItemFlag {

		public static final PropertyTypeItemFlag ATTACK = registerFlag("attack");
		public static final PropertyTypeItemFlag DEFENCE = registerFlag("defence");
		public static final PropertyTypeItemFlag MISC = registerFlag("misc");

		public PropertyTypeItemFlag(String flag) {
			super(flag);
		}

		public static final PropertyTypeItemFlag registerFlag(String flag) {
			return registerItemFlag(flag, PropertyTypeItemFlag::new);
		}
	}

	public static class WeaponRangeItemFlag extends ItemFlag {

		public static final WeaponRangeItemFlag MELEE = registerFlag("melee");
		public static final WeaponRangeItemFlag RANGE = registerFlag("range");

		public WeaponRangeItemFlag(String flag) {
			super(flag);
		}

		public static final WeaponRangeItemFlag registerFlag(String flag) {
			return registerItemFlag(flag, WeaponRangeItemFlag::new);
		}
	}

	public static class IdItemFlag extends ItemFlag {

		public static final IdItemFlag SWORD = registerFlag("sword");
		public static final IdItemFlag AXE = registerFlag("axe");

		public IdItemFlag(String flag) {
			super(flag);
		}

		public static final IdItemFlag registerFlag(String flag) {
			return registerItemFlag(flag, IdItemFlag::new);
		}
	}
}
