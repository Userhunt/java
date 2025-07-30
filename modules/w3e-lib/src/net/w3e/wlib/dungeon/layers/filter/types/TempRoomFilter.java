package net.w3e.wlib.dungeon.layers.filter.types;

import net.w3e.wlib.dungeon.layers.LayerRange;

public class TempRoomFilter extends LayerRangeRoomFilter {

	public TempRoomFilter() {
		this(null);
	}

	public TempRoomFilter(LayerRange range) {
		super(JSON_MAP.TEMPERATURE, range);
	}
}
