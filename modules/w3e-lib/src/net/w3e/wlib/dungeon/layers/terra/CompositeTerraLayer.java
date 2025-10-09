package net.w3e.wlib.dungeon.layers.terra;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.array.ArraySerializeOnlyCodec;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.layers.ISetupRoomLayer;
import net.w3e.wlib.dungeon.layers.ListLayer;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.json.WJsonBuilder;

@DefaultCodec(CompositeTerraLayer.CompositeTerraLayerJsonAdapter.class)
public class CompositeTerraLayer extends TerraLayer<Object> implements IListLayer {

	public static final String TYPE = "terra/composite";

	@DefaultCodec(CompositeTerraLayer.TerraLayerArrayJsonAdapter.class)
	private final ListLayer<?>[] layers;

	public CompositeTerraLayer(DungeonGenerator generator, int stepRate, boolean createRoomIfNotExists, ListLayer<?>... layers) {
		super(JSON_MAP.COMPOSITE, generator, null, null, stepRate, createRoomIfNotExists);
		this.layers = layers;
	}

	@Override
	public void applyRegistryContext(DungeonRegistryContext registryContext) {
		for (DungeonLayer layer : this.layers) {
			layer.applyRegistryContext(registryContext);
		}
	}

	@Override
	public final CompositeTerraLayer createGenerator(DungeonGenerator generator) {
		return new CompositeTerraLayer(generator, this.stepRate, this.createRoomIfNotExists, Stream.of(this.layers).map(e -> e.createGenerator(generator)).toArray(TerraLayer[]::new));
	}

	@Override
	public final void setupRoom(DungeonRoomInfo room) {
		for (DungeonLayer layer : this.layers) {
			if (layer instanceof ISetupRoomLayer setupRoomLayer) {
				setupRoomLayer.setupRoom(room);
			}
		}
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {
		for (DungeonLayer layer : this.layers) {
			layer.setupLayer(true);
		}
	}

	@Override
	public final void generateRoom(DungeonRoomInfo room) {
		this.generator.generateRoom(room);
	}

	@Override
	protected TerraLayer<Object>.TerraGenerator createRoomGenerator() {
		return new TerraGenerator(this) {

			@Override
			protected void generateRoom(DungeonRoomInfo room) throws DungeonException {
				for (ListLayer<?> layer : CompositeTerraLayer.this.layers) {
					if (layer instanceof IListLayer listLayer) {
						listLayer.generateRoom(room);
					}
				}
			}
		};
	}

	static class CompositeTerraLayerJsonAdapter extends ReflectiveBuilderCodec<CompositeTerraLayer> {

		public CompositeTerraLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, CompositeTerraLayerData.class, registry);
		}

		private static class CompositeTerraLayerData implements WJsonBuilder<CompositeTerraLayer> {

			private DungeonLayer[] layers;
			private int stepRate = 75;
			private boolean createRoomIfNotExists;

			@Override
			public final CompositeTerraLayer build() {
				this.lessThan(this.stepRate, "stepRate");
				this.isEmpty(this.layers, "layers");
				return new CompositeTerraLayer(null, this.stepRate, createRoomIfNotExists, Stream.of(this.layers).toArray(ListLayer[]::new));
			}
		}
	}

	private static class TerraLayerArrayJsonAdapter extends ArraySerializeOnlyCodec {

		public TerraLayerArrayJsonAdapter(Type type, CodecRegistry registry) {
			super(DungeonLayer.class, registry);
		}
	}

}
