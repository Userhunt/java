package net.w3e.wlib.dungeon.direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.utils.collection.WeightedPool;
import net.skds.lib2.utils.linkiges.Obj2FloatPair;
import net.skds.lib2.utils.linkiges.Obj2FloatPairRecord;

public record DungeonChances(int count0, int count1, int count2, int count3, int count4, int count5, int front, int side, int up, int down) {
	public static final DungeonChances INSTANCE = new DungeonChances(1, 1, 0, 0, 0, 0, 1, 0, 0, 0);

	public final DungeonDirections generate(Random random) {
		List<Obj2FloatPair<Count>> collection = new ArrayList<>();
		this.add(collection, this.count0, Count.c0);
		this.add(collection, this.count1, Count.c1);
		this.add(collection, this.count2, Count.c2);
		this.add(collection, this.count3, Count.c3);
		this.add(collection, this.count4, Count.c4);
		this.add(collection, this.count5, Count.c5);
		if (collection.isEmpty()) {
			return DungeonDirections.EMPTY;
		}

		int i = new WeightedPool<>(collection).getRandom(random).ordinal();
		if (i == 0) {
			return DungeonDirections.EMPTY;
		} else if (i == 5) {
			return DungeonDirections.FULL;
		} else {
			return DungeonDirections.with(this, random, i);
		}
	}

	private void add(List<Obj2FloatPair<Count>> collection, int wegiht, Count count) {
		if (wegiht <= 0) {
			return;
		}
		collection.add(new Obj2FloatPairRecord<>(wegiht, count));
	}

	private enum Count {
		c0,
		c1,
		c2,
		c3,
		c4,
		c5;
	}

	public final List<Direction> generate(Random random, Direction direction) {
		return this.generate(random).directions(direction);
	}
}