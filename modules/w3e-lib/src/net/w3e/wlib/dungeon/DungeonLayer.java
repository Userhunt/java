package net.w3e.wlib.dungeon;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;
import net.skds.lib2.utils.json.ConfigType;
import net.skds.lib2.utils.json.TypedConfig;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.json.DungeonJsonAdapters;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.dungeon.layers.roomvalues.AbstractLayerRoomValues;
import net.w3e.wlib.dungeon.layers.roomvalues.BaseLayerRoomRange;

public abstract class DungeonLayer implements TypedConfig {

	protected transient final ConfigType<?> configType;
	private final transient DungeonGenerator generator;

	protected DungeonLayer(DungeonGenerator generator) {
		this.configType = DungeonJsonAdapters.INSTANCE.getConfigType(this.keyName());
		this.generator = generator;
	}

	protected abstract String keyName();

	@Override
	public final ConfigType<?> getConfigType() {
		return this.configType;
	}

	public abstract DungeonLayer withDungeon(DungeonGenerator generator);
	public abstract void regenerate(boolean composite) throws DungeonException;
	public abstract int generate() throws DungeonException;
	public void rotate(Direction rotation, DungeonRoomInfo room, Map<Direction, Direction> wrapRotation) throws DungeonException {}

	protected final Random random() {
		return this.generator.random();
	}
	protected final int random100() {
		return this.random().nextInt(100) + 1;
	}
	protected final void assertInside(Vec3I pos) throws DungeonException {
		if (!this.generator.testDimension(pos)) {
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
	protected final AbstractLayerRoomValues<BaseLayerRoomRange> getRoomValues(DungeonRoomInfo room) {
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
