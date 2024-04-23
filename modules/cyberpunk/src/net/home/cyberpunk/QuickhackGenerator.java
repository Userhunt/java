package net.home.cyberpunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class QuickhackGenerator {

	private final List<QuickHackInfo> list = Arrays.asList(
		// Combat
			new QuickHackInfo("Overheat", Rarity.HAVE_2Uncommon),
			new QuickHackInfo("EMPOverload", Rarity.HAVE_1Common),
			new QuickHackInfo("Contagion", Rarity.HAVE_3Rare),
			new QuickHackInfo("BrainMelt", Rarity.HAVE_3Rare),
		// Control
			new QuickHackInfo("Blind", Rarity.HAVE_2Uncommon),
			new QuickHackInfo("DisableCyberware", Rarity.HAVE_3Rare),
			new QuickHackInfo("LocomotionMalfunction", Rarity.HAVE_3Rare),
			new QuickHackInfo("WeaponMalfunction", Rarity.HAVE_3Rare)
		// Covert
			//https://cyberpunk.fandom.com/wiki/Cyberpunk_2077_Quickhacks#Covert
		// Ultimate
			//https://cyberpunk.fandom.com/wiki/Cyberpunk_2077_Quickhacks#Ultimate
			
	);

	private ShopPage page;

	@Deprecated
	public QuickhackGenerator(CyberpunkGenerator generator) {
		this.page = generator.push("QuickHacks").image("base/gameplay/gui/common/icons/items/item_icons6.inkatlas", "quickhack_callout");
		for(QuickHackInfo info : list) {
			info.generate(this);
		}
		this.add("Items.Recipe_OverheatProgram", Rarity.Common);
		this.add("Items.OverheatProgram", Rarity.Common);

		this.add("Items.EMPOverloadProgram", Rarity.Common);
		this.add("Items.Recipe_EMPOverloadProgram", Rarity.Common);

		this.add("Items.Recipe_ContagionProgram", Rarity.Uncommon);

		this.add("Items.Recipe_BlindProgram", Rarity.Common);
		this.add("Items.BlindProgram", Rarity.Common);

		this.add("Items.Recipe_DisableCyberwareProgram", Rarity.Uncommon);

		this.add("Items.Recipe_LocomotionMalfunctionProgram", Rarity.Uncommon);

		this.add("Items.Recipe_WeaponMalfunctionProgram", Rarity.Uncommon);

		/*
		"Items.Recipe_WhistleLvl0Program", 1)
		"Items.Recipe_WhistleLvl1Program", 1)
		"Items.Recipe_WhistleLvl4Program", 1)
		"Items.Recipe_WhistleLvl4PlusPlusProgram", 1)
		"Items.Recipe_MadnessLvl3Program", 1)
		"Items.Recipe_MadnessLvl4Program", 1)
		"Items.Recipe_MadnessLvl4PlusPlusProgram", 1)
		"Items.Recipe_GrenadeExplodeLvl3Program", 1)
		"Items.Recipe_GrenadeExplodeLvl4Program", 1)
		"Items.Recipe_GrenadeExplodeLvl4PlusPlusProgram", 1)
		"Items.Recipe_MemoryWipeLvl2Program", 1)
		"Items.Recipe_MemoryWipeLvl3Program", 1)
		"Items.Recipe_MemoryWipeLvl4Program", 1)
		"Items.Recipe_MemoryWipeLvl4PlusPlusProgram", 1)
		"Items.Recipe_PingProgram", 1)
		"Items.Recipe_PingLvl2Program", 1)
		"Items.Recipe_PingLvl3Program", 1)
		"Items.Recipe_PingLvl4Program", 1)
		"Items.Recipe_PingLvl4PlusPlusProgram", 1)
		"Items.Recipe_CommsCallInProgram", 1)
		"Items.Recipe_CommsCallInLvl1Program", 1)
		"Items.Recipe_CommsCallInLvl2Program", 1)
		"Items.Recipe_CommsCallInLvl3Program", 1)
		"Items.Recipe_CommsCallInLvl4Program", 1)
		"Items.Recipe_CommsCallInLvl4PlusPlusProgram", 1)
		"Items.Recipe_CommsNoiseProgram", 1)
		"Items.Recipe_CommsNoiseLvl2Program", 1)
		"Items.Recipe_CommsNoiseLvl3Program", 1)
		"Items.Recipe_CommsNoiseLvl4Program", 1)
		"Items.Recipe_CommsNoiseLvl4PlusPlusProgram", 1)
		"Items.Recipe_SuicideLvl3Program", 1)
		"Items.Recipe_SuicideLvl4Program", 1)
		"Items.Recipe_SuicideLvl4PlusPlusProgram", 1)
		"Items.Recipe_SystemCollapseLvl4PlusPlusProgram", 1)
		"Items.Recipe_SystemCollapseLvl3Program", 1)
		"Items.Recipe_SystemCollapseLvl4Program", 1)
		"Items.Recipe_WhistleProgram", 1)
		"Items.Recipe_WhistleLvl2Program", 1)
		"Items.Recipe_WhistleLvl3Program", 1)

		Game.AddToInventory("Items.WhistleLvl0Program", 1)
		Game.AddToInventory("Items.PingProgram", 1)
		 */
	}

	private final void add(String item, int price, Rarity rarity) {
		this.page.addUnique(item, price, rarity);
	}

	private final void add(String item, Rarity rarity) {
		this.page.addUnique(item, 5 * rarity.lvl, rarity);
	}

	private final record QuickHackInfo(String name, String base, String iconic, Predicate<Rarity> filter) {
		private QuickHackInfo(String name) {
			this(name, Rarity.HAVE_1Common);
		}
		private QuickHackInfo(String name, Predicate<Rarity> filter) {
			this(name, "%sLvl%sProgram", "%sLvl4PlusPlusProgram", filter);
		}

		private final void generate(QuickhackGenerator generator) {
			for (Rarity rarity : Rarity.iterableValues(filter)) {
				generator.add(String.format("Items.Recipe_" + base, name, rarity.lvl - 1), rarity);
			}
			generator.add(String.format("Items.Recipe_" + this.iconic, name), 50, Rarity.Legendary);
		}
	}
}
