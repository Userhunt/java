package net.w3e.wlib.dungeon;

import java.util.Random;

import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;

public record DungeonPos(Vec3I pos, Direction direction, boolean enterance) {
	public static final DungeonPos EMPTY_POS = new DungeonPos();
	public static final DungeonPos EMPTY_ENTERANCE = new DungeonPos(Vec3I.ZERO, true);

	public DungeonPos() {
		this(Vec3I.ZERO);
	}

	public DungeonPos(Vec3I pos) {
		this(pos, false);
	}

	public DungeonPos(Vec3I pos, boolean enterance) {
		this(pos, null, enterance);
	}

	@Deprecated
	public final Direction direction() {
		return this.direction;
	}

	public final Direction getDirection(Random random) {
		if (this.direction != null) {
			return this.direction;
		}
		return switch (random.nextInt(4)) {
			case 0 -> Direction.EAST;
			case 1 -> Direction.NORTH;
			case 2 -> Direction.SOUTH;
			case 3 -> Direction.WEST;
			default -> null;
		};
	}
}
