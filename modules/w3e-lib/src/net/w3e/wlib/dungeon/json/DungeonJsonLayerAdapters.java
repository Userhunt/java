package net.w3e.wlib.dungeon.json;

import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.layers.*;
import net.w3e.wlib.dungeon.layers.path.*;
import net.w3e.wlib.dungeon.layers.path.lab.LabDFSLayer;
import net.w3e.wlib.dungeon.layers.path.lab.LabHAKLayer;
import net.w3e.wlib.dungeon.layers.terra.*;
import net.w3e.wlib.json.adapters.WJsonAdaptersMap;

public class DungeonJsonLayerAdapters extends WJsonAdaptersMap<DungeonLayer> {

	public final ConfigType<EmptyLayer> EMPTY = this.registerConfigType(EmptyLayer.TYPE, EmptyLayer.class);

	@SuppressWarnings("unchecked")
	public final ConfigType<PathRepeatLayer<?>> PATH_REPEAT = this.registerConfigType(PathRepeatLayer.TYPE, (Class<PathRepeatLayer<?>>)(Class<?>)PathRepeatLayer.class);
	public final ConfigType<WormLayer> PATH_WORM = this.registerConfigType(WormLayer.TYPE, WormLayer.class);
	public final ConfigType<DistanceLayer> DISTANCE = this.registerConfigType(DistanceLayer.TYPE, DistanceLayer.class);
	public final ConfigType<CompositeTerraLayer> COMPOSITE = this.registerConfigType(CompositeTerraLayer.TYPE, CompositeTerraLayer.class);
	public final ConfigType<DifficultyLayer> DIFFICULTY = this.registerConfigType(DifficultyLayer.TYPE, DifficultyLayer.class);
	public final ConfigType<TemperatureLayer> TEMPERATURE = this.registerConfigType(TemperatureLayer.TYPE, TemperatureLayer.class);
	public final ConfigType<WetLayer> WET = this.registerConfigType(WetLayer.TYPE, WetLayer.class);
	public final ConfigType<ClearLayer> CLEAR = this.registerConfigType(ClearLayer.TYPE, ClearLayer.class);
	public final ConfigType<RotateLayer> ROTATE = this.registerConfigType(RotateLayer.TYPE, RotateLayer.class);

	public final ConfigType<BiomeLayer> BIOME = this.registerConfigType(BiomeLayer.TYPE, BiomeLayer.class);
	public final ConfigType<RoomLayer> ROOM = this.registerConfigType(RoomLayer.TYPE, RoomLayer.class);
	public final ConfigType<FeatureLayer> FEATURE = this.registerConfigType(FeatureLayer.TYPE, FeatureLayer.class);

	public final ConfigType<LabHAKLayer> PATH_LAB_HAK = registerConfigType(LabHAKLayer.TYPE, LabHAKLayer.class);
	public final ConfigType<LabDFSLayer> PATH_LAB_DFS = registerConfigType(LabDFSLayer.TYPE, LabDFSLayer.class);

	public DungeonJsonLayerAdapters() {
		super(DungeonLayer.class);
	}

	@Override
	protected final DungeonLayer createEmpty() {
		return new EmptyLayer();
	}
}
