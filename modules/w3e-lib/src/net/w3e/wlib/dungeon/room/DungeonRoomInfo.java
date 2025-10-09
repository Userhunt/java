package net.w3e.wlib.dungeon.room;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.direction.ConnectionState;
import net.w3e.wlib.mat.VecUtil;

public final class DungeonRoomInfo {

	@Getter
	private final Vec3I pos;
	@Getter
	private final transient Vec3I chunk;
	@Getter
	private final DungeonRoomData data = new DungeonRoomData();

	@Getter
	@Setter
	private boolean wall = true;
	@Getter
	@Setter
	private boolean entrance;

	private final EnumMap<Direction, ConnectionState> connections = new EnumMap<>(Direction.class);

	public DungeonRoomInfo(Vec3I pos) {
		this(pos, VecUtil.pos2Chunk(pos));
	}

	public DungeonRoomInfo(Vec3I pos, Vec3I chunk) {
		this.pos = pos;
		this.chunk = chunk;
	}

	public boolean isConnect(Direction direction) {
		return this.connections.getOrDefault(direction, ConnectionState.NONE).isAny();
	}

	public boolean isHardConnect(Direction direction) {
		return this.connections.getOrDefault(direction, ConnectionState.NONE).isHard();
	}

	public boolean isSoftConnect(Direction direction) {
		return this.connections.getOrDefault(direction, ConnectionState.NONE).isSoft();
	}

	public DungeonRoomInfo setConnection(Direction direction, boolean value) {
		if (value) {
			this.connections.put(direction, ConnectionState.BOTH);
		} else {
			this.connections.remove(direction);
		}
		return this;
	}

	public DungeonRoomInfo setHardConnection(Direction direction, boolean value) {
		if (value) {
			this.connections.compute(direction, (_, old) -> old == null ? ConnectionState.HARD : old.add(ConnectionState.HARD));
		} else {
			this.connections.compute(direction, (_, old) -> old == null ? null : old.remove(ConnectionState.HARD));
		}
		return this;
	}

	public DungeonRoomInfo setSoftConnection(Direction direction, boolean value) {
		if (value) {
			this.connections.compute(direction, (_, old) -> old == null ? ConnectionState.SOFT : old.add(ConnectionState.SOFT));
		} else {
			this.connections.compute(direction, (_, old) -> old == null ? null : old.remove(ConnectionState.SOFT));
		}
		return this;
	}

	public DungeonRoomInfo setConnections(Collection<Direction> connections, boolean value) {
		for (Direction connection : connections) {
			this.setConnection(connection, value);
		}
		return this;
	}

	public DungeonRoomInfo setHardConnections(Collection<Direction> connections, boolean value) {
		for (Direction connection : connections) {
			this.setHardConnection(connection, value);
		}
		return this;
	}

	public DungeonRoomInfo setSoftConnections(Collection<Direction> connections, boolean value) {
		for (Direction connection : connections) {
			this.setSoftConnection(connection, value);
		}
		return this;
	}

	public Set<Direction> getNotConnected() {
		return getNotConnected(Direction.VALUES);
	}

	public Set<Direction> getNotConnected(Direction[] directions) {
		Set<Direction> dirs = EnumSet.noneOf(Direction.class);
		for (Direction direction : directions) {
			if (!this.isConnect(direction)) {
				dirs.add(direction);
			}
		}
		return dirs;
	}

	public Set<Direction> getHardNotConnected() {
		return getHardNotConnected(Direction.VALUES);
	}

	public Set<Direction> getHardNotConnected(Direction[] directions) {
		Set<Direction> dirs = EnumSet.noneOf(Direction.class);
		for (Direction direction : directions) {
			if (!this.isHardConnect(direction)) {
				dirs.add(direction);
			}
		}
		return dirs;
	}

	public Set<Direction> getSoftNotConnected() {
		return getSoftNotConnected(Direction.VALUES);
	}

	public Set<Direction> getSoftNotConnected(Direction[] directions) {
		Set<Direction> dirs = EnumSet.noneOf(Direction.class);
		for (Direction direction : directions) {
			if (!this.isSoftConnect(direction)) {
				dirs.add(direction);
			}
		}
		return dirs;
	}

	public int connectCount() {
		int count = 0;
		for (Direction direction : Direction.values()) {
			if (this.isConnect(direction)) {
				count++;
			}
		}
		return count;
	}

	public int connectHardCount() {
		int count = 0;
		for (Direction direction : Direction.values()) {
			if (this.isHardConnect(direction)) {
				count++;
			}
		}
		return count;
	}

	public int connectSoftCount() {
		int count = 0;
		for (Direction direction : Direction.values()) {
			if (this.isSoftConnect(direction)) {
				count++;
			}
		}
		return count;
	}

	public void copyFrom(DungeonRoomInfo value) {
		this.setWall(value.isWall());
		this.setEntrance(value.isEntrance());
		this.connections.putAll(value.connections);
		this.data.copyFrom(value.data);
	}

	public List<String> displayString() {
		List<String> list = new ArrayList<>();
		list.add(String.format("Pos: %s", this.getPos()));
		list.add(String.format("Chunk: %s", this.getChunk()));
		list.add(String.format("Distance: %s", this.getData().getDistance()));
		list.add(String.format("IsWall: %s", this.isWall()));
		list.add(String.format("Isentrance: %s", this.isEntrance()));
		list.add(String.format("Connections: %s", this.connections));

		DungeonRoomData displayData = this.data.clone();
		displayData.setDistance(-1);
		List<String> dataArray = new LinkedList<>(Arrays.asList(
			SosisonUtils.toJson(displayData)
		.replace("\t", "  ").split("\n")));
		list.add("Data: " + dataArray.removeFirst());
		list.addAll(dataArray);
		return list;
	}

}
