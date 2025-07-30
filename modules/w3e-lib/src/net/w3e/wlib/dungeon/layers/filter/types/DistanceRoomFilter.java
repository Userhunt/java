package net.w3e.wlib.dungeon.layers.filter.types;

import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.LayerRange;

public class DistanceRoomFilter extends LayerRangeRoomFilter {

	public DistanceRoomFilter() {
		this(null);
	}

	public DistanceRoomFilter(LayerRange range) {
		super(JSON_MAP.DISTANCE, range);
	}

	@Override
	public final Integer get(DungeonRoomInfo room) {
		return room.getDistance() != -1 ? room.getDistance() : Integer.MIN_VALUE;
	}
}
