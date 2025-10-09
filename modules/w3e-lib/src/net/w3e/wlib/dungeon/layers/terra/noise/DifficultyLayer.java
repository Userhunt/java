package net.w3e.wlib.dungeon.layers.terra.noise;

import java.lang.reflect.Type;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.room.DungeonRoomData;

@DefaultCodec(DifficultyLayer.DifficultyLayerJsonAdapter.class)
public class DifficultyLayer extends NoiseLayer {

	public static final String TYPE = "terra/difficulty";

	public static final String KEY = "difficulty";
	private final float add;
	private final float scale;

	public DifficultyLayer(DungeonGenerator generator, NoiseData data, int stepRate, float add, float scale, boolean createRoomIfNotExists) {
		super(DungeonLayer.JSON_MAP.DIFFICULTY, generator, KEY, data, stepRate, createRoomIfNotExists);
		this.add = add;
		this.scale = scale;
	}

	@Override
	public final DifficultyLayer createGenerator(DungeonGenerator generator) {
		return new DifficultyLayer(generator, this.noise, this.stepRate, this.add, this.scale, this.createRoomIfNotExists);
	}

	@Override
	protected void putData(DungeonRoomData data, float noise) {
		data.setDifficulty(noise * this.scale + this.add);
	}

	static class DifficultyLayerJsonAdapter extends ReflectiveBuilderCodec<DifficultyLayer> {

		public DifficultyLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, DifficultyLayerData.class, registry);
		}

		private static class DifficultyLayerData extends NoiseLayerData<DifficultyLayer> {

			private float add = 0;
			private float scale = 1;

			@Override
			protected final DifficultyLayer build(NoiseData noise, int stepRate, boolean createRoomIfNotExists) {
				return new DifficultyLayer(null, noise, stepRate, this.add, this.scale, createRoomIfNotExists);
			}
		}
	}

}
