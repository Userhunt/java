package net.w3e.wlib.json;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalDeserializer;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.sosison.SosisonEntryType;

@DefaultCodec(IntData.IntDataJsonAdapter.class)
public class IntData {

	public final int min;
	public final int max;

	public IntData(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString() {
		return String.format("{min:%s,max:%s}", min, max);
	}

	static class IntDataJsonAdapter extends AbstractCodec<IntData> {

		private final UniversalDeserializer<IntDataA> reader;

		public IntDataJsonAdapter(Type type, CodecRegistry registry) {
			super(type, registry);
			this.reader = this.registry.getDeserializerIndirect(IntDataA.class);
		}

		@Override
		public void write(IntData value, UniversalWriter writer) throws IOException {
			if (value.min == value.max) {
				writer.writeInt(value.min);
			} else {
				writer.beginObject();
				writer.writeInt("min", value.min);
				writer.writeInt("max", value.max);
				writer.endObject();
			}
		}

		@Override
		public IntData read(UniversalReader reader) throws IOException {
			if (reader.nextEntryType() == SosisonEntryType.NULL) {
				reader.skipNull();
				return new IntData(0, 0);
			}
			if (reader.nextEntryType().isNumber()) {
				int data = reader.readInt();
				return new IntData(data, data);
			} else {
				IntDataA data = this.reader.read(reader);
				return new IntData(Math.min(data.min, data.max), Math.max(data.min, data.max));
			}
		}

		private static class IntDataA {
			public int min;
			public int max;
		}
	}
}
