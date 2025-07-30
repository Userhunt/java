package net.w3e.wlib.dungeon.layers.filter.types;

import java.util.Random;

import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.LayerRange;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilter;

public abstract class LayerRangeRoomFilter extends RoomLayerFilter<Integer> {

	protected LayerRange value = null;

	public LayerRangeRoomFilter(ConfigType<? extends LayerRangeRoomFilter> configType, LayerRange value) {
		super(configType);
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
