package net.w3e.wlib.dungeon.filters;

import java.util.Random;

import net.skds.lib2.io.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;
import net.w3e.wlib.dungeon.json.RoomLayerJsonAdaptersMap;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.json.WJsonRegistryElement;

public abstract class RoomFilter extends WJsonRegistryElement {

	protected static final RoomLayerJsonAdaptersMap JSON_MAP = DungeonJsonAdapters.INSTANCE.roomFilterAdapters;

	public RoomFilter(ConfigType<? extends RoomFilter> configType) {
		super(configType);
	}

	public abstract boolean notValid();

	public abstract boolean testValue(Random random, DungeonRoomInfo room);
}
