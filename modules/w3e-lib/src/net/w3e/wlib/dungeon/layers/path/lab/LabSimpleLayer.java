package net.w3e.wlib.dungeon.layers.path.lab;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.ArrayUtils;
import net.w3e.lib.TFNStateEnum;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.direction.DungeonChances;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.json.WJsonBuilder;

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
				found = addRoom(this.point.getPos());
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
		this.addRoom(this.point.getPos());
		this.roomCount = rooms.size();
		return rooms;
	}

	private TFNStateEnum addRoom(Vec3I pos) {
		for (Direction dir : Direction.randomAll(this.random())) {
			if (dir == this.direction || this.point.isHardConnect(dir)) {
				//log.debug("connected " + pos + " to " + pos.addI(dir) + " prev direction " + this.direction);
				continue;
			}
			DungeonRoomCreateInfo info = this.get(pos.addI(dir));
			if (info.isInside() && info.isWall()) {
				DungeonRoomInfo room = info.room();
				room.setWall(false);
				room.setHardConnection(dir.getOpposite(), true);
				this.point.setHardConnection(dir, true);
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
		room.setSoftConnections(this.connectionChances.generate(random(), this.direction), true);
	}

	protected abstract void generateNextPathPoint();

	protected abstract static class LabSimpleLayerData<T extends LabSimpleLayer> implements WJsonBuilder<T> {
		private int stepCount;
		private DungeonChances directionChances = DungeonChances.INSTANCE;
		@Override
		public final T build() {
			this.lessThan(this.stepCount, "stepCount");
			this.nonNull(this.directionChances, "directionChances");
			return this.build(this.stepCount, this.directionChances);
		}

		protected abstract T build(int stepCount, DungeonChances directionChances);
	}
}
