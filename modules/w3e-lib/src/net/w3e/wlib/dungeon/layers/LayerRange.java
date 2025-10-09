package net.w3e.wlib.dungeon.layers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Random;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalDeserializer;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.TransientComponent;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

@DefaultCodec(LayerRange.LayerRangeJsonCodec.class)
public record LayerRange(int min, int max, @TransientComponent int range) implements Comparable<LayerRange> {

	public static final LayerRange ZERO = new LayerRange(0, 0, 0);
	public static final LayerRange ONE = new LayerRange(1, 1, 0);

	public LayerRange(int min, int max) {
		this(min, max, max - min);
	}
	public final boolean notValid() {
		return this.max < this.min;
	}
	public final boolean notValid(int min, int max) {
		return this.max < this.min || this.min < min || this.max > max;
	}

	/*public final int range() {
		return this.max - min;
	}*/

	public final int random(Random random) {
		int range = this.range();
		if (range == 0) {
			return this.min;
		}
		return random.nextInt(range) + this.min;
	}

	public boolean test(int value) {
		return value == Integer.MIN_VALUE || (value >= this.min && value <= this.max);
	}

	@Override
	public int compareTo(LayerRange range) {
		return Integer.compare(this.min, range.min);
	}

	public static final LayerRange randomize(Random random, int min, int max) {
		LayerRange range = new LayerRange(min, max);
		if (range.notValid()) {
			return new LayerRange(0, 0);
		}
		int a = range.random(random);
		int b = range.random(random);
		return new LayerRange(Math.min(a, b), Math.max(a, b));
	}

	static class LayerRangeJsonCodec extends AbstractCodec<LayerRange> {

		private final UniversalDeserializer<LayerRangeData> reader;

		public LayerRangeJsonCodec(Type type, CodecRegistry registry) {
			super(type, registry);
			reader = registry.getDeserializerIndirect(LayerRangeData.class);
		}

		@Override
		public void write(LayerRange value, UniversalWriter writer) throws IOException {
			if (value.range == 0) {
				writer.writeInt(value.min);
			} else {
				if (value.min != MIN || value.max != MAX) {
					writer.beginObject();
					if (value.min != MIN) writer.writeInt("min", value.min);
					if (value.max != MAX) writer.writeInt("max", value.max);
					writer.endObject();
				} else {
					writer.writeNull();
				}
			}
		}

		@Override
		public LayerRange read(UniversalReader reader) throws IOException {
			if (reader.nextEntryType() == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			if (reader.nextEntryType().isNumber()) {
				int value = reader.readInt();
				return new LayerRange(value, value, 0);
			} else if (reader.nextEntryType() == SosisonEntryType.BEGIN_OBJECT) {
				LayerRangeData data = this.reader.read(reader);
				return new LayerRange(data.min, data.max);
			} else {
				throw new ParseException("cant read LayerRange");
			}
		}

		private static class LayerRangeData {
			private int min = MIN;
			private int max = MAX;
		}

		private static final int MIN = 0;
		private static final int MAX = Integer.MAX_VALUE;
	}
}

