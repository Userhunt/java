package net.w3e.wlib.dungeon.layers.filter;

import java.util.HashMap;
import java.util.List;

import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.RoomLayerJsonAdaptersMap.RoomLayerFilterCache;

public class RoomLayerFilterValues extends HashMap<String, Object> {

	public RoomLayerFilterValues(DungeonRoomInfo room, List<RoomLayerFilterCache> filters) {
		for (RoomLayerFilterCache cache : filters) {
			RoomLayerFilter<?> value = cache.getValue();
			this.put(value.keyName(), value.get(room));
		}
	}
}
