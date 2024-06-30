package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.OpenSimplex2S;
import net.w3e.base.math.vector.WVector3;

public class TemperatureLayer extends TerraLayer<Integer> {

	public static final String KEY = "temperature";

	private final int min;
	private final int max;
	private final double scale;
	private long seed;

	public TemperatureLayer(DungeonGenerator generator, int min, int max, double scale) {
		this(generator, min, max, scale, BMatUtil.round(((double)max - min) / 2 + min), 50);
	}

	public TemperatureLayer(DungeonGenerator generator, int min, int max, double scale, int def, int stepRate) {
		super(generator, KEY, def, stepRate);
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
		int temperature = BMatUtil.round(BMatUtil.toRange(noise, -1, 1, this.min, this.max));
		room.data().put("temperature", temperature);
	}

	public static final TemperatureLayer example(DungeonGenerator generator) {
		return new TemperatureLayer(generator, -25, 35, -8d);
	}

	public static final int MIN = -25;
	public static final int MAX = 35;

}
