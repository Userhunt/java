package net.w3e.base.dungeon.layers.terra;

import lombok.NoArgsConstructor;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.json.ILayerAdapter;

public class WetLayer extends NoiseLayer {

	public static final String KEY = "wet";

	public WetLayer(DungeonGenerator generator, NoiseData data) {
		super(generator, data.withKey(KEY));
	}

	@Override
	public final WetLayer withDungeon(DungeonGenerator generator) {
		return new WetLayer(generator, this.data);
	}

	@NoArgsConstructor
	@SuppressWarnings({"FieldMayBeFinal"})
	public static class WetLayerData implements ILayerAdapter<WetLayer> {
		private NoiseData noise = NoiseData.INSTANCE;
		@Override
		public final WetLayer withDungeon(DungeonGenerator generator) {
			this.nonNull("noise", this.noise);
			return new WetLayer(generator, this.noise);
		}
	}

	public static final WetLayer example(DungeonGenerator generator) {
		return new WetLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build());
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

}
