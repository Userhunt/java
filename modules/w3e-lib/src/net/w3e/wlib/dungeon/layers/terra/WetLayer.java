package net.w3e.wlib.dungeon.layers.terra;

import java.lang.reflect.Type;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;

@DefaultJsonCodec(WetLayer.WetLayerJsonAdapter.class)
public class WetLayer extends NoiseLayer {

	public static final String TYPE = "terra/wet";

	public static final String KEY = "wet";

	public WetLayer(DungeonGenerator generator, NoiseData data, int stepRate, boolean createRoomIfNotExists) {
		super(JSON_MAP.WET, generator, KEY, data, stepRate, createRoomIfNotExists);
	}

	@Override
	public final WetLayer withDungeon(DungeonGenerator generator) {
		return new WetLayer(generator, this.noise, this.stepRate, this.createRoomIfNotExists);
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

	static class WetLayerJsonAdapter extends JsonReflectiveBuilderCodec<WetLayer> {

		public WetLayerJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, WetLayerData.class, registry);
		}

		private static class WetLayerData extends NoiseLayerData<WetLayer> {
			@Override
			protected final WetLayer withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate, boolean createRoomIfNotExists) {
				return new WetLayer(generator, noise, stepRate, createRoomIfNotExists);
			}
		}
	}

	public static final WetLayer example(DungeonGenerator generator) {
		return new WetLayer(generator, new NoiseData.NoiseDataBuilder().setMinMax(MIN, MAX).build(), 50, true);
	}
}
