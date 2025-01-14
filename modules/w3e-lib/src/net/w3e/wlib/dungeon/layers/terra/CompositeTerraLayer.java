package net.w3e.wlib.dungeon.layers.terra;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.io.json.codec.array.ArraySerializeOnlyJsonCodec;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.ILayerData;

@DefaultJsonCodec(CompositeTerraLayer.CompositeTerraLayerJsonAdapter.class)
public class CompositeTerraLayer extends TerraLayer<Object> {

	public static final String TYPE = "terra/composite";

	@DefaultJsonCodec(CompositeTerraLayer.TerraLayerArrayJsonAdapter.class)
	private final TerraLayer<?>[] layers;

	public CompositeTerraLayer(DungeonGenerator generator, int stepRate, boolean fast, TerraLayer<?>... layers) {
		super(TYPE, generator, null, null, stepRate, fast);
		this.layers = layers;
	}

	@Override
	public final CompositeTerraLayer withDungeon(DungeonGenerator generator) {
		return new CompositeTerraLayer(generator, this.stepRate, this.fast, Stream.of(this.layers).map(e -> e.withDungeon(generator)).toArray(TerraLayer[]::new));
	}

	@Override
	public final void setup(DungeonRoomInfo room) {
		for (TerraLayer<?> layer : this.layers) {
			layer.setup(room);
		}
	}

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		super.regenerate(composite);
		for (TerraLayer<?> layer : this.layers) {
			layer.regenerate(true);
		}
	}

	@Override
	protected final void generate(DungeonRoomInfo room) throws DungeonException {
		for (TerraLayer<?> layer : this.layers) {
			layer.generate(room);
		}
	}

	static class CompositeTerraLayerJsonAdapter extends JsonReflectiveBuilderCodec<CompositeTerraLayerJsonAdapter.CompositeTerraLayerData> {

		public CompositeTerraLayerJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, CompositeTerraLayerData.class, registry);
		}

		private static class CompositeTerraLayerData implements ILayerData<CompositeTerraLayer> {

			private DungeonLayer[] layers;
			private int stepRate = 75;
			private boolean fast;

			@Override
			public final CompositeTerraLayer withDungeon(DungeonGenerator generator) {
				this.lessThan("stepRate", this.stepRate);
				this.isEmpty("layers", this.layers);
				return new CompositeTerraLayer(generator, this.stepRate, fast, Stream.of(this.layers).map(layer -> layer.withDungeon(generator)).toArray(TerraLayer[]::new));
			}
		}
	}

	private static class TerraLayerArrayJsonAdapter extends ArraySerializeOnlyJsonCodec {
	
		public TerraLayerArrayJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(DungeonLayer.class, registry);
		}
	}

	public static final CompositeTerraLayer example(DungeonGenerator generator) {
		return new CompositeTerraLayer(generator, 50, false, TemperatureLayer.example(generator), WetLayer.example(generator), DifficultyLayer.example(generator));
	}

}
