package net.w3e.base.dungeon.direction;

import java.util.Random;

import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;

public record DungeonPos(WVector3 pos, WDirection direction, boolean enterance) {
	public static final DungeonPos EMPTY_POS = new DungeonPos();
	public static final DungeonPos EMPTY_ENTERANCE = new DungeonPos(WVector3.EMPTY, true);

	public DungeonPos() {
		this(WVector3.EMPTY);
	}

	public DungeonPos(WVector3 pos) {
		this(pos, false);
	}

	public DungeonPos(WVector3 pos, boolean enterance) {
		this(pos, null, false);
	}

	@Deprecated
	public final WDirection direction() {
		return this.direction;
	}

	public final WDirection getDirection(Random random) {
		if (this.direction != null) {
			return this.direction;
		}
		return switch (random.nextInt(4)) {
			case 0 -> WDirection.EAST;
			case 1 -> WDirection.NORTH;
			case 2 -> WDirection.SOUTH;
			case 3 -> WDirection.WEST;
			default -> null;
		};
	}
}
