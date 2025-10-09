package net.w3e.wlib.dungeon.filters;

import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

import java.util.Random;

public class RoomFilterEmpty extends RoomFilter {

	public static final RoomFilterEmpty INSTANCE = new RoomFilterEmpty();

	public RoomFilterEmpty() {
		super(JSON_MAP.EMPTY);
	}

	@Override
	public boolean notValid() {
		return true;
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		return true;
	}

}
