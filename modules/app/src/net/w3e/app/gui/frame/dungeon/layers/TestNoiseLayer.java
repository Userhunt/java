package net.w3e.app.gui.frame.dungeon.layers;

import net.w3e.wlib.dungeon.layers.terra.noise.NoiseData;
import net.w3e.wlib.dungeon.layers.terra.noise.NoiseData.Mode;
import net.w3e.wlib.dungeon.layers.terra.noise.NoiseData.NoiseSKDSBuilder;
import net.w3e.wlib.dungeon.layers.terra.noise.TemperatureLayer;

public abstract class TestNoiseLayer extends TestLayers {

	public static final int DEFAULT = 0;
	public static final int MIN = -25;
	public static final int MAX = 30;
	public static final int SCALE = 8;
	public static final String KEY = TemperatureLayer.KEY;
	public static final int STEP = 100;

	public static void init() {}

	public static NoiseData.NoiseDataBuilder data() {
		return new NoiseData.NoiseDataBuilder().setMinMax(TestNoiseLayer.MIN, TestNoiseLayer.MAX).setScale(1d / TestNoiseLayer.SCALE).setDefValue(TestNoiseLayer.DEFAULT);
	}

	public static TemperatureLayer w3e() {
		return new TemperatureLayer(null, data().build(), 100, true);
	}

	public static TemperatureLayer sasai() {
		return new TemperatureLayer(null, data().setMode(Mode.SKDS).setModeData(new NoiseSKDSBuilder().getAsJson()).build(), 100, true);
	}
}
