package net.w3e.app.utils.cyberpunk;

public class ShardsGenerator {
	public ShardsGenerator(CyberpunkGeneratorScreen generator) {
		ShopPage page = generator.push("Shards");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_2Uncommon)) {
			page.addUnique("Items.CWCapacityPermaReward_" + rarity, 20 * rarity.lvl, rarity);
		}
		for (String name : new String[]{"Body", "Cool", "Intelligence", "Reflexes", "TechnicalAbility"}) {
			page.addUnique(String.format("Items.%sSkill_Skillbook", name), 15, Rarity.Rare);
			page.addUnique(String.format("Items.%sSkillbook_Strong", name), 30, Rarity.Epic);
			page.addUnique(String.format("Items.%sSkill_Skillbook_Legendary", name), 45, Rarity.Legendary);
		}
		page.addUnique("Items.TechnicalSkillbook_Strong", 30, Rarity.Epic);
		page.addUnique("Items.ReflexSkillbook_Strong", 30, Rarity.Epic);
		page.remove("Items.TechnicalAbilitySkillbook_Strong");
		page.remove("Items.ReflexesSkillbook_Strong");
	}
}
