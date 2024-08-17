package net.w3e.base.dungeon;

import java.util.Random;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.dungeon.layers.roomvalues.AbstractLayerRoomValues;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WBoxI;
import net.w3e.base.math.vector.i.WVector3I;

@AllArgsConstructor
public abstract class DungeonLayer {

	private final DungeonGenerator generator;

	public abstract DungeonLayer withDungeon(DungeonGenerator generator);
	public abstract void regenerate(boolean b) throws DungeonException;
	public abstract int generate() throws DungeonException;

	protected final Random random() {
		return this.generator.random();
	}
	protected final int random100() {
		return this.random().nextInt(100) + 1;
	}
	protected final void assertInside(WVector3I pos) throws DungeonException {
		if (!this.generator.testDimension(pos)) {
			throw new DungeonException("Something went wrong. Pos is not insisde dungeon " + pos.toStringArray());
		}
	}
	protected final boolean isOnFrame(WVector3I pos) {
		WBoxI box = this.generator.dimension().copy();
		System.out.println(box);
		System.out.println(box.expand(-1, -1, -1));
		return this.generator.dimension().copy().expand(-1, -1, -1).contains(pos);
	}
	protected final DungeonRoomCreateInfo putOrGet(WVector3I pos) {
		return this.generator.putOrGet(pos);
	}
	protected final DungeonRoomCreateInfo get(WVector3I pos) {
		return this.generator.get(pos);
	}
	protected final DungeonRoomCreateInfo removeRoom(WVector3I pos) {
		return this.generator.removeRoom(pos);
	}
	protected final AbstractLayerRoomValues<BaseLayerRoomRange> getRoomValues(DungeonRoomInfo room) {
		return this.generator.getRoomValues(room);
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
		void add(WVector3I pos, WDirection direction) throws DungeonException;
	}
}
