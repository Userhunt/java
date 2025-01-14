package net.w3e.wlib.dungeon.json;

import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapter.DungeonJsonAdaptersMap;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.RotateLayer;
import net.w3e.wlib.dungeon.layers.filter.types.*;
import net.w3e.wlib.dungeon.layers.path.PathRepeatLayer;
import net.w3e.wlib.dungeon.layers.path.WormLayer;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.dungeon.layers.terra.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.WetLayer;

public class DungeonJsonAdapters {
	public static final DungeonJsonAdapters INSTANCE = new DungeonJsonAdapters();

	public final DungeonJsonAdaptersMap layerAdapters = new DungeonJsonAdaptersMap(DungeonLayer.class);
	public final RoomLayerJsonAdaptersMap roomFilter = new RoomLayerJsonAdaptersMap();

	private DungeonJsonAdapters() {}

	static {
		INSTANCE.registerDefault();
	}

	private void registerDefault() {
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(TemperatureLayer.KEY, TempRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(WetLayer.KEY, WetRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(DifficultyLayer.KEY, DifficultyRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(DistanceLayer.KEY, DistanceRoomFilter.class));
		this.roomFilter.registerConfigType(new DungeonJsonAdapter<>(RandomRangeRoomFilter.KEY, RandomRangeRoomFilter.class));

		this.registerLayerAdapter(new DungeonJsonAdapter<>(PathRepeatLayer.TYPE, PathRepeatLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(WormLayer.TYPE, WormLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(DistanceLayer.TYPE, DistanceLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(CompositeTerraLayer.TYPE, CompositeTerraLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(DifficultyLayer.TYPE, DifficultyLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(TemperatureLayer.TYPE, TemperatureLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(WetLayer.TYPE, WetLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(ClearLayer.TYPE, ClearLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(RotateLayer.TYPE, RotateLayer.class));

		this.registerLayerAdapter(new DungeonJsonAdapter<>(BiomeLayer.TYPE, BiomeLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(RoomLayer.TYPE, RoomLayer.class));
		this.registerLayerAdapter(new DungeonJsonAdapter<>(FeatureLayer.TYPE, FeatureLayer.class));
	}

	public final void registerLayerAdapter(DungeonJsonAdapter<?> configType) {
		this.layerAdapters.registerConfigType(configType);
	}

	public final void register() {}
}
