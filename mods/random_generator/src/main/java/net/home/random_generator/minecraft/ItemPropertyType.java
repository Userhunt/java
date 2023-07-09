package net.home.random_generator.minecraft;

import java.util.List;
import java.util.Map;

import net.w3e.base.generator.PropertyType;

public class ItemPropertyType extends PropertyType {

	public static final String FLAG_ITEM = PropertyType.registerFlag("item");

	public static final String FLAG_ATTRIBUTE = registerFlag("attribute");
	public static final String FLAG_ENCHANT = registerFlag("enchant");

	public static final String FLAG_ARMOR = registerFlag("armor");
	public static final String FLAG_TOOL = registerFlag("tool");
	public static final String FLAG_WEAPON = registerFlag("weapon");

	public static final String FLAG_ATTACK = registerFlag("attack");
	public static final String FLAG_DEFENCE = registerFlag("defence");
	public static final String FLAG_MISC = registerFlag("misc");

	public static final String FLAG_MELEE = registerFlag("melee");
	public static final String FLAG_RANGE = registerFlag("range");

	public static final String FLAG_SWORD = registerFlag("sword");
	public static final String FLAG_AXE = registerFlag("axe");

	public static String registerFlag(String flag) {
		return PropertyType.registerFlag("item/" + flag);
	}

	public static final ItemPropertyType ATTRIBUTE_ATTACK_DAMAGE = register("item", "attribute", "attack_damage", 
	new ItemFlagsBuilder().parameter(FLAG_ATTRIBUTE).propertyType(FLAG_ATTACK).weaponRange(FLAG_MELEE).item(FLAG_SWORD).build()
	);
	public static final ItemPropertyType ATTRIBUTE_ATTACK_SPEED = register("item", "attribute", "attack_speed", new ItemFlagsBuilder().parameter(FLAG_ATTRIBUTE).propertyType(FLAG_ATTACK).weaponRange(FLAG_MELEE).item(FLAG_SWORD).build());

	public static final ItemPropertyType ATTRIBUTE_ARMOR = register("item", "attribute", "armor", 								PropertyType.FLAG_ATTRIBUTE, PropertyType.FLAG_ARMOR, 	PropertyType.FLAG_DEFENCE, 	PropertyType.FLAG_SWORD);

	public static final ItemPropertyType ATTRIBUTE_MOVEMENT_SPEED = register("item", "attribute", "movement_speed", 				PropertyType.FLAG_ATTRIBUTE, 							PropertyType.FLAG_MISC, 	PropertyType.FLAG_SWORD);

	public static final ItemPropertyType ATTRIBUTE_MAX_HEALTH = register("item", "attribute", "max_health", 						PropertyType.FLAG_ATTRIBUTE, PropertyType.FLAG_ARMOR, 	PropertyType.FLAG_DEFENCE, 	PropertyType.FLAG_SWORD);

	public static final ItemPropertyType ATTRIBUTE_KNOCKBACK_RESITANCE = register("item", "attribute", "knockback_resitance", 	PropertyType.FLAG_ATTRIBUTE, PropertyType.FLAG_DEFENCE, PropertyType.FLAG_DEFENCE,	PropertyType.FLAG_SWORD);


	public static final ItemPropertyType ENCHANT_UNBREAKING = register("item", "enchant", "unbreaking", 							PropertyType.FLAG_ENCHANT, 								PropertyType.FLAG_MISC, 	PropertyType.FLAG_SWORD);

	public static final ItemPropertyType ENCHANT_MENDING = register("item", "enchant", "mending", 								PropertyType.FLAG_ENCHANT, 								PropertyType.FLAG_MISC, 	PropertyType.FLAG_SWORD);

	public static final ItemPropertyType ENCHANT_FIRE_ASPECT = register("item", "enchant", "fire_aspect", 						PropertyType.FLAG_ENCHANT, 	PropertyType.FLAG_MELEE, 	PropertyType.FLAG_ATTACK, 	PropertyType.FLAG_SWORD);


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
		private String itemType;
		private String propertyType;
		private String weaponRange;
		private String item;

		public ItemFlagsBuilder() {
			super(FLAG_ITEM);
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_ATTRIBUTE
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_ENCHANT
		 */
		public ItemFlagsBuilder parameter(String flag) {
			this.parameter = push(this.parameter, flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_ARMOR
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_TOOL
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_WEAPON
		 */
		public ItemFlagsBuilder itemType(String flag) {
			this.itemType = push(this.itemType, flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_ATTACK
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_DEFENCE
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_MISC
		 */
		public ItemFlagsBuilder propertyType(String flag) {
			this.propertyType = push(this.propertyType, flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_MELEE
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_RANGE
		 */
		public ItemFlagsBuilder weaponRange(String flag) {
			this.weaponRange = push(this.weaponRange, flag);
			return this;
		}

		/**
		 * @see net.home.random_generator.minecraft.ItemPropertyType#FLAG_SWORD
		 */
		public ItemFlagsBuilder item(String flag) {
			this.item = push(this.item, flag);
			return this;
		}
	}

}
