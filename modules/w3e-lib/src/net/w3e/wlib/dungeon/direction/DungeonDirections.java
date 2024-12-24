package net.w3e.wlib.dungeon.direction;

import java.util.ArrayList;
import java.util.List;

import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Direction.Axis;
import net.w3e.wlib.collection.RandomCollection;

public record DungeonDirections(boolean forwad, boolean left, boolean right, boolean up, boolean down) {
	public static final DungeonDirections EMPTY = new DungeonDirections(false, false, false, false, false);
	public static final DungeonDirections FULL = new DungeonDirections(true, true, true, true, true);

	public final DungeonDirections withForward() {
		return new DungeonDirections(true, this.left, this.right, this.up, this.down);
	}

	public final DungeonDirections withLeft() {
		return new DungeonDirections(this.forwad, true, this.right, this.up, this.down);
	}

	public final DungeonDirections withRight() {
		return new DungeonDirections(this.forwad, this.left, true, this.up, this.down);
	}

	public final DungeonDirections withUp() {
		return new DungeonDirections(this.forwad, this.left, this.right, true, this.down);
	}

	public final DungeonDirections withDown() {
		return new DungeonDirections(this.forwad, this.left, this.right, this.up, true);
	}

	public static final DungeonDirections with(DungeonChances chances, RandomCollection<Integer> collection, int count) {
		DungeonDirections direction = DungeonDirections.EMPTY;
		collection.clear();
		collection.add(chances.front() * 2, 0);
		collection.add(chances.side(), 1);
		collection.add(chances.side(), 2);
		collection.add(chances.up(), 3);
		collection.add(chances.down(), 4);
		for (int i = 0; i < count && !collection.isEmpty(); i++) {
			Integer id = collection.remove();
			direction = switch(id) {
				case 0 -> direction.withForward();
				case 1 -> direction.withLeft();
				case 2 -> direction.withRight();
				case 3 -> direction.withUp();
				case 4 -> direction.withDown();
				default -> direction;
			};
		}
		return direction;
	}

	public final List<Direction> directions(Direction direction) {
		List<Direction> list = new ArrayList<>();
		if (this.forwad) {
			list.add(direction);
		}
		if (this.left) {
			list.add(direction.rotateCounterclockwise(Axis.Y));
		}
		if (this.right) {
			list.add(direction.rotateClockwise(Axis.Y));
		}
		if (this.up) {
			list.add(Direction.UP);
		}
		if (this.down) {
			list.add(Direction.DOWN);
		}
		return list;
	}
}
