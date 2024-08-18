package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class DifficultyLayer extends NoiseLayer {

	public static final String TYPE = "terra/difficulty";

	public static final String KEY = "difficulty";
	private final float add;
	private final float scale;

	public DifficultyLayer(DungeonGenerator generator, NoiseData data, int stepRate) {
		this(generator, data, stepRate, 0, 1);
	}

	public DifficultyLayer(DungeonGenerator generator, NoiseData data, int stepRate, float add, float scale) {
		super(generator, data.withKey(KEY), stepRate);
		this.add = add;
		this.scale = scale;
	}

	@Override
	public final DifficultyLayer withDungeonImpl(DungeonGenerator generator) {
		return new DifficultyLayer(generator, this.noise, this.stepRate, this.add, this.scale);
	}

	@Override
	protected final float modify(float value) {
		return value * this.scale + this.add;
	}

	public static class DifficultyLayerData extends NoiseLayerData<DifficultyLayer> {

		@Override
		protected final DifficultyLayer withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate) {
			return new DifficultyLayer(generator, noise, stepRate);
		}
	}

	public static final DifficultyLayer example(DungeonGenerator generator) {
		return new DifficultyLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build(), 50).setTypeKey(TYPE);
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

}
