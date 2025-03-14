package net.w3e.wlib.dungeon.direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Direction.Axis;
import net.skds.lib2.utils.collection.WeightedPool;
import net.skds.lib2.utils.linkiges.Obj2FloatPair;
import net.skds.lib2.utils.linkiges.Obj2FloatPairRecord;

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

	public static final DungeonDirections with(DungeonChances chances, Random random, int count) {
		List<Obj2FloatPair<Direction>> collection = new ArrayList<>();
		add(collection, chances.front() * 2, Direction.FORWARD);
		add(collection, chances.side(), Direction.LEFT);
		add(collection, chances.side(), Direction.RIGHT);
		add(collection, chances.up(), Direction.UP);
		add(collection, chances.down(), Direction.DOWN);

		WeightedPool<Direction> pool = new WeightedPool<>(collection);
		DungeonDirections direction = DungeonDirections.EMPTY;
		for (int i = 0; i < count && !collection.isEmpty(); i++) {
			Direction id = pool.getAndRemoveRandom(random);
			direction = switch(id) {
				case FORWARD -> direction.withForward();
				case LEFT -> direction.withLeft();
				case RIGHT -> direction.withRight();
				case UP -> direction.withUp();
				case DOWN -> direction.withDown();
				default -> direction;
			};
		}
		return direction;
	}

	private static void add(List<Obj2FloatPair<Direction>> collection, int wegiht, Direction count) {
		if (wegiht <= 0) {
			return;
		}
		collection.add(new Obj2FloatPairRecord<>(wegiht, count));
	}

	public final List<Direction> directions(Direction direction) {
		List<Direction> list = new ArrayList<>();
		if (this.forwad) {
			list.add(direction);
		}
		if (direction != Direction.UP && direction != Direction.DOWN) {
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
		}
		return list;
	}
}
