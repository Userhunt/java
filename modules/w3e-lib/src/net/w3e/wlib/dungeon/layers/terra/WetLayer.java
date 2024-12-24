package net.w3e.wlib.dungeon.layers.terra;

import net.w3e.wlib.dungeon.DungeonGenerator;

public class WetLayer extends NoiseLayer {

	public static final String TYPE = "terra/wet";

	public static final String KEY = "wet";

	public WetLayer(DungeonGenerator generator, NoiseData data, int stepRate, boolean fast) {
		super(generator, data.withKey(KEY), stepRate, fast);
	}

	@Override
	protected String keyName() {
		return TYPE;
	}

	@Override
	public final WetLayer withDungeon(DungeonGenerator generator) {
		return new WetLayer(generator, this.noise, this.stepRate, this.fast);
	}

	public static class WetLayerAdapter extends NoiseLayerAdapter<WetLayerData> {
		public WetLayerAdapter() {
			super(WetLayerData.class);
		}
	}

	private static class WetLayerData extends NoiseLayerData<WetLayer> {
		@Override
		protected final WetLayer withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate, boolean fast) {
			return new WetLayer(generator, noise, stepRate, fast);
		}
	}


	public static final WetLayer example(DungeonGenerator generator) {
		return new WetLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build(), 50, false);
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

}
