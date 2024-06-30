package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.OpenSimplex2S;
import net.w3e.base.math.vector.WVector3;

public class NoiseLayer extends TerraLayer<Integer> {

	private final int min;
	private final int max;
	private final double scale;
	private long seed;

	public NoiseLayer(DungeonGenerator generator, String defKey, int min, int max, double scale) {
		this(generator, defKey, min, max, scale, BMatUtil.round(((double)max - min) / 2 + min), 50);
	}

	public NoiseLayer(DungeonGenerator generator, String defKey, int min, int max, double scale, int defValue, int stepRate) {
		super(generator, defKey, defValue, stepRate);
		this.min = min;
		this.max = max;
		if (scale < 0) {
			scale = 1d / scale;
		}
		this.scale = scale;
	}

	@Override
	public final void regenerate() {
		this.seed = this.random().nextLong();
		super.regenerate();
	}

	@Override
	public final void generate(DungeonRoomInfo room) {
		WVector3 pos = room.pos();
		double x = pos.getX() * scale;
		double y = pos.getY() * scale;
		double z = pos.getZ() * scale;
		float noise = OpenSimplex2S.noise3_ImproveXZ(this.seed, x, y, z);
		int value = BMatUtil.round(BMatUtil.toRange(noise, -1, 1, this.min, this.max));
		room.data().put(this.defKey, value);
	}
}
