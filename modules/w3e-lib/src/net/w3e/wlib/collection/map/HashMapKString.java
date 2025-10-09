package net.w3e.wlib.collection.map;

import net.skds.lib2.io.codec.BuiltinCodecFactory;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.annotation.DefaultCodec;

import java.lang.reflect.Type;
import java.util.HashMap;

@DefaultCodec(HashMapKString.HashMapKStringCodec.class)
public class HashMapKString extends HashMap<String, Object> implements MapK<String> {

	static class HashMapKStringCodec extends BuiltinCodecFactory.MapCodec {

		public HashMapKStringCodec(Type type, CodecRegistry registry) {
			super(HashMapKString.class, new Type[]{String.class, Object.class}, registry);
		}
	}
}
