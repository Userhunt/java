package net.w3e.wlib.dungeon.filters;

import java.util.Random;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

public class RoomFilterDistance extends RoomFilterRange {

	private RoomFilterDistance() {
		super(JSON_MAP.DISTANCE);
	}

	public RoomFilterDistance(LayerRange layerRange) {
		super(JSON_MAP.DISTANCE, layerRange);
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		int distance = room.getData().getDistance();
		if (distance == -1) {
			return true;
		}
		return this.testValue(distance);
	}
	
}
