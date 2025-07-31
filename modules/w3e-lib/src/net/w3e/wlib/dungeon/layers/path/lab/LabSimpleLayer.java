package net.w3e.wlib.dungeon.layers.path.lab;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.ArrayUtils;
import net.w3e.lib.TFNStateEnum;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.direction.DungeonChances;
import net.w3e.wlib.dungeon.json.ILayerData;

public abstract class LabSimpleLayer extends DungeonLayer {

	protected final int stepCount;
	protected final DungeonChances connectionChances;
	protected transient DungeonRoomInfo point;
	protected transient Direction direction = null;
	private transient int step = 1;
	private transient int roomCount;

	private transient boolean generatePre = true;

	protected LabSimpleLayer(ConfigType<?> configType, DungeonGenerator generator, int stepCount, DungeonChances connectionChances) {
		super(configType, generator);
		this.stepCount = stepCount;
		this.connectionChances = connectionChances;
	}

	@Override
	public void setupLayer(boolean composite) throws DungeonException {}

	@Override
	public final float generate() throws DungeonException {
		if (generatePre) {
			this.generatePre();
			this.generatePre = false;
			return 0.001f;
		}

		if (this.step < roomCount) {
			int stepId = this.stepCount;
			TFNStateEnum found = TFNStateEnum.NOT_STATED;
			while (stepId > 1 && this.point != null && found != TFNStateEnum.FALSE) {
				stepId--;
				found = addRoom(this.point.pos());
			}
			if (found != TFNStateEnum.TRUE) {
				this.point = null;
				generateNextPathPoint();
			}
		}

		if (this.step < this.roomCount) {
			return Math.min(this.step * 1f / this.roomCount, 0.999f);
		}

		return 1;
	}

	protected List<DungeonRoomInfo> generatePre() {
		Vec3I size = dungeonSize().addI(Vec3I.SINGLE);
		List<DungeonRoomInfo> rooms = new ArrayList<>(size.xi() * size.yi() * size.zi());
		this.forEach(room -> {
			rooms.add(room.room());
		}, true);
		this.point = ArrayUtils.getRandom(rooms, this.random());
		this.point.setWall(false);
		this.point.setEntrance(true);
		this.addRoom(this.point.pos());
		this.roomCount = rooms.size();
		return rooms;
	}

	private TFNStateEnum addRoom(Vec3I pos) {
		for (Direction dir : Direction.randomAll(this.random())) {
			if (dir == this.direction || this.point.isConnect(dir, true)) {
				//log.debug("connected " + pos + " to " + pos.addI(dir) + " prev direction " + this.direction);
				continue;
			}
			DungeonRoomCreateInfo info = this.get(pos.addI(dir));
			if (info.isInside() && info.isWall()) {
				DungeonRoomInfo room = info.room();
				room.setWall(false);
				room.setConnection(dir.getOpposite(), true, true);
				this.point.setConnection(dir, true, true);
				this.point = room;
				this.direction = dir.getOpposite();
				this.step++;
				onAddRoom(room);
				return TFNStateEnum.TRUE;
			}
		}
		return TFNStateEnum.FALSE;
	}

	protected void onAddRoom(DungeonRoomInfo room) {
		Objects.requireNonNull(this.direction);
		room.setConnections(this.connectionChances.generate(random(), this.direction), true, false);
	}

	protected abstract void generateNextPathPoint();

	protected abstract static class LabSimpleLayerData<T extends LabSimpleLayer> implements ILayerData<T> {
		private int stepCount;
		private DungeonChances directionChances = DungeonChances.INSTANCE;
		@Override
		public final T withDungeon(DungeonGenerator generator) {
			this.lessThan("stepCount", this.stepCount);
			this.nonNull("directionChances", this.directionChances);
			return this.withDungeon(generator, this.stepCount, this.directionChances);
		}

		protected abstract T withDungeon(DungeonGenerator generator, int stepCount, DungeonChances directionChances);
	}
}
