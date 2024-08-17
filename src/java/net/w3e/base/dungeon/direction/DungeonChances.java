package net.w3e.base.dungeon.direction;

import java.util.List;
import java.util.Random;

import net.w3e.base.collection.RandomCollection;
import net.w3e.base.math.vector.WDirection;

public record DungeonChances(int count0, int count1, int count2, int count3, int count4, int count5, int front, int side, int up, int down) {
	public static final DungeonChances INSTANCE = new DungeonChances(1, 1, 0, 0, 0, 0, 1, 0, 0, 0);

	public final DungeonDirections generate(Random random) {
		RandomCollection<Integer> collection = new RandomCollection<>(random);
		collection.add(this.count0, 0);
		collection.add(this.count1, 1);
		collection.add(this.count2, 2);
		collection.add(this.count3, 3);
		collection.add(this.count4, 4);
		collection.add(this.count5, 5);
		Integer value = collection.getRandom();
		if (value == null) {
			return DungeonDirections.EMPTY;
		}
		int i = value.intValue();
		if (i == 0) {
			return DungeonDirections.EMPTY;
		} else if (i == 5) {
			return DungeonDirections.FULL;
		} else {
			return DungeonDirections.with(this, collection, i);
		}
	}

	public final List<WDirection> generate(Random random, WDirection direction) {
		return this.generate(random).directions(direction);
	}
}