package net.w3e.wlib.dungeon.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;

@DefaultCodec(DungeonKeySupplier.JCodec.class)
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return this.key == null;
		} else if (obj == this) {
			return true;
		} else if (obj instanceof DungeonKeySupplier key) {
			return Objects.equals(this.key, key.key);
		} else if (obj.getClass() == TYPE) {
			return Objects.equals(this.key, obj);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.key);
	}

	@Override
	public String toString() {
		return Objects.toString(this.key);
	}

	static class JCodec extends AbstractCodec<DungeonKeySupplier> {

		private final UniversalCodec<Object> codec;

		public JCodec(Type type, CodecRegistry registry) {
			super(type, registry);
			Objects.requireNonNull(TYPE);
			this.codec = registry.getCodecIndirect(TYPE);
		}

		@Override
		public void write(DungeonKeySupplier value, UniversalWriter writer) throws IOException {
			this.codec.write(value.key, writer);
		}

		@Override
		public DungeonKeySupplier read(UniversalReader reader) throws IOException {
			Object value = this.codec.read(reader);
			return new DungeonKeySupplier(value);
		}
		
	}
}
