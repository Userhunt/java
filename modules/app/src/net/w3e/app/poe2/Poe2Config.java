package net.w3e.app.poe2;

import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.DefaultFile;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.w3e.lib.ITFNStateEnum;
import net.w3e.lib.TFNStateEnum;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static net.skds.lib2.io.codec.BuiltinCodecFactory.parseEnum;

@Getter
@Setter
@DefaultFile("poe2-ritual.json")
@CustomLog
public class Poe2Config {

	private int x = 0;
	private int y = 0;
	private int size = 600;
	private TreeMap<String, ItemClass> items = new TreeMap<>();

	public static Poe2Config read() {
		Poe2Config config = SosisonUtils.readJson(Poe2Config.class);
		if (config == null) {
			config = new Poe2Config();
		}
		return config;
	}

	public void save() {
		SosisonUtils.saveJson(this);
	}

	@Getter
	public static class ItemClass implements PostDeserializeCall {

		private int x = 0;
		private int y = 0;
		private Map<String, ItemState> items = new TreeMap<>();

		@Override
		public void postDeserialized() {
			if (x <= 0) {
				log.warn("x is 0");
				throw new IllegalStateException("x is 0");
			}
			if (y <= 0) {
				log.warn("y is 0");
			}
			Objects.requireNonNull(this.items, "items is null");
			for (Map.Entry<String, ItemState> entry : items.entrySet()) {
				String key = entry.getKey();
				if (key == null) {
					log.warn("key in items is null");
				} else if (key.isBlank()) {
					log.warn("key in items is blank");
				}
				if (entry.getValue() == null) {
					log.warn("value with key " + key + " in items is null");
				}
			}
		}
	}

	@DefaultCodec(ItemState.Codec.class)
	public enum ItemState implements ITFNStateEnum {
		TRUE,
		FALSE,
		NULL,
		;

		@Override
		public TFNStateEnum getAsITFNStateEnum() {
			return switch (this) {
				case TRUE -> TFNStateEnum.TRUE;
				case FALSE -> TFNStateEnum.FALSE;
				case NULL -> TFNStateEnum.NOT_STATED;
			};
		}

		static class Codec extends AbstractCodec<ItemState> {

			public Codec(Type type, CodecRegistry registry) {
				super(type, registry);
			}

			@Override
			public void write(ItemState value, UniversalWriter writer) throws IOException {
				if (value == null || value == NULL) {
					writer.writeNull();
					return;
				}
				writer.writeBoolean(value.isTrue());
			}

			@Override
			public ItemState read(UniversalReader reader) throws IOException {
				SosisonEntryType type = reader.nextEntryType();
				switch (type) {
					case NULL -> {
						reader.skipNull();
						return NULL;
					}
					case STRING -> {
						return parseEnum(reader.readString().toUpperCase(), ItemState.class);
					}
					case BOOLEAN -> {
						return parseEnum(String.valueOf(reader.readBoolean()).toUpperCase(), ItemState.class);
					}
					default -> {
						if (type.isNumber()) {
							int n = reader.readInt();
							ItemState[] values = ItemState.class.getEnumConstants();
							if (n < 0 || n >= values.length) return null;
							return values[n];
						} else
							throw new ParseException("Unexpected token " + type);
					}
				}
			}
		}
	}

}
