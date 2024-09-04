package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class WetLayer extends NoiseLayer {

	public static final String TYPE = "terra/wet";

	public static final String KEY = "wet";

	public WetLayer(DungeonGenerator generator, NoiseData data, int stepRate, boolean fast) {
		super(generator, data.withKey(KEY), stepRate, fast);
	}

	@Override
	public final WetLayer withDungeonImpl(DungeonGenerator generator) {
		return new WetLayer(generator, this.noise, this.stepRate, this.fast);
	}

	public static class WetLayerData extends NoiseLayerData<WetLayer> {
		@Override
		protected final WetLayer withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate, boolean fast) {
			return new WetLayer(generator, noise, stepRate, fast);
		}
	}

	public static final WetLayer example(DungeonGenerator generator) {
		return new WetLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build(), 50, false).setTypeKey(TYPE);
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

}
