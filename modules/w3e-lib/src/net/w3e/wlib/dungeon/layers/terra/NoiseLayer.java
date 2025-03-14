package net.w3e.wlib.dungeon.layers.terra;

import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;

public abstract class NoiseLayer extends TerraLayer<Integer> {

	protected final NoiseData noise;
	private transient long seed;

	public NoiseLayer(WJsonTypedTypeAdapter<? extends NoiseLayer> configType, DungeonGenerator generator, String defKey, NoiseData noise, boolean createRoomIfNotExists) {
		this(configType, generator, defKey, noise, 50, createRoomIfNotExists);
	}

	public NoiseLayer(WJsonTypedTypeAdapter<? extends NoiseLayer> configType, DungeonGenerator generator, String defKey, NoiseData noise, int stepRate, boolean createRoomIfNotExists) {
		super(configType, generator, defKey, noise.defValue, stepRate, createRoomIfNotExists);
		this.noise = noise.clone();
	}

	@Override
	public abstract NoiseLayer withDungeon(DungeonGenerator generator);

	@Override
	public void setupLayer(boolean composite) throws DungeonException {
		this.seed = this.random().nextLong();
		this.noise.setup(this.seed);
	}

	@Override
	public final void generate(DungeonRoomInfo room) throws DungeonException {
		Vec3I pos = room.pos();
		double scale = this.noise.scale;
		double x = pos.xi() * scale;
		double y = pos.yi() * scale;
		double z = pos.zi() * scale;
		float noise = this.noise.generate(this.seed, x, y, z);
		noise = this.noise.toRange(noise);
		noise = this.modify(noise);
		room.data().put(this.defKey, noise);
	}

	protected float modify(float value) {
		return value;
	}

	protected abstract static class NoiseLayerData<T extends NoiseLayer> implements ILayerData<T> {
		protected NoiseData noise = NoiseData.INSTANCE;
		protected int stepRate = 50;
		protected boolean createRoomIfNotExists = false;
		@Override
		public final T withDungeon(DungeonGenerator generator) {
			this.nonNull("noise", this.noise);
			this.lessThan("stepRate", this.stepRate);
			return this.withDungeon(generator, this.noise, this.stepRate, this.createRoomIfNotExists);
		}

		protected abstract T withDungeon(DungeonGenerator generator, NoiseData noise, int stepRate, boolean createRoomIfNotExists);
	}
}
