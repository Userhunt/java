package net.w3e.wlib.dungeon.filters;

import java.util.Objects;

import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.mat.FastMath;
import net.w3e.wlib.dungeon.layers.LayerRange;

public abstract class RoomFilterRange extends RoomFilter {

	protected final LayerRange value;

	/**
	 * json
	 */
	protected RoomFilterRange(ConfigType<? extends RoomFilterRange> configType) {
		super(configType);
		this.value = LayerRange.ZERO;
	}

	protected RoomFilterRange(ConfigType<? extends RoomFilterRange> configType, LayerRange value) {
		super(configType);
		Objects.requireNonNull(value, "value");
		this.value = value;
	}

	@Override
	public boolean notValid() {
		return this.value == null || this.value.notValid();
	}

	protected final boolean testValue(float value) {
		if (value == Float.POSITIVE_INFINITY) {
			return true;
		}
		return this.value.test(FastMath.round(value));
	}

	protected final boolean testValue(int value) {
		return this.value.test(value);
	}
}
