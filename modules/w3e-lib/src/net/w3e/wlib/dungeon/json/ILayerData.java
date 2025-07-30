package net.w3e.wlib.dungeon.json;

import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.json.WJsonBuilder;

public interface ILayerData<T> extends WJsonBuilder<T> {
	T withDungeon(DungeonGenerator generator);
	default T build() {
		return withDungeon(null);
	}
}
