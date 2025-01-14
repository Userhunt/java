package net.w3e.wlib.dungeon.json;

import static net.w3e.wlib.dungeon.json.DungeonJsonAdapters.INSTANCE;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializeBuilder;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.layers.DungeonLayerFactory;
import net.w3e.wlib.mat.WBoxI;

public class DungeonJsonAdaptersString {

	public static void initString() {
		JsonUtils.addFactory(DungeonKeySupplier.class, (type, registry) -> new AbstractJsonCodec<DungeonKeySupplier>(type, registry) {
			private final JsonCodec<String> codec = this.registry.getCodec(String.class);
			@Override
			public void write(DungeonKeySupplier value, JsonWriter writer) throws IOException {
				this.codec.write(value.get(), writer);
			}
			@Override
			public DungeonKeySupplier read(JsonReader reader) throws IOException {
				String value = this.codec.read(reader);
				return () -> value;
			}
		});
		JsonUtils.addFactory(DungeonGenerator.class, (type, registry) -> new DungeonGeneratorJsonAdapter(type, registry));
		INSTANCE.register();
	}

	public static class DungeonGeneratorJsonAdapter extends JsonReflectiveBuilderCodec<DungeonGenerator> {
		public DungeonGeneratorJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, EDungeon.class, registry);
		}
	}

	private static class EDungeon implements JsonDeserializeBuilder<DungeonGenerator> {
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
