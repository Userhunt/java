package net.w3e.wlib.dungeon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializeBuilder;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.layers.DungeonLayerFactory;
import net.w3e.wlib.mat.WBoxI;

@DefaultJsonCodec(DungeonGeneratorFactory.JCodec.class)
public class DungeonGeneratorFactory {
	
	public final long seed;
	public final WBoxI dimension;
	private final MapTString dataFactory;
	private final List<DungeonLayerFactory> layers = new ArrayList<>();

	public DungeonGeneratorFactory(long seed, WBoxI dimension, MapTString dataFactory, List<DungeonLayerFactory> layers) {
		this.seed = seed;
		this.dimension = dimension;
		this.dataFactory = dataFactory;
		this.layers.addAll(layers);
	}

	public final DungeonGenerator create(Long seed, WBoxI dimension) {
		return new DungeonGenerator(seed == null ? this.seed : seed, dimension == null ? this.dimension : dimension, this.dataFactory == null ? new MapTString() : this.dataFactory, this.layers);
	}

	public final CompletableFuture<DungeonGeneratorResult> generator(Long seed, WBoxI dimension, DungeonGenerator.DungeonGenerationCallback callback) {
		return create(seed, dimension).generateAsync(callback);
	}

	public static class JCodec extends JsonReflectiveBuilderCodec<DungeonGenerator> {
		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, DGFData.class, registry);
		}

		private static class DGFData implements JsonDeserializeBuilder<DungeonGenerator> {
			private long seed = 0;
			private WBoxI dimension = new WBoxI(0, 0, 0, 0, 0, 0).expand(4, 0, 4);
			private MapTString data = new MapTString();
			private DungeonLayer[] layers = new DungeonLayer[0];
			private final transient List<DungeonLayerFactory> layerFactories = new ArrayList<>();
	
			@Override
			public final DungeonGenerator build() {
				if (this.layerFactories.size() != this.layers.length) {
					this.layerFactories.clear();
					for (DungeonLayer layer : this.layers) {
						if (layer == null) {
							throw new NullPointerException();
						}
						this.layerFactories.add(generator -> layer.withDungeon(generator));
					}
				}
				MapTString map = this.data != null ? new MapTString(this.data) : new MapTString();
				return new DungeonGenerator(this.seed, this.dimension, map, this.layerFactories);
			}
		}
	}
}
