package net.w3e.wlib.dungeon.filters;

import java.util.Random;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

public class RoomFilterTemp extends RoomFilterRange {

	private RoomFilterTemp() {
		super(JSON_MAP.TEMPERATURE);
	}

	public RoomFilterTemp(LayerRange layerRange) {
		super(JSON_MAP.TEMPERATURE, layerRange);
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		return this.testValue(room.getData().getTemperature());
	}

}
