package net.w3e.wlib.dungeon.layers.filter.types;

import net.w3e.wlib.dungeon.layers.LayerRange;

public class DifficultyRoomFilter extends LayerRangeRoomFilter {

	public DifficultyRoomFilter() {
		this(null);
	}

	public DifficultyRoomFilter(LayerRange range) {
		super(JSON_MAP.DIFFICULTY, range);
	}
}
