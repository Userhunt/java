package net.w3e.wlib.dungeon.layers.terra.noise;

import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.layers.terra.IListLayer;
import net.w3e.wlib.dungeon.layers.terra.TerraLayer;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.json.WJsonBuilder;

public abstract class NoiseLayer extends TerraLayer<Integer> implements IListLayer {

	protected final NoiseData noise;
	private transient long seed;

	public NoiseLayer(ConfigType<? extends NoiseLayer> configType, DungeonGenerator generator, String defKey, NoiseData noise, boolean createRoomIfNotExists) {
		this(configType, generator, defKey, noise, 50, createRoomIfNotExists);
	}

	public NoiseLayer(ConfigType<? extends NoiseLayer> configType, DungeonGenerator generator, String defKey, NoiseData noise, int stepRate, boolean createRoomIfNotExists) {
		super(configType, generator, defKey, noise.defValue, stepRate, createRoomIfNotExists);
		this.noise = noise.clone();
	}

	@Override
	public abstract NoiseLayer createGenerator(DungeonGenerator generator);

	@Override
	public void setupLayer(boolean composite) throws DungeonException {
		this.seed = this.random().nextLong();
		this.noise.setup(this.seed);
	}

	@Override
	public void setupRoom(DungeonRoomInfo room) {
		putData(room.getData(), this.defValue);
	}

	@Override
	public final void generateRoom(DungeonRoomInfo room) throws DungeonException {
		Vec3I pos = room.getPos();
		double scale = this.noise.scale;
		double x = pos.xi() * scale;
		double y = pos.yi() * scale;
		double z = pos.zi() * scale;
		float noise = this.noise.generate(this.seed, x, y, z);
		noise = this.noise.toRange(noise);

		putData(room.getData(), noise);
	}

	protected void putData(DungeonRoomData data, float noise) {
		data.put(this.defKey, noise);
	}

	@Override
	protected TerraGenerator createRoomGenerator() {
		return new TerraGenerator(this) {
			@Override
			protected void generateRoom(DungeonRoomInfo value) {
				NoiseLayer.this.generateRoom(value);
			}
		};
	}

	protected abstract static class NoiseLayerData<T extends NoiseLayer> implements WJsonBuilder<T> {
		protected NoiseData noise = NoiseData.INSTANCE;
		protected int stepRate = 50;
		protected boolean createRoomIfNotExists = false;
		@Override
		public final T build() {
			this.nonNull(this.noise, "noise");
			this.lessThan(this.stepRate, "stepRate");
			return this.build(this.noise, this.stepRate, this.createRoomIfNotExists);
		}

		protected abstract T build(NoiseData noise, int stepRate, boolean createRoomIfNotExists);
	}
}
