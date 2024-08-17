package net.w3e.base.dungeon.json;

import net.w3e.base.dungeon.layers.ClearLayer;
import net.w3e.base.dungeon.layers.DistanceLayer;
import net.w3e.base.dungeon.layers.path.PathRepeatLayer.PathRepeatLayerData;
import net.w3e.base.dungeon.layers.path.WormLayer.WormLayerData;
import net.w3e.base.dungeon.layers.terra.CompositeTerraLayer.CompositeTerraLayerData;
import net.w3e.base.dungeon.layers.terra.DifficultyLayer.DifficultyLayerData;
import net.w3e.base.dungeon.layers.terra.TemperatureLayer.TemperatureLayerData;
import net.w3e.base.dungeon.layers.terra.WetLayer.WetLayerData;

public class DungeonGeneratorJsonAdapters {

	private static final DungeonLayerJsonAdapter LAYERS_ADAPTER = new DungeonLayerJsonAdapter();

	static {
		LAYERS_ADAPTER.register("path/worm", WormLayerData.class);
		LAYERS_ADAPTER.register("path/repeat", PathRepeatLayerData.class);
		LAYERS_ADAPTER.register("distance", DistanceLayer.class);
		LAYERS_ADAPTER.register("terra/composite", CompositeTerraLayerData.class);
		LAYERS_ADAPTER.register("terra/difficulty", DifficultyLayerData.class);
		LAYERS_ADAPTER.register("terra/temperature", TemperatureLayerData.class);
		LAYERS_ADAPTER.register("terra/wet", WetLayerData.class);
		// BIOME
		// ROOM
		// FEATURE

		LAYERS_ADAPTER.register("clear", ClearLayer.class);
	}

	public static final DungeonLayerJsonAdapter getLayersAdapter() {
		return LAYERS_ADAPTER.copy(true);
	}
}
