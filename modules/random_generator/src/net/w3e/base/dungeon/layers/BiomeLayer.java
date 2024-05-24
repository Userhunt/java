package net.w3e.base.dungeon.layers;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;

public class BiomeLayer<T> extends DungeonLayer<T> {

	public BiomeLayer(DungeonGenerator<T> generator) {
		super(generator);
	}

	@Override
	public void regenerate() {

	}

	@Override
	public int generate() {
		return 100;
	}

	public static final BiomeLayer<String> example(DungeonGenerator<String> generator) {
		return new BiomeLayer<>(generator);
	}
	
}
