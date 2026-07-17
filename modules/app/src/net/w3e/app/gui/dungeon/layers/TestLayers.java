package net.w3e.app.gui.dungeon.layers;

import net.skds.lib2.io.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;

public class TestLayers {

	//public final ConfigType<LabHAKLayer> PATH_LAB_HAK = registerConfigType(EmptyLayer.TYPE, LabHAKLayer.class);
	//public final ConfigType<LabDFSLayer> PATH_LAB_DFS = registerConfigType(EmptyLayer.TYPE, LabDFSLayer.class);

	public static <T extends DungeonLayer> ConfigType<T> registerConfigType(String keyName, Class<T> configClass) {
		return DungeonJsonAdapters.INSTANCE.layerAdapters.registerConfigType(keyName, configClass);
	}

	public static void init() {
		TestNoiseLayer.init();
	}
}
