package net.w3e.wlib.dungeon.layers.filter.types;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.terra.DifficultyLayer;

public class DifficultyRoomFilter extends LayerRangeRoomFilter {

	public DifficultyRoomFilter() {
		this(null);
	}
	
	public DifficultyRoomFilter(LayerRange range) {
		super(DifficultyLayer.KEY, range);
	}
}
