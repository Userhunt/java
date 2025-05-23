package net.w3e.wlib.dungeon.layers.filter;

import java.util.Random;

import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;
import net.w3e.wlib.dungeon.json.RoomLayerJsonAdaptersMap;
import net.w3e.wlib.json.WJsonRegistryElement;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;

public abstract class RoomLayerFilter<V> extends WJsonRegistryElement {

	protected static final RoomLayerJsonAdaptersMap JSON_MAP = DungeonJsonAdapters.INSTANCE.roomFilterAdapters;

	public RoomLayerFilter(WJsonTypedTypeAdapter<? extends RoomLayerFilter<V>> configType) {
		super(configType);
	}

	public abstract boolean notValid();

	@SuppressWarnings("unchecked")
	public final boolean test(Random random, Object value) {
		return testValue(random, (V)value);
	}
	protected abstract boolean testValue(Random random, V value);

	public abstract V get(DungeonRoomInfo room);

	protected final int getOr(DungeonRoomInfo room, String key) {
		return getOr(room, key, Integer.MIN_VALUE);
	}

	protected final int getOr(DungeonRoomInfo room, String key, int notSet) {
		MapTString data = room.data();
		if (data.containsKey(key)) {
			int value = data.getInt(key);
			if (value != Integer.MIN_VALUE) {
				return value;
			}
		}
		return Integer.MIN_VALUE;
	}
}
