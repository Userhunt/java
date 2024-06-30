package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;

public class CompositeTerraLayer extends TerraLayer<Object> {

	private final TerraLayer<?>[] layers;

	public CompositeTerraLayer(DungeonGenerator generator, int stepRate, TerraLayer<?>... layers) {
		super(generator, null, null, stepRate);
		this.layers = layers;
	}

	@Override
	public final void setup(DungeonRoomInfo room) {
		for (TerraLayer<?> layer : this.layers) {
			layer.setup(room);
		}
	}

	@Override
	public final void regenerate() {
		super.regenerate();
	}

	@Override
	protected final void generate(DungeonRoomInfo room) {
		for (TerraLayer<?> layer : this.layers) {
			layer.generate(room);
		}
	}

	@Deprecated
	public static final CompositeTerraLayer example(DungeonGenerator generator) {
		return new CompositeTerraLayer(generator, 50, TemperatureLayer.example(generator), WetLayer.example(generator));
	}
	
}
