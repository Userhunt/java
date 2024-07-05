package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;

public class DifficultyLayer extends NoiseLayer {

	public static final String KEY = "difficulty";
	private final float add;
	private final float scale;

	public DifficultyLayer(DungeonGenerator generator, NoiseData data) {
		this(generator, data, 0, 1);
	}

	public DifficultyLayer(DungeonGenerator generator, NoiseData data, float add, float scale) {
		super(generator, data.withKey(KEY));
		this.add = add;
		this.scale = scale;
	}

	@Override
	protected final float modify(float value) {
		return value * this.scale + this.add;
	}

	public static final DifficultyLayer example(DungeonGenerator generator) {
		return new DifficultyLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build());
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

}
