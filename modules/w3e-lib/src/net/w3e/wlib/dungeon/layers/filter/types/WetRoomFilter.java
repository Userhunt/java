package net.w3e.wlib.dungeon.layers.filter.types;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.terra.WetLayer;

public class WetRoomFilter extends LayerRangeRoomFilter {

	public WetRoomFilter() {
		this(null);
	}
	
	public WetRoomFilter(LayerRange range) {
		super(WetLayer.KEY, range);
	}

	@Override
	public final boolean notValid() {
		return this.value.notValid(0, 100);
	}
}
