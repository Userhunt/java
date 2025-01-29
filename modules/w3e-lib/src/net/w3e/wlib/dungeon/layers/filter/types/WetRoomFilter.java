package net.w3e.wlib.dungeon.layers.filter.types;

import net.w3e.wlib.dungeon.layers.LayerRange;

public class WetRoomFilter extends LayerRangeRoomFilter {

	public WetRoomFilter() {
		this(null);
	}
	
	public WetRoomFilter(LayerRange range) {
		super(JSON_MAP.WET, range);
	}

	@Override
	public final boolean notValid() {
		return this.value.notValid(0, 100);
	}
}
