package net.w3e.wlib.dungeon.json;

import net.skds.lib2.io.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.filters.RoomFilter;
import net.w3e.wlib.dungeon.filters.*;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.EmptyLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.VariantTerraLayer;
import net.w3e.wlib.dungeon.layers.terra.noise.WetLayer;
import net.w3e.wlib.json.adapters.WJsonAdaptersMap;

public class RoomLayerJsonAdaptersMap extends WJsonAdaptersMap<RoomFilter> {

	public final ConfigType<RoomFilterEmpty> EMPTY = this.registerConfigType(EmptyLayer.TYPE, RoomFilterEmpty.class);
	public final ConfigType<RoomFilterTemp> TEMPERATURE = this.registerConfigType(TemperatureLayer.KEY, RoomFilterTemp.class);
	public final ConfigType<RoomFilterWet> WET = this.registerConfigType(WetLayer.KEY, RoomFilterWet.class);
	public final ConfigType<RoomFilterVariant> VARIANT = this.registerConfigType(VariantTerraLayer.KEY, RoomFilterVariant.class);
	public final ConfigType<RoomFilterDifficulty> DIFFICULTY = this.registerConfigType(DifficultyLayer.KEY, RoomFilterDifficulty.class);
	public final ConfigType<RoomFilterDistance> DISTANCE = this.registerConfigType(DistanceLayer.KEY, RoomFilterDistance.class);
	public final ConfigType<RoomFilterRandom> RANDOM = this.registerConfigType(RoomFilterRandom.KEY, RoomFilterRandom.class);
	public final ConfigType<RoomFilterNot> NOT = this.registerConfigType(RoomFilterNot.KEY, RoomFilterNot.class);

	public RoomLayerJsonAdaptersMap() {
		super(RoomFilter.class);
	}

	@Override
	protected RoomFilter createEmpty() {
		return RoomFilterEmpty.INSTANCE;
	}

}
