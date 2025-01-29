package net.w3e.wlib.dungeon.layers.filter.types;

import java.util.Random;

import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.LayerRange;

public class RandomRangeRoomFilter extends LayerRangeRoomFilter {

	public static final String KEY = "random";

	public RandomRangeRoomFilter() {
		this(null);
	}
	
	public RandomRangeRoomFilter(LayerRange range) {
		super(JSON_MAP.RANDOM, range);
	}

	@Override
	protected final boolean testValue(Random random, Integer value) {
		return this.value.test(random.nextInt(100) + 1);
	}

	@Override
	public final Integer get(DungeonRoomInfo room) {
		return null;
	}
}
