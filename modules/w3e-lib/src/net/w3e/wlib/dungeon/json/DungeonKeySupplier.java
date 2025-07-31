package net.w3e.wlib.dungeon.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

@DefaultJsonCodec(DungeonKeySupplier.JCodec.class)
@RequiredArgsConstructor
public final class DungeonKeySupplier {

	@Setter
	private static Class<?> TYPE;

	private final Object key;

	public Object getRaw() {
		return this.key;
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T)getRaw();
	}

	static class JCodec extends AbstractJsonCodec<DungeonKeySupplier> {

		private final JsonCodec<Object> codec;

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			Objects.requireNonNull(TYPE);
			this.codec = registry.getCodecIndirect(TYPE);
		}

		@Override
		public void write(DungeonKeySupplier value, JsonWriter writer) throws IOException {
			this.codec.write(value.key, writer);
		}

		@Override
		public DungeonKeySupplier read(JsonReader reader) throws IOException {
			Object value = this.codec.read(reader);
			return new DungeonKeySupplier(value);
		}
	
		
	}
}
