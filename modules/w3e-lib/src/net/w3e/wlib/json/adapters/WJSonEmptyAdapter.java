package net.w3e.wlib.json.adapters;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.nulls.NullJsonCodec;
import net.skds.lib2.io.json.exception.JsonReadException;

public class WJSonEmptyAdapter extends NullJsonCodec {

	public WJSonEmptyAdapter(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final Object read(JsonReader reader) throws IOException {
		if (reader.nextEntryType() != JsonEntryType.NULL) {
			throw new JsonReadException("non null empty data");
		}
		reader.skipNull();
		return create();
	}

	protected Object create() {
		return null;
	}
}
