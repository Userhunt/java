package net.w3e.wlib.dungeon.registry;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonNamed;

@AllArgsConstructor
@DefaultCodec(DungeonRegistryObject.JCodec.class)
public final class DungeonRegistryObject implements IDungeonNamed {

	private final DungeonKeySupplier keyName;

	private Object object;

	public DungeonRegistryObject(Object object) {
		this(null, object);
	}

	@Override
	public DungeonKeySupplier keyName() {
		return this.keyName;
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject() {
		return (T)this.object;
	}

	public final void applyRegistry(DungeonRegistryContext registryContext, BiFunction<DungeonRegistryContext, DungeonKeySupplier, Object> getter) {
		if (this.keyName != null) {
			Object object = getter.apply(registryContext, this.keyName);
			if (object != null) {
				this.object = object;
			}
		}
	}

	public final <T> boolean isNull(Predicate<T> predicate, Class<T> cl) {
		if (this.object != null) {
			return predicate.test(this.getObject());
		} else {
			return this.keyName == null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof DungeonRegistryObject reg && reg.getClass() == this.getClass()) {
			if (this.keyName != null || reg.keyName != null) {
				return Objects.equals(this.keyName, reg.keyName);
			} else {
				return Objects.equals(this.object, reg.object);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (this.keyName != null) {
			return this.keyName.hashCode();
		}
		return super.hashCode();
	}

	@Override
	public String toString() {
		if (this.keyName() != null) {
			return this.keyName().toString();
		}
		return super.toString();
	}

	public static class JCodec extends AbstractCodec<DungeonRegistryObject> {

		private final UniversalCodec<DungeonKeySupplier> keyCodec;
		private final UniversalCodec<Object> objectCodec;

		public JCodec(Type objectType, CodecRegistry registry) {
			super(DungeonRegistryObject.class, registry);
			this.keyCodec = this.registry.getCodecIndirect(DungeonKeySupplier.class);
			this.objectCodec = this.registry.getCodecIndirect(objectType);
		}

		@Override
		public final void write(DungeonRegistryObject value, UniversalWriter writer) throws IOException {
			if (value.keyName != null) {
				this.keyCodec.write(value.keyName, writer);
			} else {
				this.objectCodec.write(value.object, writer);
			}
		}

		@Override
		public final DungeonRegistryObject read(UniversalReader reader) throws IOException {
			if (reader.nextEntryType() == SosisonEntryType.STRING) {
				return new DungeonRegistryObject(this.keyCodec.read(reader), null);
			} else {
				return new DungeonRegistryObject(null, this.objectCodec.read(reader));
			}
		}

	}
}
