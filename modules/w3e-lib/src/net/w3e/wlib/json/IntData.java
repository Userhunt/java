package net.w3e.wlib.json;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

@DefaultJsonCodec(IntData.IntDataJsonAdapter.class)
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

	private static class IntDataJsonAdapter extends AbstractJsonCodec<IntData> {

		private final JsonCodec<IntDataA> reader;

		public IntDataJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			this.reader = this.registry.getCodecIndirect(IntDataA.class);
		}

		@Override
		public void write(IntData value, JsonWriter writer) throws IOException {
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
		public IntData read(JsonReader reader) throws IOException {
			if (reader.nextEntryType() == JsonEntryType.NULL) {
				reader.skipNull();
				return new IntData(0, 0);
			}
			if (reader.nextEntryType() == JsonEntryType.NUMBER) {
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
