package net.w3e.base.api.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

public class Packet {

	protected Packet() {}

	public final ByteBuffer write(int i) {
		JsonObject json = new JsonObject();
		json.addProperty("c", getClass().getSimpleName());
		PacketBuffer buffer = new PacketBuffer();
		write(buffer);
		json.addProperty("i", i);
		json.addProperty("m", buffer.message());

		return ByteBuffer.wrap(json.toString().getBytes());
	}

	@Deprecated
	protected void write(PacketBuffer buffer) {
		buffer.writeString("empty@" + hashCode());
	}

	public final Packet read(String json) {
		json = json.substring(1, json.length() - 1);
		List<String> list = new ArrayList<String>(Arrays.asList(json.split(", ")));
		return read(new PacketBuffer(list));
	}

	@Deprecated
	protected Packet read(PacketBuffer buffer) {
		return this;
	}

	@Deprecated
	public void run() {
		
	}

	public static record PacketHandler(String clazz, int i, String message) implements Comparable<PacketHandler> {
		@Override
		public int compareTo(PacketHandler o) {
			return this.i - o.i;
		}
	}

	protected class PacketBuffer {

		private final List<String> buffer;

		public PacketBuffer() {
			this(new ArrayList<>());
		}

		private PacketBuffer(List<String> list) {
			this.buffer = list;
		}

		public final void writeInt(int i) {
			this.buffer.add(String.valueOf(i));
		}

		public final void writeString(String string) {
			this.buffer.add(string);
		}

		public final int readInt() {
			String key = this.buffer.remove(0);
			return Integer.valueOf(key);
		}

		public final String readString() {
			return this.buffer.remove(0);
		}

		private final String message() {
			return this.toString();
		}

		@Override
		public String toString() {
			return buffer.toString();
		}
	}
}
