package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class TemperatureLayer extends NoiseLayer {

	public static final String KEY = "temperature";

	public TemperatureLayer(DungeonGenerator generator, int min, int max, double scale) {
		super(generator, KEY, min, max, scale);
	}

	public TemperatureLayer(DungeonGenerator generator, int min, int max, double scale, int def, int stepRate) {
		super(generator, KEY, min, max, scale, def, stepRate);
	}

	public static final TemperatureLayer example(DungeonGenerator generator) {
		return new TemperatureLayer(generator, MIN, MAX, -8d);
	}

	public static final int MIN = -25;
	public static final int MAX = 35;

}
