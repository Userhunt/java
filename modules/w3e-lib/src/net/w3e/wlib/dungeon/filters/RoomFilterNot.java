package net.w3e.wlib.dungeon.filters;

import java.util.Random;

import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

public class RoomFilterNot extends RoomFilter {

	public static final String KEY = "not";
	private final RoomFilter filter = RoomFilterEmpty.INSTANCE;

	public RoomFilterNot() {
		super(JSON_MAP.NOT);
	}

	@Override
	public boolean notValid() {
		return this.filter.notValid();
	}

	@Override
	public boolean testValue(Random random, DungeonRoomInfo room) {
		return !this.filter.testValue(random, room);
	}

}
