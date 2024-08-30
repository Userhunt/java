package net.w3e.base.dungeon.layers.terra;

import java.util.stream.Stream;

import com.google.gson.JsonSyntaxException;

import net.w3e.base.dungeon.DungeonException;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.message.MessageUtil;

public class CompositeTerraLayer extends TerraLayer<Object> {

	public static final String TYPE = "terra/composite";

	private final TerraLayer<?>[] layers;

	public CompositeTerraLayer(DungeonGenerator generator, int stepRate, TerraLayer<?>... layers) {
		super(generator, null, null, stepRate);
		this.layers = layers;
	}

	@Override
	public final CompositeTerraLayer withDungeonImpl(DungeonGenerator generator) {
		return new CompositeTerraLayer(generator, this.stepRate, Stream.of(this.layers).map(e -> e.withDungeon(generator)).toArray(TerraLayer[]::new));
	}

	@Override
	public final void setup(DungeonRoomInfo room) {
		for (TerraLayer<?> layer : this.layers) {
			layer.setup(room);
		}
	}

	@Override
	public final void regenerate(WDirection rotation, boolean composite) throws DungeonException {
		super.regenerate(rotation, composite);
		for (TerraLayer<?> layer : this.layers) {
			layer.regenerate(rotation, true);
		}
	}

	@Override
	protected final void generate(DungeonRoomInfo room) throws DungeonException {
		for (TerraLayer<?> layer : this.layers) {
			layer.generate(room);
		}
	}

	@SuppressWarnings({"FieldMayBeFinal"})
	public static class CompositeTerraLayerData implements ILayerAdapter<CompositeTerraLayer> {

		private DungeonLayer[] layers;
		private int stepRate = 75;
		@Override
		public final CompositeTerraLayer withDungeon(DungeonGenerator generator) {
			this.lessThan("stepRate", this.stepRate);
			this.isEmpty("layers", this.layers);
			if (!Stream.of(this.layers).anyMatch(layer -> !(layer instanceof TerraLayer))) {
				return new CompositeTerraLayer(generator, this.stepRate, Stream.of(this.layers).map(layer -> layer.withDungeon(generator)).toArray(TerraLayer[]::new));
			} else {
				throw new JsonSyntaxException(MessageUtil.EXPECTED.createMsg(Stream.of(this.layers).filter(layer -> !(layer instanceof TerraLayer)), "terra layer", TerraLayer.class.getSimpleName()));
			}
		}
	}

	public static final CompositeTerraLayer example(DungeonGenerator generator) {
		return new CompositeTerraLayer(generator, 50, TemperatureLayer.example(generator), WetLayer.example(generator), DifficultyLayer.example(generator)).setTypeKey(TYPE);
	}

}
