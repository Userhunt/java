package net.w3e.wlib.dungeon.layers.interfaces;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.SerializeOnlyJsonCodec;
import net.skds.lib2.utils.Holders.IntHolder;

@DefaultJsonCodec(DungeonInfoCountHolder.DungeonInfoCountHolderJsonAdapter.class)
public class DungeonInfoCountHolder extends IntHolder {
	public DungeonInfoCountHolder() {}

	public DungeonInfoCountHolder(int value) {
		super(value);
	}

	public final DungeonInfoCountHolder copy() {
		return new DungeonInfoCountHolder(this.getValue());
	}

	public static class DungeonInfoCountHolderJsonAdapter extends SerializeOnlyJsonCodec<DungeonInfoCountHolder> {

		public DungeonInfoCountHolderJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(DungeonInfoCountHolder value, JsonWriter writer) throws IOException {
			if (value.getValue() != -1) {
				writer.writeInt(value.getValue());
			} else {
				writer.writeNull();
			}
		}

		/*@Override
		public final Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return context.deserialize(json, int.class);
		}*/
	}
}
