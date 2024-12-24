package net.w3e.wlib.dungeon.json;

import net.w3e.wlib.dungeon.DungeonGenerator;

public interface ILayerData<T> {
	T withDungeon(DungeonGenerator generator);
}
