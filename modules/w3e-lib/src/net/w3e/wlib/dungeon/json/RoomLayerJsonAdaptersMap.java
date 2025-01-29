package net.w3e.wlib.dungeon.json;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilter;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilterValues;
import net.w3e.wlib.dungeon.layers.filter.types.DifficultyRoomFilter;
import net.w3e.wlib.dungeon.layers.filter.types.DistanceRoomFilter;
import net.w3e.wlib.dungeon.layers.filter.types.RandomRangeRoomFilter;
import net.w3e.wlib.dungeon.layers.filter.types.TempRoomFilter;
import net.w3e.wlib.dungeon.layers.filter.types.WetRoomFilter;
import net.w3e.wlib.dungeon.layers.terra.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.WetLayer;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;

public class RoomLayerJsonAdaptersMap extends WJsonTypedTypeAdapter.WJsonAdaptersMap<RoomLayerFilter<?>> {

	private final List<RoomLayerFilterCache> filters = new ArrayList<>();

	public final WJsonTypedTypeAdapter<TempRoomFilter> TEMPERATURE = this.registerConfigType(new WJsonTypedTypeAdapter<>(TemperatureLayer.KEY, TempRoomFilter.class));
	public final WJsonTypedTypeAdapter<WetRoomFilter> WET = this.registerConfigType(new WJsonTypedTypeAdapter<>(WetLayer.KEY, WetRoomFilter.class));
	public final WJsonTypedTypeAdapter<DifficultyRoomFilter> DIFFICULTY = this.registerConfigType(new WJsonTypedTypeAdapter<>(DifficultyLayer.KEY, DifficultyRoomFilter.class));
	public final WJsonTypedTypeAdapter<DistanceRoomFilter> DISTANCE = this.registerConfigType(new WJsonTypedTypeAdapter<>(DistanceLayer.KEY, DistanceRoomFilter.class));
	public final WJsonTypedTypeAdapter<RandomRangeRoomFilter> RANDOM = this.registerConfigType(new WJsonTypedTypeAdapter<>(RandomRangeRoomFilter.KEY, RandomRangeRoomFilter.class));
	
	public RoomLayerJsonAdaptersMap() {
		super(RoomLayerFilter.class);
	}

	@Override
	protected void registerAdapter(WJsonTypedTypeAdapter<? extends RoomLayerFilter<?>> configType) {
		this.filters.add(new RoomLayerFilterCache(configType));
	}

	public final RoomLayerFilterValues createFilters(DungeonRoomInfo room) {
		return new RoomLayerFilterValues(room, this.filters);
	}

	public static class RoomLayerFilterCache {

		private final WJsonTypedTypeAdapter<?> configType;
		private RoomLayerFilter<?> value;

		public RoomLayerFilterCache(WJsonTypedTypeAdapter<?> configType) {
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
