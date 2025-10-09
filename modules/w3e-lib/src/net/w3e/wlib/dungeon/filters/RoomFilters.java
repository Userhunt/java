package net.w3e.wlib.dungeon.filters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.BuiltinCodecFactory.CollectionCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

@DefaultCodec(RoomFilters.JCodec.class)
public class RoomFilters extends ArrayList<RoomFilter> {

	public static final RoomFilters NULL = new RoomFilters();

	public boolean test(Random random, DungeonRoomInfo room) {
		for (RoomFilter filter : this) {
			if (!filter.testValue(random, room)) {
				return false;
			}
		}
		return true;
	}

	public boolean notValid() {
		for (RoomFilter filter : this) {
			if (filter.notValid()) {
				return true;
			}
		}
		return false;
	}

	static class JCodec extends CollectionCodec {
		public JCodec(Type tClass, CodecRegistry registry) {
			super(RoomFilters.class, registry.getCodecIndirect(RoomFilter.class), registry, RoomFilters::new);
		}
	}

	public static class RoomFiltersNullPredicate implements Predicate<RoomFilters> {
		@Override
		public boolean test(RoomFilters t) {
			return t.isEmpty();
		}
	}
}
