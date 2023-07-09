package net.home.random_generator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.home.random_generator.minecraft.ItemPropertyType;
import net.w3e.base.generator.GenRange;
import net.w3e.base.generator.GenRange.ValueRangeBuilder;
import net.w3e.base.generator.PropertySelector;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.generator.property.GenNormalProperty.GenNormalBuilder;
import net.w3e.base.generator.property.GenRequiredProperty.GenRequiredBuilder;

public abstract class ItemRegistry extends GenRegistry {

	public static ItemPropertyType register(String type, String subType, String id, String... flags) {
		return PropertyType.register(new ItemPropertyType(type, subType, id, flags));
	}

	public static ItemPropertyType register(String type, String subType, String id, Map<String, List<String>> flags) {
		return PropertyType.register(new ItemPropertyType(type, subType, id, flags));
	}

	public static class SwordRegistry extends ItemRegistry {
		@Override
		public final void refill() {
			list.clear();

			//attributes
			list.add(new GenRequiredBuilder(ItemPropertyType.ATTRIBUTE_ATTACK_DAMAGE)	.valueRange(new ValueRangeBuilder(4, 8, 0.5, 0.2).spreadLvl(0.05).build()).build());
			list.add(new GenRequiredBuilder(ItemPropertyType.ATTRIBUTE_ATTACK_SPEED)		.valueRange(new ValueRangeBuilder(1, 2, 0.2, 0.1).spreadLvl(0.02).build()).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_ARMOR)				.valueRange(new ValueRangeBuilder(-5, 5, 1, 0).round(1).build()).weight(GenRange.weight(1, 5, 0.5)).chance(GenRange.chance(10, 20, 1)).levelRange(GenRange.levelRange(1)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_MOVEMENT_SPEED)		.valueRange(new ValueRangeBuilder(-20, 10, 3.5, 10).spreadLvl(-1.1).build()).weight(GenRange.weight(1)).chance(GenRange.chance(15, 5, 1)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_MAX_HEALTH)			.valueRange(new ValueRangeBuilder(-5, 10, 1, 1).spreadLvl(0.1).round(1).build()).weight(GenRange.weight(4)).chance(GenRange.chance(0, 35, 7)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_KNOCKBACK_RESITANCE).valueRange(new ValueRangeBuilder(-0.5, 1, 0.4, 0.35).spreadLvl(-0.03).build()).weight(GenRange.weight(2)).chance(GenRange.chance(0, 15, 3)).levelRange(GenRange.levelRange(3)).nerfLvl(false).clampRandom(true).build());
			//enchant
			list.add(new GenNormalBuilder(ItemPropertyType.ENCHANT_UNBREAKING)	.valueRange(new ValueRangeBuilder(1, 5, 0, 2.5).baseSpread(2).round(0).build()).weight(GenRange.weight(2)).chance(GenRange.chance(0, 25)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ENCHANT_MENDING)		.valueRange(new ValueRangeBuilder(1).round(0).build()).weight(GenRange.weight(2)).chance(GenRange.chance(0, 25)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ENCHANT_FIRE_ASPECT)	.valueRange(new ValueRangeBuilder(1, 3, 0, 1.5).baseSpread(1).round(0).build()).weight(GenRange.weight(2)).chance(GenRange.chance(0, 25)).build());
		}
		@Override
		public PropertySelector selector() {
			return new PropertySelector(e -> Objects.equals(e, "item"), null, null, e -> false);
		}
	}

	public static class AxeRegistry extends ItemRegistry {
		@Override
		public final void refill() {
			list.clear();

			//attributes
			list.add(new GenRequiredBuilder(ItemPropertyType.ATTRIBUTE_ATTACK_DAMAGE)	.valueRange(new ValueRangeBuilder(5, 9, 0.5, 0.2).spreadLvl(0.05).build()).build());
			list.add(new GenRequiredBuilder(ItemPropertyType.ATTRIBUTE_ATTACK_SPEED)		.valueRange(new ValueRangeBuilder(0.7, 1.5, 0.2, 0.1).spreadLvl(0.02).build()).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_ARMOR)				.valueRange(new ValueRangeBuilder(-7, 3, 1, 0).round(1).build()).weight(GenRange.weight(1, 5, 0.5)).chance(GenRange.chance(10, 20, 1)).levelRange(GenRange.levelRange(1)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_MOVEMENT_SPEED)		.valueRange(new ValueRangeBuilder(-10, 15, 3.5, 10).spreadLvl(-1.1).build()).weight(GenRange.weight(1)).chance(GenRange.chance(15, 5, 1)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_MAX_HEALTH)			.valueRange(new ValueRangeBuilder(-5, 10, 1, 1).spreadLvl(0.1).round(1).build()).weight(GenRange.weight(4)).chance(GenRange.chance(0, 35, 7)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ATTRIBUTE_KNOCKBACK_RESITANCE).valueRange(new ValueRangeBuilder(-0.3, 1, 0.3, 0.35).spreadLvl(-0.03).build()).weight(GenRange.weight(2)).chance(GenRange.chance(0, 15, 3)).levelRange(GenRange.levelRange(3)).nerfLvl(false).clampRandom(true).build());
			//enchant
			list.add(new GenNormalBuilder(ItemPropertyType.ENCHANT_UNBREAKING)	.valueRange(new ValueRangeBuilder(1, 5, 0, 2.5).baseSpread(2).round(0).build()).weight(GenRange.weight(1)).chance(GenRange.chance(0, 35)).build());
			list.add(new GenNormalBuilder(ItemPropertyType.ENCHANT_MENDING)		.valueRange(new ValueRangeBuilder(1).round(0).build()).weight(GenRange.weight(1)).chance(GenRange.chance(0, 35)).build());
			//ENCHANT_FIRE_ASPECT
		}
		@Override
		public PropertySelector selector() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Unimplemented method 'selector'");
		}
	}
}
