package net.w3e.wlib.json.adapters;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.skds.lib2.utils.json.JsonUtils;

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

	public static class IntDataAdapter extends TypeAdapter<IntData> {
		@Override
		public final void write(JsonWriter out, IntData value) throws IOException {
			if (value.min == value.max) {
				out.value(value.min);
			} else {
				out.beginObject();
				out.name("min").value(value.min);
				out.name("max").value(value.max);
				out.endObject();
			}
		}

		@Override
		public final IntData read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NUMBER) {
				int data = in.nextInt();
				return new IntData(data, data);
			} else {
				TypeAdapter<IntDataA> adapter = JsonUtils.getGSON().getAdapter(IntDataA.class);
				IntDataA data = adapter.read(in);
				return new IntData(Math.min(data.min, data.max), Math.max(data.min, data.max));
			}
		}
	}

	private static class IntDataA {
		public int min;
		public int max;
	}
}
