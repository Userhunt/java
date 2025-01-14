package net.w3e.wlib.dungeon.layers.terra;

import java.lang.reflect.Type;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;

@DefaultJsonCodec(TemperatureLayer.TemperatureLayerJsonAdapter.class)
public class TemperatureLayer extends NoiseLayer {

	public static final String TYPE = "terra/temperature";

	public static final String KEY = "temperature";

	public TemperatureLayer(DungeonGenerator generator, NoiseData data, int stepRate, boolean fast) {
		super(TYPE, generator, data.withKey(KEY), stepRate, fast);
	}

	@Override
	public final TemperatureLayer withDungeon(DungeonGenerator generator) {
		return new TemperatureLayer(generator, this.noise, this.stepRate, this.fast);
	}

	public static final int MIN = -25;
	public static final int MAX = 35;

	static class TemperatureLayerJsonAdapter extends JsonReflectiveBuilderCodec<TemperatureLayerJsonAdapter.TemperatureLayerData> {

		public TemperatureLayerJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, TemperatureLayerData.class, registry);
		}

		private static class TemperatureLayerData extends NoiseLayerData<TemperatureLayer> {
			@Override
			public final TemperatureLayer withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate, boolean fast) {
				return new TemperatureLayer(generator, noise, stepRate, fast);
			}
		}
	}

	public static final TemperatureLayer example(DungeonGenerator generator) {
		return new TemperatureLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).generateDefValue().build(), 50, false);
	}
}
