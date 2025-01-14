package net.w3e.wlib.dungeon.layers.interfaces;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.Predicate;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.utils.Holders.IntHolder;

@DefaultJsonCodec(DungeonInfoCountHolder.DungeonInfoCountHolderJsonAdapter.class)
public class DungeonInfoCountHolder extends IntHolder {

	public static final DungeonInfoCountHolder NULL = new DungeonInfoCountHolder(-1) {
		@Override
		public final void setValue(int value) {}
		@Override
		public final int increment(int inc) {
			return this.value;
		}
		@Override
		public final int decrement(int inc) {
			return this.value;
		}
	};

	public DungeonInfoCountHolder() {}

	public DungeonInfoCountHolder(int value) {
		super(value);
	}

	public final DungeonInfoCountHolder copy() {
		if (this.value < 0) {
			return DungeonInfoCountHolder.NULL;
		}
		return new DungeonInfoCountHolder(this.getValue());
	}

	public static class DungeonInfoCountHolderJsonAdapter extends AbstractJsonCodec<DungeonInfoCountHolder> {

		public DungeonInfoCountHolderJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(DungeonInfoCountHolder value, JsonWriter writer) throws IOException {
			if (value.getValue() > 0) {
				writer.writeInt(value.getValue());
			} else {
				writer.writeNull();
			}
		}

		@Override
		public DungeonInfoCountHolder read(JsonReader reader) throws IOException {
			if (reader.nextEntryType() == JsonEntryType.NULL) {
				reader.skipNull();
				return DungeonInfoCountHolder.NULL;
			}
			return new DungeonInfoCountHolder(reader.readInt());
		}
	}

	public static class DungeonInfoCountHolderNullPredicate implements Predicate<DungeonInfoCountHolder> {
		@Override
		public boolean test(DungeonInfoCountHolder t) {
			return t.getValue() < 0;
		}
	}
}
