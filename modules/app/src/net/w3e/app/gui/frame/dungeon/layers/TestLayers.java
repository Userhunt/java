package net.w3e.app.gui.frame.dungeon.layers;

import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;

public class TestLayers {

	public static final TestLayers INSTANCE = new TestLayers();

	//public final ConfigType<LabHAKLayer> PATH_LAB_HAK = registerConfigType(EmptyLayer.TYPE, LabHAKLayer.class);
	//public final ConfigType<LabDFSLayer> PATH_LAB_DFS = registerConfigType(EmptyLayer.TYPE, LabDFSLayer.class);

	protected TestLayers() {}

	public static final <T extends DungeonLayer> ConfigType<T> registerConfigType(String keyName, Class<T> configClass) {
		return DungeonJsonAdapters.INSTANCE.layerAdapters.registerConfigType(keyName, configClass);
	}

	public static void init() {
		TestNoiseLayer.init();
	}
}
