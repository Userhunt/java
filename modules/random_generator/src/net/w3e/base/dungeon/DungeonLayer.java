package net.w3e.base.dungeon;

import java.util.Random;
import java.util.function.Consumer;

import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;

public abstract class DungeonLayer {

	private final DungeonGenerator generator;

	public DungeonLayer(DungeonGenerator generator) {
		this.generator = generator;
	}

	public abstract void regenerate(boolean b);
	public abstract int generate();
	protected final Random random() {
		return this.generator.random();
	}
	protected final DungeonRoomCreateInfo putOrGet(WVector3 pos) {
		return this.generator.putOrGet(pos);
	}
	protected final DungeonRoomCreateInfo get(WVector3 pos) {
		return this.generator.get(pos);
	}
	protected final void forEach(Consumer<DungeonRoomCreateInfo> function, boolean createIfNotExists) {
		this.generator.forEach(function, createIfNotExists);
	}
	protected final WVector3 dungeonSize() {
		return this.generator.dimension().size();
	}

	public static interface IPathLayer {
		void add(WVector3 pos, WDirection direction);
	}
}
