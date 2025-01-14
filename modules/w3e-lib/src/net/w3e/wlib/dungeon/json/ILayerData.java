package net.w3e.wlib.dungeon.json;

import net.skds.lib2.io.json.codec.JsonDeserializeBuilder;
import net.w3e.wlib.dungeon.DungeonGenerator;

public interface ILayerData<T> extends JsonDeserializeBuilder<T>, IDungeonJsonAdapter {
	T withDungeon(DungeonGenerator generator);
	default T build() {
		return withDungeon(null);
	}
}
