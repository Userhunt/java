package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class WetLayer extends NoiseLayer {

	public static final String KEY = "wet";

	public WetLayer(DungeonGenerator generator, NoiseData data) {
		super(generator, data.withKey(KEY));
	}

	public static final WetLayer example(DungeonGenerator generator) {
		return new WetLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build());
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

}
