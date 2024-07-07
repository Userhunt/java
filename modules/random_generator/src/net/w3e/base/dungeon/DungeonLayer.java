package net.w3e.base.dungeon;

import java.util.Random;
import java.util.function.Consumer;

import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;

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
	protected final DungeonRoomCreateInfo putOrGet(WVector3I pos) {
		return this.generator.putOrGet(pos);
	}
	protected final DungeonRoomCreateInfo get(WVector3I pos) {
		return this.generator.get(pos);
	}
	protected final void forEach(Consumer<DungeonRoomCreateInfo> function) {
		this.forEach(function, false);
	}
	protected final void forEach(Consumer<DungeonRoomCreateInfo> function, boolean createIfNotExists) {
		this.generator.forEach(function, createIfNotExists);
	}
	protected final WVector3I dungeonSize() {
		return this.generator.dimension().size();
	}

	public static interface IPathLayer {
		void add(WVector3I pos, WDirection direction);
	}
}
