package net.w3e.base.dungeon;

import java.util.Random;
import java.util.function.Consumer;

import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.dungeon.layers.roomvalues.BaseLayerRoomRange;
import net.w3e.base.json.adapters.WTypedJsonAdapter.WJsonKeyHolder;
import net.w3e.base.dungeon.layers.roomvalues.AbstractLayerRoomValues;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;

public abstract class DungeonLayer implements WJsonKeyHolder {

	protected String type;
	private final transient DungeonGenerator generator;

	protected DungeonLayer(DungeonGenerator generator) {
		this.generator = generator;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final <VALUE> VALUE setTypeKey(String type) {
		this.type = type;
		return (VALUE)this;
	}

	public final DungeonLayer withDungeon(DungeonGenerator generator) {
		DungeonLayer instance = this.withDungeonImpl(generator);
		instance.type = type;
		return instance;
	}
	protected abstract DungeonLayer withDungeonImpl(DungeonGenerator generator);
	public abstract void regenerate(WDirection rotation, boolean composite) throws DungeonException;
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

	@FunctionalInterface
	public static interface IPathLayer extends ILayerAdapter<DungeonLayer> {
		void add(WVector3I pos, WDirection direction) throws DungeonException;
		default DungeonLayer withDungeon(DungeonGenerator generator) {
			throw new AssertionError();
		}
	}
}
