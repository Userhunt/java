package net.w3e.wlib.dungeon.json;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapter.DungeonJsonAdaptersMap;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilter;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilterValues;

public class RoomLayerJsonAdaptersMap extends DungeonJsonAdaptersMap {

	private final List<RoomLayerFilterCache> filters = new ArrayList<>();
	
	public RoomLayerJsonAdaptersMap() {
		super(RoomLayerFilter.class);
	}

	@Override
	protected void registerAdapter(DungeonJsonAdapter<?> configType) {
		this.filters.add(new RoomLayerFilterCache(configType));
	}

	public final RoomLayerFilterValues createFilters(DungeonRoomInfo room) {
		return new RoomLayerFilterValues(room, this.filters);
	}

	public static class RoomLayerFilterCache {

		private final DungeonJsonAdapter<?> configType;
		private RoomLayerFilter<?> value;

		public RoomLayerFilterCache(DungeonJsonAdapter<?> configType) {
			this.configType = configType;
		}

		public RoomLayerFilter<?> getValue() {
			if (this.value == null) {
				try {
					this.value = (RoomLayerFilter<?>)this.configType.getTypeClass().getDeclaredConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			return this.value;
		}
	}
}
