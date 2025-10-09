package net.w3e.wlib.dungeon.filters;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

import java.util.Random;

public class RoomFilterVariant extends RoomFilterRange {

	private RoomFilterVariant() {
		super(JSON_MAP.VARIANT);
	}

	public RoomFilterVariant(LayerRange layerRange) {
		super(JSON_MAP.VARIANT, layerRange);
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		return this.testValue(room.getData().getVariant());
	}
	
}
