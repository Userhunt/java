package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class TemperatureLayer extends NoiseLayer {

	public static final String TYPE = "terra/temperature";

	public static final String KEY = "temperature";

	public TemperatureLayer() {
		super(null, null);
	}

	public TemperatureLayer(DungeonGenerator generator, NoiseData data, int stepRate) {
		super(generator, data.withKey(KEY), stepRate);
	}

	@Override
	public final TemperatureLayer withDungeonImpl(DungeonGenerator generator) {
		return new TemperatureLayer(generator, this.noise, this.stepRate);
	}

	public static class TemperatureLayerData extends NoiseLayerData<TemperatureLayer> {

		@Override
		public final TemperatureLayer withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate) {
			return new TemperatureLayer(generator, noise, stepRate);
		}
	}

	public static final TemperatureLayer example(DungeonGenerator generator) {
		return new TemperatureLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).generateDefValue().build(), 50).setTypeKey(TYPE);
	}

	public static final int MIN = -25;
	public static final int MAX = 35;

}
