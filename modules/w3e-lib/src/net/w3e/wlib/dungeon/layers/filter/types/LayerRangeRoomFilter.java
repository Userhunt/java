package net.w3e.wlib.dungeon.layers.filter.types;

import java.util.Random;

import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilter;

public abstract class LayerRangeRoomFilter extends RoomLayerFilter<Integer> {

	protected LayerRange value = null;

	public LayerRangeRoomFilter(String keyName, LayerRange value) {
		super(keyName);
		this.value = value;
	}

	@Override
	public boolean notValid() {
		return this.value != null && this.value.notValid();
	}

	@Override
	protected boolean testValue(Random random, Integer value) {
		return this.value != null && this.value.test(value);
	}

	@Override
	public Integer get(DungeonRoomInfo room) {
		return getOr(room, this.keyName());
	}
}
