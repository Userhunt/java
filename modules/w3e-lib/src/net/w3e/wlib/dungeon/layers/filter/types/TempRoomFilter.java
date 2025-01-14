package net.w3e.wlib.dungeon.layers.filter.types;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.terra.TemperatureLayer;

public class TempRoomFilter extends LayerRangeRoomFilter {

	public TempRoomFilter() {
		this(null);
	}
	
	public TempRoomFilter(LayerRange range) {
		super(TemperatureLayer.KEY, range);
	}
}
