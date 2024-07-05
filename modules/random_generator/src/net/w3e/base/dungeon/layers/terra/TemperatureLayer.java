package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class TemperatureLayer extends NoiseLayer {

	public static final String KEY = "temperature";

	public TemperatureLayer(DungeonGenerator generator, NoiseData data) {
		super(generator, data.withKey(KEY));
	}

	public static final TemperatureLayer example(DungeonGenerator generator) {
		return new TemperatureLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).generateDefValue().build());
	}

	public static final int MIN = -25;
	public static final int MAX = 35;

}
