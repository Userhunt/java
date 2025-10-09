package net.w3e.wlib.dungeon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.json.WJsonBuilder;
import net.w3e.wlib.mat.WBoxI;

@DefaultCodec(DungeonGeneratorFactory.JCodec.class)
public class DungeonGeneratorFactory {

	public final long seed;
	public final WBoxI dimension;
	protected final DungeonRoomData dataFactory;
	protected final List<DungeonLayer> layers = new ArrayList<>();

	public DungeonGeneratorFactory(long seed, WBoxI dimension, DungeonRoomData dataFactory, List<DungeonLayer> layers) {
		this.seed = seed;
		this.dimension = dimension;
		this.dataFactory = dataFactory;
		this.layers.addAll(layers);
	}

	public final void applyRegistryContext(DungeonRegistryContext registryContext) {
		layers.forEach(l -> l.applyRegistryContext(registryContext));
	}

	public final DungeonGenerator create(Long seed, WBoxI dimension) {
		return new DungeonGenerator(seed == null ? this.seed : seed, dimension == null ? this.dimension : dimension, this.dataFactory, this.layers);
	}

	public final CompletableFuture<DungeonGeneratorResult> generator(Long seed, WBoxI dimension, DungeonGenerator.DungeonGenerationCallback callback) {
		return create(seed, dimension).generateAsync(callback);
	}

	protected static class JCodec extends ReflectiveBuilderCodec<DungeonGeneratorFactory> {

		public JCodec(Type type, CodecRegistry registry) {
			this(type, DGFData.class, registry);
		}

		protected JCodec(Type type, Type builderType, CodecRegistry registry) {
			super(type, builderType, registry);
		}

		protected static class DGFData implements WJsonBuilder<DungeonGeneratorFactory> {
			protected long seed = 0;
			protected WBoxI dimension = new WBoxI(0, 0, 0, 0, 0, 0).expand(4, 0, 4);
			protected DungeonRoomData data = DungeonRoomData.NULL;
			protected List<DungeonLayer> layers = new ArrayList<>();

			@Override
			public DungeonGeneratorFactory build() {
				this.nonNull(this.data, "data");
				return new DungeonGeneratorFactory(this.seed, this.dimension, this.data, this.layers);
			}

		}
	}
}
