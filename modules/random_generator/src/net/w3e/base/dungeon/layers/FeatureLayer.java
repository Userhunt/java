package net.w3e.base.dungeon.layers;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;

public class FeatureLayer extends DungeonLayer {

	public FeatureLayer(DungeonGenerator generator) {
		super(generator);
	}

	@Override
	public void regenerate() {

	}

	@Override
	public int generate() {
		return 100;
	}

	@Deprecated
	public static final FeatureLayer example(DungeonGenerator generator) {
		return new FeatureLayer(generator);
	}

}
