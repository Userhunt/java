package net.w3e.wlib.dungeon.filters;

import java.util.Random;

import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

public class RoomFilterRandom extends RoomFilterRange {

	public static final String KEY = "random";

	private RoomFilterRandom() {
		super(JSON_MAP.RANDOM);
	}

	public RoomFilterRandom(LayerRange layerRange) {
		super(JSON_MAP.RANDOM, layerRange);
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		return this.value.test(random.nextInt(100) + 1);
	}

}
