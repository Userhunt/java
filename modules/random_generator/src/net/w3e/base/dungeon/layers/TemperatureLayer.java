package net.w3e.base.dungeon.layers;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;

public class TemperatureLayer<T> extends DungeonLayer<T> {

	public TemperatureLayer(DungeonGenerator<T> generator, int min, int max, int count) {
		super(generator);
	}

	@Override
	public void regenerate() {

	}

	@Override
	public int generate() {
		return 100;
	}

	public static final TemperatureLayer<String> example(DungeonGenerator<String> generator) {
		return new TemperatureLayer<>(generator, 0, 0, 0);
	}
	
}
