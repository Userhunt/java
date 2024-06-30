package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class WetLayer extends NoiseLayer {

	public static final String KEY = "temperature";

	public WetLayer(DungeonGenerator generator, int min, int max, double scale) {
		super(generator, KEY, min, max, scale);
	}

	public WetLayer(DungeonGenerator generator, int min, int max, double scale, int def, int stepRate) {
		super(generator, KEY, min, max, scale, def, stepRate);
	}

	public static final WetLayer example(DungeonGenerator generator) {
		return new WetLayer(generator, MIN, MAX, -8d);
	}

	public static final int MIN = 0;
	public static final int MAX = 35;

}
