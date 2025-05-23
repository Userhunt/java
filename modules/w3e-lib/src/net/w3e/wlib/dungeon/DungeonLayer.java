package net.w3e.wlib.dungeon;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;
import net.w3e.wlib.dungeon.json.DungeonJsonLayerAdapters;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.layers.filter.RoomLayerFilterValues;
import net.w3e.wlib.json.WJsonRegistryElement;
import net.w3e.wlib.json.WJsonTypedTypeAdapter;

public abstract class DungeonLayer extends WJsonRegistryElement {

	protected static final DungeonJsonLayerAdapters JSON_MAP = DungeonJsonAdapters.INSTANCE.layerAdapters;

	private final transient DungeonGenerator generator;

	protected DungeonLayer(WJsonTypedTypeAdapter<?> configType, DungeonGenerator generator) {
		super(configType);
		this.generator = generator;
	}

	public abstract DungeonLayer withDungeon(DungeonGenerator generator);
	public abstract void setupLayer(boolean composite) throws DungeonException;
	public abstract float generate() throws DungeonException;
	public void rotate(Direction rotation, DungeonRoomInfo room, Map<Direction, Direction> wrapRotation) throws DungeonException {}

	protected final Random random() {
		return this.generator.random();
	}
	protected final int random100() {
		return this.random().nextInt(100) + 1;
	}
	protected final boolean testIsInside(Vec3I pos) throws DungeonException {
		return this.generator.testPosIsInside(pos);
	}
	protected final void assertIsInside(Vec3I pos) throws DungeonException {
		if (!this.testIsInside(pos)) {
			throw new DungeonException("Something went wrong. Pos is not insisde dungeon " + pos.toString());
		}
	}
	protected final DungeonRoomCreateInfo putOrGet(Vec3I pos) {
		return this.generator.putOrGet(pos);
	}
	protected final DungeonRoomCreateInfo get(Vec3I pos) {
		return this.generator.get(pos);
	}
	protected final DungeonRoomCreateInfo removeRoom(Vec3I pos) {
		return this.generator.removeRoom(pos);
	}
	protected final RoomLayerFilterValues getRoomValues(DungeonRoomInfo room) {
		return this.generator.getRoomValues(room);
	}
	protected final void forEach(Consumer<DungeonRoomCreateInfo> function) {
		this.forEach(function, false);
	}
	protected final void forEach(Consumer<DungeonRoomCreateInfo> function, boolean createIfNotExists) {
		this.generator.forEach(function, createIfNotExists);
	}
	protected final void rotateDimension(Direction rotation) {
		this.generator.dimension().rotate(rotation);
	}
	protected final Vec3I dungeonSize() {
		return this.generator.dimension().size();
	}
	protected final List<DungeonLayer> layers() {
		return this.generator.layers();
	}

	@FunctionalInterface
	public static interface IPathLayer extends ILayerData<DungeonLayer> {
		void add(Vec3I pos, Direction direction) throws DungeonException;
		default DungeonLayer withDungeon(DungeonGenerator generator) {
			throw new AssertionError();
		}
	}
}
