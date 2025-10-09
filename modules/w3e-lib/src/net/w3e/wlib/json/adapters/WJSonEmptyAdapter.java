package net.w3e.wlib.json.adapters;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.nulls.NullCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

public class WJSonEmptyAdapter extends NullCodec {

	public WJSonEmptyAdapter(Type type, CodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final Object read(UniversalReader reader) throws IOException {
		if (reader.nextEntryType() != SosisonEntryType.NULL) {
			throw new ParseException("non null empty data");
		}
		reader.skipNull();
		return create();
	}

	protected Object create() {
		return null;
	}
}
