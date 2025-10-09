package net.w3e.wlib.dungeon.filters;

import java.util.Random;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

public class RoomFilterWet extends RoomFilterRange {

	private RoomFilterWet() {
		super(JSON_MAP.WET);
	}

	public RoomFilterWet(LayerRange layerRange) {
		super(JSON_MAP.WET, layerRange);
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		return this.testValue(room.getData().getWet());
	}

}
