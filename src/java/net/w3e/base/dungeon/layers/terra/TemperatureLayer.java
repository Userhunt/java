package net.w3e.base.dungeon.layers.terra;

import lombok.NoArgsConstructor;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.json.ILayerAdapter;

public class TemperatureLayer extends NoiseLayer {

	public static final String KEY = "temperature";

	public TemperatureLayer(DungeonGenerator generator, NoiseData data) {
		super(generator, data.withKey(KEY));
	}

	@Override
	public final TemperatureLayer withDungeon(DungeonGenerator generator) {
		return new TemperatureLayer(generator, this.data);
	}

	@NoArgsConstructor
	@SuppressWarnings({"FieldMayBeFinal"})
	public static class TemperatureLayerData implements ILayerAdapter<TemperatureLayer> {
		private NoiseData noise = NoiseData.INSTANCE;
		@Override
		public final TemperatureLayer withDungeon(DungeonGenerator generator) {
			this.nonNull("noise", this.noise);
			return new TemperatureLayer(generator, this.noise);
		}
	}

	public static final TemperatureLayer example(DungeonGenerator generator) {
		return new TemperatureLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).generateDefValue().build());
	}

	public static final int MIN = -25;
	public static final int MAX = 35;

}
