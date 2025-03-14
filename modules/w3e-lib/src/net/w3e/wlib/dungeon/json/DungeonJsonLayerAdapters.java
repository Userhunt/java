package net.w3e.wlib.dungeon.json;

import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.layers.*;
import net.w3e.wlib.dungeon.layers.path.*;
import net.w3e.wlib.dungeon.layers.path.lab.LabDFSLayer;
import net.w3e.wlib.dungeon.layers.path.lab.LabHAKLayer;
import net.w3e.wlib.dungeon.layers.terra.*;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;
import net.w3e.wlib.json.WJsonTypedTypeAdapter.WJsonAdaptersMap;

public class DungeonJsonLayerAdapters extends WJsonAdaptersMap<DungeonLayer> {

	public final WJsonTypedTypeAdapter<EmptyLayer> EMPTY = this.registerConfigType(EmptyLayer.TYPE, EmptyLayer.class);

	@SuppressWarnings("unchecked")
	public final WJsonTypedTypeAdapter<PathRepeatLayer<?>> PATH_REPEAT = this.registerConfigType(PathRepeatLayer.TYPE, (Class<PathRepeatLayer<?>>)(Class<?>)PathRepeatLayer.class);
	public final WJsonTypedTypeAdapter<WormLayer> PATH_WORM = this.registerConfigType(WormLayer.TYPE, WormLayer.class);
	public final WJsonTypedTypeAdapter<DistanceLayer> DISTANCE = this.registerConfigType(DistanceLayer.TYPE, DistanceLayer.class);
	public final WJsonTypedTypeAdapter<CompositeTerraLayer> COMPOSITE = this.registerConfigType(CompositeTerraLayer.TYPE, CompositeTerraLayer.class);
	public final WJsonTypedTypeAdapter<DifficultyLayer> DIFFICULTY = this.registerConfigType(DifficultyLayer.TYPE, DifficultyLayer.class);
	public final WJsonTypedTypeAdapter<TemperatureLayer> TEMPERATURE = this.registerConfigType(TemperatureLayer.TYPE, TemperatureLayer.class);
	public final WJsonTypedTypeAdapter<WetLayer> WET = this.registerConfigType(WetLayer.TYPE, WetLayer.class);
	public final WJsonTypedTypeAdapter<ClearLayer> CLEAR = this.registerConfigType(ClearLayer.TYPE, ClearLayer.class);
	public final WJsonTypedTypeAdapter<RotateLayer> ROTATE = this.registerConfigType(RotateLayer.TYPE, RotateLayer.class);

	public final WJsonTypedTypeAdapter<BiomeLayer> BIOME = this.registerConfigType(BiomeLayer.TYPE, BiomeLayer.class);
	public final WJsonTypedTypeAdapter<RoomLayer> ROOM = this.registerConfigType(RoomLayer.TYPE, RoomLayer.class);
	public final WJsonTypedTypeAdapter<FeatureLayer> FEATURE = this.registerConfigType(FeatureLayer.TYPE, FeatureLayer.class);

	public final WJsonTypedTypeAdapter<LabHAKLayer> PATH_LAB_HAK = registerConfigType(LabHAKLayer.TYPE, LabHAKLayer.class);
	public final WJsonTypedTypeAdapter<LabDFSLayer> PATH_LAB_DFS = registerConfigType(LabDFSLayer.TYPE, LabDFSLayer.class);

	public DungeonJsonLayerAdapters() {
		super(DungeonLayer.class);
	}

	@Override
	protected final DungeonLayer createEmpty() {
		return new EmptyLayer();
	}
}
