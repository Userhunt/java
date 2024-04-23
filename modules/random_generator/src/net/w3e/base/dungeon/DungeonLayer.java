package net.w3e.base.dungeon;

import java.util.Random;

import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.math.vector.WVector3;

public abstract class DungeonLayer<T> {

	private final DungeonGenerator<T> generator;

	public DungeonLayer(DungeonGenerator<T> generator) {
		this.generator = generator;
	}

	public abstract void regenerate();
	public abstract int generate();
	protected final Random random() {
		return this.generator.random();
	}
	protected final DungeonRoomCreateInfo<T> put(WVector3 pos) {
		return this.generator.put(pos);
	}
	protected final DungeonRoomCreateInfo<T> get(WVector3 pos) {
		return this.generator.get(pos);
	}
}
