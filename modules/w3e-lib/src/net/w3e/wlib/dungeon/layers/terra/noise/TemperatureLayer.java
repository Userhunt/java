package net.w3e.wlib.dungeon.layers.terra.noise;

import java.lang.reflect.Type;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.room.DungeonRoomData;

@DefaultCodec(TemperatureLayer.TemperatureLayerJsonAdapter.class)
public class TemperatureLayer extends NoiseLayer {

	public static final String TYPE = "terra/temperature";

	public static final String KEY = "temperature";

	public TemperatureLayer(DungeonGenerator generator, NoiseData data, int stepRate, boolean createRoomIfNotExists) {
		super(JSON_MAP.TEMPERATURE, generator, KEY, data, stepRate, createRoomIfNotExists);
	}

	@Override
	public final TemperatureLayer createGenerator(DungeonGenerator generator) {
		return new TemperatureLayer(generator, this.noise, this.stepRate, this.createRoomIfNotExists);
	}

	@Override
	protected void putData(DungeonRoomData data, float noise) {
		data.setTemperature(noise / 100);
	}

	static class TemperatureLayerJsonAdapter extends ReflectiveBuilderCodec<TemperatureLayer> {

		public TemperatureLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, TemperatureLayerData.class, registry);
		}

		private static class TemperatureLayerData extends NoiseLayerData<TemperatureLayer> {
			@Override
			public final TemperatureLayer build(NoiseData noise, int stepRate, boolean createRoomIfNotExists) {
				return new TemperatureLayer(null, noise, stepRate, createRoomIfNotExists);
			}
		}
	}

}
