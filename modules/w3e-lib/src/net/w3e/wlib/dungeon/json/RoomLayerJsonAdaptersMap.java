package net.w3e.wlib.dungeon.json;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.skds.lib2.io.json.codec.typed.ConfigType;
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
import net.w3e.wlib.json.adapters.WJsonAdaptersMap;

public class RoomLayerJsonAdaptersMap extends WJsonAdaptersMap<RoomLayerFilter<?>> {

	private final List<RoomLayerFilterCache> filters = new ArrayList<>();

	public final ConfigType<TempRoomFilter> TEMPERATURE = this.registerConfigType(TemperatureLayer.KEY, TempRoomFilter.class);
	public final ConfigType<WetRoomFilter> WET = this.registerConfigType(WetLayer.KEY, WetRoomFilter.class);
	public final ConfigType<DifficultyRoomFilter> DIFFICULTY = this.registerConfigType(DifficultyLayer.KEY, DifficultyRoomFilter.class);
	public final ConfigType<DistanceRoomFilter> DISTANCE = this.registerConfigType(DistanceLayer.KEY, DistanceRoomFilter.class);
	public final ConfigType<RandomRangeRoomFilter> RANDOM = this.registerConfigType(RandomRangeRoomFilter.KEY, RandomRangeRoomFilter.class);

	public RoomLayerJsonAdaptersMap() {
		super(RoomLayerFilter.class);
	}

	@Override
	protected void registerAdapter(ConfigType<? extends RoomLayerFilter<?>> configType) {
		this.filters.add(new RoomLayerFilterCache(configType));
	}

	public final RoomLayerFilterValues createFilters(DungeonRoomInfo room) {
		return new RoomLayerFilterValues(room, this.filters);
	}

	public static class RoomLayerFilterCache {

		private final ConfigType<?> configType;
		private RoomLayerFilter<?> value;

		public RoomLayerFilterCache(ConfigType<?> configType) {
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
