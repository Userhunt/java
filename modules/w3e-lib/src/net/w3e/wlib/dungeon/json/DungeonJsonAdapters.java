package net.w3e.wlib.dungeon.json;

import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;

public class DungeonJsonAdapters {
	public static final DungeonJsonAdapters INSTANCE = new DungeonJsonAdapters();

	public final RoomLayerJsonAdaptersMap roomFilterAdapters = new RoomLayerJsonAdaptersMap();
	public final DungeonJsonLayerAdapters layerAdapters = new DungeonJsonLayerAdapters();

	private DungeonJsonAdapters() {}

	public final void registerLayerAdapter(WJsonTypedTypeAdapter<? extends DungeonLayer> configType) {
		this.layerAdapters.registerConfigType(configType);
	}

	public final void register() {}
}
