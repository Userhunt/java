package net.w3e.wlib.dungeon.room;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.codec.annotation.SkipSerialization;

public class DungeonRoomBaseData implements Cloneable {

	@Setter
	@Getter
	@SkipSerialization(defaultFloat = Float.POSITIVE_INFINITY)
	private float temperature = Float.POSITIVE_INFINITY;

	@Setter
	@Getter
	@SkipSerialization(defaultFloat = Float.POSITIVE_INFINITY)
	private float wet = Float.POSITIVE_INFINITY;

	@Setter
	@Getter
	@SkipSerialization(defaultFloat = Float.POSITIVE_INFINITY)
	private float variant = Float.POSITIVE_INFINITY;

	@Setter
	@Getter
	@SkipSerialization(defaultFloat = Float.POSITIVE_INFINITY)
	private float difficulty = Float.POSITIVE_INFINITY;

	public final DungeonRoomBaseData copyFrom(DungeonRoomBaseData data) {
		this.temperature = data.temperature;
		this.wet = data.wet;
		this.variant = data.variant;
		this.difficulty = data.difficulty;
		return this;
	}

	@Override
	public DungeonRoomBaseData clone() {
		return new DungeonRoomBaseData().copyFrom(this);
	}
}
