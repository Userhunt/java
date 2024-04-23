package net.home.cyberpunk;

public class CyberwareGenerator {
	private final CyberpunkGenerator generator;

	public CyberwareGenerator(CyberpunkGenerator generator) {
		this.generator = generator;
		this.generateArms();
		this.generateOperatingSystem();
		this.generateCirculatory();
		this.generateFace();
		this.generateFrontal();
		this.generateHands();
		this.generateIntegumentary();
		this.generateLegs();
		this.generateNervous();
		this.generateSkeleton();
	}

	private final void generateArms() {
		ShopPage page = this.generator.push("Cyberware Arms").image("base/gameplay/gui/common/icons/items/item_icons9.inkatlas", "cw_arms_strongarms");
		generateArms(page, "");
		generateArms(page, "Chemical");
		generateArms(page, "Electric");
		generateArms(page, "Thermal");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_4Epic)) {
			page.addUnique("Items.AdvancedMaxTacMantisBlades" + rarity, rarity);
		}
	}

	private final void generateArms(ShopPage page, String string) {
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_2Uncommon)) {
			String name = String.format("%s%s", string, rarity);
			page.addUnique(String.format("Items.AdvancedStrongArms%s", name), rarity);
			page.addUnique(String.format("Items.AdvancedMantisBlades%s", name), rarity);
			page.addUnique(String.format("Items.AdvancedNanoWires%s", name), rarity);
			page.addUnique(String.format("Items.AdvancedProjectileLauncher%s", name), rarity);
		}
	}

	private final void generateOperatingSystem() {
		ShopPage page = this.generator.push("Cyberware Operating System").image("base/gameplay/gui/common/icons/items/item_icons5.inkatlas", "cw_system_biotechcyberdeck");
		// berserk
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_2Uncommon)) {
			page.addUnique("Items.AdvancedBerserkC1MK" + (rarity.lvl - 1), rarity);
			page.addUnique("Items.AdvancedBerserkC2MK" + (rarity.lvl - 1), rarity);
			if (Rarity.HAVE_3Rare.test(rarity)) {
				page.addUnique("Items.AdvancedBerserkC3MK" + rarity.lvl, rarity);
				if (Rarity.HAVE_4Epic.test(rarity)) {
					page.addUnique("Items.AdvancedBerserkC4MK" + rarity.lvl, rarity);
				}
			}
		}
		// cyberdeck
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			String I = this.generator.convertNumber(rarity.lvl) + rarity;
			page.addUnique("Items.AdvancedArasakaShadowMK" + I, rarity);
			page.addUnique("Items.AdvancedMilitechParalineMK" + I, rarity);
			page.addUnique("Items.AdvancedTetratronicRipplerMK" + I, rarity);
			if (Rarity.HAVE_2Uncommon.test(rarity)) {
				page.addUnique("Items.AdvancedBiotechSigmaMK" + this.generator.convertNumber(rarity.lvl - 1) + rarity, rarity);
				if (Rarity.HAVE_3Rare.test(rarity)) {
					page.addUnique("Items.AdvancedRavenMicrocyberMK" + this.generator.convertNumber(rarity.lvl - 2) + rarity, rarity);
					page.addUnique("Items.HauntedCyberdeck_" +  rarity, rarity);
					System.out.println("Items.HauntedCyberdeck_" + rarity);
				}
			}
		}
		page.addUnique("Items.AdvancedNetwatchNetdriverMKLegendary", Rarity.Legendary);
		// sandevistan
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_2Uncommon)) {
			page.addUnique("Items.AdvancedSandevistanC1MK" + (rarity.lvl - 1), rarity);
			page.addUnique("Items.AdvancedSandevistanC2MK" + rarity.lvl, rarity);
			if (Rarity.HAVE_3Rare.test(rarity)) {
				page.addUnique("Items.AdvancedSandevistanC3MK" + rarity.lvl, rarity);
				if (Rarity.HAVE_4Epic.test(rarity)) {
					page.addUnique("Items.AdvancedSandevistanC4MK" + rarity.lvl, rarity);
				}
			}
		}
		page.addUnique("Items.AdvancedSandevistanApogee", Rarity.Legendary);
		// Capacity
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_2Uncommon)) {
			page.addUnique("Items.CapacityBooster" + rarity, rarity);
		}
	}

	private final void generateCirculatory() {
		ShopPage page = this.generator.push("Cyberware Circulatory").image("base/gameplay/gui/common/icons/items/item_icons8.inkatlas", "cw_circulatory_staminaregenbooster");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			page.addUnique("Items.AdvancedStaminaRegenBooster" + rarity, rarity);
			page.addUnique("Items.AdvancedBiomonitor" + rarity, rarity);
			page.addUnique("Items.AdvancedShockAbsor" + rarity, rarity);
			page.addUnique("Items.AdvancedHealOnKill" + rarity, rarity);
			page.addUnique("Items.AdvancedCyberRotors" + rarity, rarity);
			page.addUnique("Items.AdvancedCatchMeIfYouCan" + rarity, rarity);
			if (Rarity.HAVE_2Uncommon.test(rarity)) {
				page.addUnique("Items.AdvancedBloodPump" + rarity, rarity);
				if (Rarity.HAVE_3Rare.test(rarity)) {
					page.addUnique("Items.AdvancedViralVenom" + rarity, rarity);
					page.addUnique("Items.AdvancedDischargeConnector" + rarity, rarity);
					page.addUnique("Items.IconicDischargeConnector" + rarity, rarity);
					page.addUnique("Items.IconicShockAbsorber" + rarity, rarity);
					if (Rarity.HAVE_4Epic.test(rarity)) {
						page.addUnique("Items.AdvancedSecondHeart" + rarity, rarity);
					}
				}
			}
		}
	}

	private final void generateFace() {
		ShopPage page = this.generator.push("Cyberware Face").image("base/gameplay/gui/common/icons/items/item_icons5.inkatlas", "cw_eyes_4eye");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			page.addUnique("Items.AdvancedKiroshiOpticsBare" + rarity, rarity);
			page.addUnique("Items.AdvancedKiroshiOpticsWallhack" + rarity, rarity);
			page.addUnique("Items.AdvancedKiroshiOpticsHunter" + rarity, rarity);
			page.addUnique("Items.AdvancedKiroshiOpticsSensor" + rarity, rarity);
			page.addUnique("Items.AdvancedKiroshiOpticsPiercing" + rarity, rarity);
			page.addUnique("Items.AdvancedKiroshiOpticsCombined" + rarity, rarity);
			if (Rarity.HAVE_4Epic.test(rarity)) {
				page.addUnique("Items.Iconic_AdvancedKiroshiOpticsBare" + rarity, rarity);
			}
		}
		page.addUnique("Items.MaskCW", Rarity.Legendary);
		page.addUnique("Items.AdvancedKiroshiOpticsSensor_Uncommon", Rarity.Uncommon);
		page.remove("Items.AdvancedKiroshiOpticsSensorUncommon");
	}

	private final void generateFrontal() {
		ShopPage page = this.generator.push("Cyberware Frontal").image("base/gameplay/gui/common/icons/items/item_icons8.inkatlas", "cw_frontalcortex_memoryboost");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			page.addUnique("Items.AdvancedBioConductors" + rarity, rarity);
			page.addUnique("Items.AdvancedMechatronicCore" + rarity, rarity);
			page.addUnique("Items.AdvancedSubdermalCoProcessor" + rarity, rarity);
			page.addUnique("Items.AdvancedRamUpgrade" + rarity, rarity);
			if (Rarity.HAVE_2Uncommon.test(rarity)) {
				page.addUnique("Items.AdvancedMemoryBoost" + rarity, rarity);
				if (Rarity.HAVE_3Rare.test(rarity)) {
					page.addUnique("Items.AdvancedExDisk" + rarity, rarity);
					page.addUnique("Items.AdvancedKerenziovBoostSystem" + rarity, rarity);
					page.addUnique("Items.AdvancedSelfIce" + rarity, rarity);
					page.addUnique("Items.IconicBioConductors" + rarity, rarity);
					if (Rarity.HAVE_4Epic.test(rarity)) {
						page.addUnique("Items.AdvancedCamilloRamManager" + rarity, rarity);
						page.addUnique("Items.IconicAdvancedSubdermalCoProcessor" + rarity, rarity);
						page.addUnique("Items.IconicCamilloRamManager" + rarity, rarity);
					}
				}
			}
		}
		page.addUnique("Items.AdvancedTimeBankLegendary", Rarity.Legendary);
	}

	private final void generateHands() {
		ShopPage page = this.generator.push("Cyberware Hands").image("base/gameplay/gui/common/icons/items/item_icons8.inkatlas", "cw_hands_powergrip");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			page.addUnique("Items.AdvancedPowerGrip" + rarity, rarity);
			page.addUnique("Items.AdvancedJointLock" + rarity, rarity);
			page.addUnique("Items.AdvancedSmartLink" + rarity, rarity);
			if (Rarity.HAVE_2Uncommon.test(rarity)) {
				page.addUnique("Items.AdvancedMicroGenerator" + rarity, rarity);
				if (Rarity.HAVE_3Rare.test(rarity)) {
					page.addUnique("Items.AdvancedKnifeSharpener" + rarity, rarity);
					if (Rarity.HAVE_4Epic.test(rarity)) {
						page.addUnique("Items.IconicGunStabilizer" + rarity, rarity);
					}
				}
			}
		}
	}

	private final void generateIntegumentary() {
		ShopPage page = this.generator.push("Cyberware Integumentary").image("base/gameplay/gui/common/icons/items/item_icons5.inkatlas", "cw_integumentary_groundingplating");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			page.addUnique("Items.AdvancedSuddenAid" + rarity, rarity);
			page.addUnique("Items.AdvancedProximityReducer" + rarity, rarity);
			page.addUnique("Items.AdvancedChargeSystem" + rarity, rarity);
			page.addUnique("Items.AdvancedElectroshockMechanism" + rarity, rarity);
			page.addUnique("Items.AdvancedBoringPlating" + rarity, rarity);
			page.addUnique("Items.AdvancedCogitoFrame" + rarity, rarity);
			if (Rarity.HAVE_2Uncommon.test(rarity)) {
				page.addUnique("Items.AdvancedWeirdTankyPlating" + rarity, rarity);
				page.addUnique("Items.AdvancedAdaptiveStemCells" + rarity, rarity);
				page.addUnique("Items.AdvancedNanoTechPlates" + rarity, rarity);
				page.addUnique("Items.AdvancedOpticalCamo" + rarity, rarity);
				if (Rarity.HAVE_3Rare.test(rarity)) {
					page.addUnique("Items.AdvancedPlatingGlitch" + rarity, rarity);
					page.addUnique("Items.IconicAdvancedChiton" + rarity, rarity);
					if (Rarity.HAVE_4Epic.test(rarity)) {
						page.addUnique("Items.AdvancedPainReductor" + rarity, rarity);
						page.addUnique("Items.AdvancedBloodDepleter" + rarity, rarity);
						page.addUnique("Items.IconicAdvancedProximityReducer" + rarity, rarity);
					}
				}
			}
		}

	}

	private final void generateLegs() {
		ShopPage page = this.generator.push("Cyberware Legs").image("base/gameplay/gui/common/icons/items/item_icons6.inkatlas", "cw_legs_speed_iconic");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_2Uncommon)) {
			page.addUnique("Items.AdvancedReinforcedMuscles" + rarity, rarity);
			page.addUnique("Items.AdvancedJenkinsTendons" + rarity, rarity);
			page.addUnique("Items.AdvancedCatPaws" + rarity, rarity);
			page.addUnique("Items.AdvancedBoostedTendons" + rarity, rarity);
			if (Rarity.HAVE_4Epic.test(rarity)) {
				page.addUnique("Items.IconicJenkinsTendons" + rarity, rarity);
			}
		}
		page.addUnique("Items.AdvancedJenkinsTendonsCommon", Rarity.Common);
	}

	private final void generateNervous() {
		ShopPage page = this.generator.push("Cyberware Nervous").image("base/gameplay/gui/common/icons/items/item_icons8.inkatlas", "cw_nervoussystem_reflexrecorder");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			page.addUnique("Items.AdvancedDetectorRush" + rarity, rarity);
			page.addUnique("Items.AdvancedTroubleFinder" + rarity, rarity);
			page.addUnique("Items.AdvancedReflexRecorder" + rarity, rarity);
			page.addUnique("Items.AdvancedTyrosineInjector" + rarity, rarity);
			page.addUnique("Items.AdvancedVisualCortexSupport" + rarity, rarity);
			if (Rarity.HAVE_2Uncommon.test(rarity)) {
				page.addUnique("Items.AdvancedKerenzikov" + rarity, rarity);
				page.addUnique("Items.AdvancedOilDispenser" + rarity, rarity);
				page.addUnique("Items.AdvancedSynapticAccelerator" + rarity, rarity);
				if (Rarity.HAVE_3Rare.test(rarity)) {
					page.addUnique("Items.AdvancedNeoFiber" + rarity, rarity);
					page.addUnique("Items.IconicAdvancedDetectorRush" + rarity, rarity);
					page.addUnique("Items.IconicAdvancedReflexRecorder" + rarity, rarity);
					page.addUnique("Items.IconicAdvancedVisualCortexSupport" + rarity, rarity);
				}
			}
		}
	}

	private final void generateSkeleton() {
		ShopPage page = this.generator.push("Cyberware Skeleton").image("base/gameplay/gui/common/icons/items/item_icons5.inkatlas", "cw_skeleton_endoskeleton");
		for (Rarity rarity : Rarity.iterableValues(Rarity.HAVE_1Common)) {
			page.addUnique("Items.AdvancedBionicJoints" + rarity, rarity);
			page.addUnique("Items.AdvancedBoneMarrowCells" + rarity, rarity);
			page.addUnique("Items.AdvancedCompilingSkeleton" + rarity, rarity);
			page.addUnique("Items.AdvancedNoPainNoGain" + rarity, rarity);
			page.addUnique("Items.AdvancedRapidMuscleNurish" + rarity, rarity);
			page.addUnique("Items.AdvancedTitaniumInfusedBones" + rarity, rarity);
			page.addUnique("Items.AdvancedNeuroMatrix" + rarity, rarity);
			if (Rarity.HAVE_2Uncommon.test(rarity)) {
				page.addUnique("Items.AdvancedDenseMarrow" + rarity, rarity);
				page.addUnique("Items.AdvancedAgileJoints" + rarity, rarity);
				if (Rarity.HAVE_3Rare.test(rarity)) {
					page.addUnique("Items.AdvancedT1000" + rarity, rarity);
					page.addUnique("Items.AdvancedPainDistributor" + rarity, rarity);
					page.addUnique("Items.IconicAdvancedT1000" + rarity, rarity);
					if (Rarity.HAVE_4Epic.test(rarity)) {
						page.addUnique("Items.AdvancedEndoskeleton" + rarity, rarity);
					}
				}
			}
		}
	}
}
