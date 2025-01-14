package net.w3e.wlib.dungeon.layers.terra;

import java.lang.reflect.Type;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;

@DefaultJsonCodec(DifficultyLayer.DifficultyLayerJsonAdapter.class)
public class DifficultyLayer extends NoiseLayer {

	public static final String TYPE = "terra/difficulty";

	public static final String KEY = "difficulty";
	private final float add;
	private final float scale;

	public DifficultyLayer(DungeonGenerator generator, NoiseData data, int stepRate, float add, float scale, boolean fast) {
		super(TYPE, generator, data.withKey(KEY), stepRate, fast);
		this.add = add;
		this.scale = scale;
	}

	@Override
	public final DifficultyLayer withDungeon(DungeonGenerator generator) {
		return new DifficultyLayer(generator, this.noise, this.stepRate, this.add, this.scale, this.fast);
	}

	@Override
	protected final float modify(float value) {
		return value * this.scale + this.add;
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

	private static class DifficultyLayerJsonAdapter extends JsonReflectiveBuilderCodec<DifficultyLayerJsonAdapter.DifficultyLayerData> {

		public DifficultyLayerJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, DifficultyLayerData.class, registry);
		}

		private static class DifficultyLayerData extends NoiseLayerData<DifficultyLayer> {

			private float add = 0;
			private float scale = 1;
	
			@Override
			protected final DifficultyLayer withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate, boolean fast) {
				return new DifficultyLayer(generator, noise, stepRate, this.add, this.scale, fast);
			}
		}
	}

	public static final DifficultyLayer example(DungeonGenerator generator) {
		return new DifficultyLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build(), 50, 0, 1, false);
	}
}
