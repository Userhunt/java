package net.w3e.wlib.dungeon.json;

import static net.w3e.wlib.dungeon.json.DungeonJsonAdapters.INSTANCE;

import java.io.IOException;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;

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
		//JsonUtils.addFactory(DungeonGenerator.class, (type, registry) -> new DungeonGeneratorJsonAdapter(type, registry));
		INSTANCE.register();
	}
}
