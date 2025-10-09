package net.w3e.wlib.dungeon.layers.terra.noise;

import java.lang.reflect.Type;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.room.DungeonRoomData;

@DefaultCodec(WetLayer.WetLayerJsonAdapter.class)
public class WetLayer extends NoiseLayer {

	public static final String TYPE = "terra/wet";

	public static final String KEY = "wet";

	public WetLayer(DungeonGenerator generator, NoiseData data, int stepRate, boolean createRoomIfNotExists) {
		super(JSON_MAP.WET, generator, KEY, data, stepRate, createRoomIfNotExists);
	}

	@Override
	public final WetLayer createGenerator(DungeonGenerator generator) {
		return new WetLayer(generator, this.noise, this.stepRate, this.createRoomIfNotExists);
	}

	@Override
	protected void putData(DungeonRoomData data, float noise) {
		data.setWet(noise);
	}

	static class WetLayerJsonAdapter extends ReflectiveBuilderCodec<WetLayer> {

		public WetLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, WetLayerData.class, registry);
		}

		private static class WetLayerData extends NoiseLayerData<WetLayer> {
			@Override
			protected final WetLayer build(NoiseData noise, int stepRate, boolean createRoomIfNotExists) {
				return new WetLayer(null, noise, stepRate, createRoomIfNotExists);
			}
		}
	}

}
