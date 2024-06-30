package net.w3e.base.dungeon.layers;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;

public class RoomLayer extends DungeonLayer {

	public RoomLayer(DungeonGenerator generator) {
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
	public static final RoomLayer example(DungeonGenerator generator) {
		return new RoomLayer(generator);
	}

}
