package net.w3e.app.gui.frame.dungeon.layers;

import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;

public class TestLayers {

	public static final TestLayers INSTANCE = new TestLayers();

	//public final WJsonTypedTypeAdapter<LabHAKLayer> PATH_LAB_HAK = registerConfigType(EmptyLayer.TYPE, LabHAKLayer.class);
	//public final WJsonTypedTypeAdapter<LabDFSLayer> PATH_LAB_DFS = registerConfigType(EmptyLayer.TYPE, LabDFSLayer.class);

	protected TestLayers() {}

	public static final <T extends DungeonLayer> WJsonTypedTypeAdapter<T> registerConfigType(String keyName, Class<T> configClass) {
		return DungeonJsonAdapters.INSTANCE.layerAdapters.registerConfigType(keyName, configClass);
	}

	public static void init() {
		TestNoiseLayer.init();
	}
}
