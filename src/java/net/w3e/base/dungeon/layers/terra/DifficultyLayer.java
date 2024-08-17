package net.w3e.base.dungeon.layers.terra;

import lombok.NoArgsConstructor;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.json.ILayerAdapter;

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
	public final DifficultyLayer withDungeon(DungeonGenerator generator) {
		return new DifficultyLayer(generator, this.data, this.add, this.scale);
	}

	@Override
	protected final float modify(float value) {
		return value * this.scale + this.add;
	}

	@NoArgsConstructor
	@SuppressWarnings({"FieldMayBeFinal"})
	public static class DifficultyLayerData implements ILayerAdapter<DifficultyLayer> {
		private NoiseData noise = NoiseData.INSTANCE;
		@Override
		public final DifficultyLayer withDungeon(DungeonGenerator generator) {
			this.nonNull("noise", this.noise);
			return new DifficultyLayer(generator, this.noise);
		}
	}

	public static final DifficultyLayer example(DungeonGenerator generator) {
		return new DifficultyLayer(generator, new NoiseDataBuilder().setMinMax(MIN, MAX).build());
	}

	public static final int MIN = 0;
	public static final int MAX = 100;

}
