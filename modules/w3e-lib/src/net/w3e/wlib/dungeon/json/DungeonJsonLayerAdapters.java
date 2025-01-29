package net.w3e.wlib.dungeon.json;

import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.layers.*;
import net.w3e.wlib.dungeon.layers.path.*;
import net.w3e.wlib.dungeon.layers.terra.*;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;
import net.w3e.wlib.json.WJsonTypedTypeAdapter.WJsonAdaptersMap;

public class DungeonJsonLayerAdapters extends WJsonAdaptersMap<DungeonLayer> {

	public final WJsonTypedTypeAdapter<EmptyLayer> EMPTY = this.registerConfigType(new WJsonTypedTypeAdapter<>(EmptyLayer.TYPE, EmptyLayer.class));

	@SuppressWarnings("unchecked")
	public final WJsonTypedTypeAdapter<PathRepeatLayer<?>> PATH_REPEAT = this.registerConfigType((WJsonTypedTypeAdapter<PathRepeatLayer<?>>)(WJsonTypedTypeAdapter<?>)new WJsonTypedTypeAdapter<>(PathRepeatLayer.TYPE, PathRepeatLayer.class));
	public final WJsonTypedTypeAdapter<WormLayer> PATH_WORM = this.registerConfigType(new WJsonTypedTypeAdapter<>(WormLayer.TYPE, WormLayer.class));
	public final WJsonTypedTypeAdapter<DistanceLayer> DISTANCE = this.registerConfigType(new WJsonTypedTypeAdapter<>(DistanceLayer.TYPE, DistanceLayer.class));
	public final WJsonTypedTypeAdapter<CompositeTerraLayer> COMPOSITE = this.registerConfigType(new WJsonTypedTypeAdapter<>(CompositeTerraLayer.TYPE, CompositeTerraLayer.class));
	public final WJsonTypedTypeAdapter<DifficultyLayer> DIFFICULTY = this.registerConfigType(new WJsonTypedTypeAdapter<>(DifficultyLayer.TYPE, DifficultyLayer.class));
	public final WJsonTypedTypeAdapter<TemperatureLayer> TEMPERATURE = this.registerConfigType(new WJsonTypedTypeAdapter<>(TemperatureLayer.TYPE, TemperatureLayer.class));
	public final WJsonTypedTypeAdapter<WetLayer> WET = this.registerConfigType(new WJsonTypedTypeAdapter<>(WetLayer.TYPE, WetLayer.class));
	public final WJsonTypedTypeAdapter<ClearLayer> CLEAR = this.registerConfigType(new WJsonTypedTypeAdapter<>(ClearLayer.TYPE, ClearLayer.class));
	public final WJsonTypedTypeAdapter<RotateLayer> ROTATE = this.registerConfigType(new WJsonTypedTypeAdapter<>(RotateLayer.TYPE, RotateLayer.class));

	public final WJsonTypedTypeAdapter<BiomeLayer> BIOME = this.registerConfigType(new WJsonTypedTypeAdapter<>(BiomeLayer.TYPE, BiomeLayer.class));
	public final WJsonTypedTypeAdapter<RoomLayer> ROOM = this.registerConfigType(new WJsonTypedTypeAdapter<>(RoomLayer.TYPE, RoomLayer.class));
	public final WJsonTypedTypeAdapter<FeatureLayer> FEATURE = this.registerConfigType(new WJsonTypedTypeAdapter<>(FeatureLayer.TYPE, FeatureLayer.class));

	public DungeonJsonLayerAdapters() {
		super(DungeonLayer.class);
	}

	@Override
	protected final DungeonLayer createEmpty() {
		return new EmptyLayer();
	}
}
