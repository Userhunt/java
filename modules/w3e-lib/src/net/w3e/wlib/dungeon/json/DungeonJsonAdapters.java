package net.w3e.wlib.dungeon.json;

import net.skds.lib2.io.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonLayer;

public class DungeonJsonAdapters {
	public static final DungeonJsonAdapters INSTANCE = new DungeonJsonAdapters();

	public final RoomLayerJsonAdaptersMap roomFilterAdapters = new RoomLayerJsonAdaptersMap();
	public final DungeonJsonLayerAdapters layerAdapters = new DungeonJsonLayerAdapters();

	private DungeonJsonAdapters() {}

	public final void registerLayerAdapter(ConfigType<? extends DungeonLayer> configType) {
		this.layerAdapters.registerConfigType(configType);
	}

	public final void register() {}
}
