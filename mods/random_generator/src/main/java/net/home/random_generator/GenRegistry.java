package net.home.random_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.home.random_generator.property.GenNormalProperty;
import net.home.random_generator.property.GenRequiredProperty;
import net.w3e.base.api.registry.Registry;
import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.IProperty;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.generator.RandomGenerator;
import net.w3e.base.generator.RandomGenerator.RandomGeneratorPrint;

public class GenRegistry {

	public static final Registry<PropertyType> REGISTRY = new Registry<>("property", null);

	public static final PropertyType ATTRIBUTE_ATTACK_DAMAGE = new PropertyType("attribute/damage");
	public static final PropertyType ATTRIBUTE_ATTACK_SPEED = new PropertyType("attribute/speed");
	public static final PropertyType ATTRIBUTE_ARMOR = new PropertyType("attribute/armor");
	public static final PropertyType ATTRIBUTE_MOVEMENT_SPEED = new PropertyType("attribute/movement_speed");
	public static final PropertyType ATTRIBUTE_MAX_HEALTH = new PropertyType("attribute/max_health");
	
	public static void register() {}

	public static final Random RANDOM = new Random();
	public static final List<IProperty> SWORD_LIST = new ArrayList<>();
	public static final RandomGenerator GENERATOR = new RandomGenerator(RandomGeneratorPrint.ALL, RANDOM::nextDouble, RANDOM::nextInt, true);

	static {
		IProperty attack_damage = new GenRequiredProperty(GenRegistry.ATTRIBUTE_ATTACK_DAMAGE, GenRange.LEVEL_RANGE, GenRange.EMPTY, GenRange.valueRange(4, 8, 0.5, 0.2, 0.05, 2));
		IProperty attack_speed = new GenRequiredProperty(GenRegistry.ATTRIBUTE_ATTACK_SPEED, GenRange.LEVEL_RANGE, GenRange.EMPTY, GenRange.valueRange(1, 2, 0.2, 0.1, 0.02, 2));
		IProperty armor = new GenNormalProperty(GenRegistry.ATTRIBUTE_ARMOR, GenRange.levelRange(1), GenRange.weight(1, 5, 0.5), GenRange.valueRange(-5, 5, 1, 0, 0, 1), GenRange.chance(10, 20, 1));
		IProperty movement_speed = new GenNormalProperty(GenRegistry.ATTRIBUTE_MOVEMENT_SPEED, GenRange.LEVEL_RANGE, GenRange.weight(1), GenRange.valueRange(-35, 10, 3, 20, -1, 2), GenRange.chance(20, 5, 1));
		IProperty max_health = new GenNormalProperty(GenRegistry.ATTRIBUTE_MAX_HEALTH, GenRange.LEVEL_RANGE, GenRange.weight(4), GenRange.valueRange(-5, 10, 1, 1, 0.1, 1), GenRange.chance(0, 35, 7));

		SWORD_LIST.add(attack_damage);
		SWORD_LIST.add(attack_speed);
		SWORD_LIST.add(armor);
		SWORD_LIST.add(movement_speed);
		SWORD_LIST.add(max_health);
	}
}
