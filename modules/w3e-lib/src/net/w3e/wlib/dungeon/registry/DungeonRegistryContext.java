package net.w3e.wlib.dungeon.registry;

import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.terra.biome.BiomeLayer;

public interface DungeonRegistryContext {

	DungeonLayer getLayer(DungeonKeySupplier key);

	BiomeLayer.BiomeInfo getBiome(DungeonKeySupplier key);

	<T> T getTyped(DungeonKeySupplier key, Class<T> type);
}
