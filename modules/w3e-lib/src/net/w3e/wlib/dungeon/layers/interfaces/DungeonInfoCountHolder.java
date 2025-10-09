package net.w3e.wlib.dungeon.layers.interfaces;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.Predicate;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.utils.Holders.IntHolder;

@DefaultCodec(DungeonInfoCountHolder.DungeonInfoCountHolderJsonAdapter.class)
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

	public static class DungeonInfoCountHolderJsonAdapter extends AbstractCodec<DungeonInfoCountHolder> {

		public DungeonInfoCountHolderJsonAdapter(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(DungeonInfoCountHolder value, UniversalWriter writer) throws IOException {
			if (value.getValue() > 0) {
				writer.writeInt(value.getValue());
			} else {
				writer.writeNull();
			}
		}

		@Override
		public DungeonInfoCountHolder read(UniversalReader reader) throws IOException {
			if (reader.nextEntryType() == SosisonEntryType.NULL) {
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
