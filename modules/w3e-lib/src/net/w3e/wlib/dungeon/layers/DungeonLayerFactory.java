package net.w3e.wlib.dungeon.layers;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.SerializeOnlyJsonCodec;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;

@FunctionalInterface
@DefaultJsonCodec(value = DungeonLayerFactory.DungeonLayerFactoryJsonAdapter.class)
public interface DungeonLayerFactory {
	DungeonLayer create(DungeonGenerator generator);

	static class DungeonLayerFactoryJsonAdapter extends SerializeOnlyJsonCodec<DungeonLayerFactory> {

		public DungeonLayerFactoryJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(DungeonLayerFactory value, JsonWriter writer) throws IOException {
			DungeonLayer v = value.create(null);
			this.registry.getCodec(DungeonLayer.class).write(v, writer);
		}
	}
}
