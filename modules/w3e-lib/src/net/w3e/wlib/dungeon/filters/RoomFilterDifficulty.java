package net.w3e.wlib.dungeon.filters;

import java.util.Random;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

public class RoomFilterDifficulty extends RoomFilterRange {

	private RoomFilterDifficulty() {
		super(JSON_MAP.DIFFICULTY);
	}

	public RoomFilterDifficulty(LayerRange layerRange) {
		super(JSON_MAP.DIFFICULTY, layerRange);
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		return this.testValue(room.getData().getDifficulty());
	}
	
}
