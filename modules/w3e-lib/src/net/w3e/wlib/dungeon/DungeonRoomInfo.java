package net.w3e.wlib.dungeon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.lib.utils.PackUtil;
import net.w3e.wlib.collection.CollectionBuilder;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.mat.VecUtil;

public record DungeonRoomInfo(Vec3I pos, Vec3I chunk, int[] flags, MapTString data) {

	public static final DungeonRoomInfo create(Vec3I pos, MapTString factory) {
		return create(pos, VecUtil.pos2Chunk(pos), factory);
	}

	public static final DungeonRoomInfo create(Vec3I pos, Vec3I chunk, MapTString factory) {
		return new DungeonRoomInfo(pos, chunk, new int[]{-1, 0}, new MapTString(factory)).setWall(true);
	}

	@Deprecated
	public final int[] flags() {
		return new int[]{this.flags[0], this.flags[1]};
	}

	public final int getDistance() {
		return this.flags[0];
	}

	public final DungeonRoomInfo setDistance(int distance) {
		this.flags[0] = distance;
		return this;
	}

	public final boolean isWall() {
		return PackUtil.test(this.flags[1], 0);
	}

	public final DungeonRoomInfo setWall(boolean value) {
		this.flags[1] = PackUtil.set(this.flags[1], 0, value);
		return this;
	}

	public final boolean isEntrance() {
		return PackUtil.test(this.flags[1], 1);
	}

	public final DungeonRoomInfo setEntrance(boolean value) {
		this.flags[1] = PackUtil.set(this.flags[1], 1, value);
		return this;
	}

	private final int getConnectionFlag() {
		return this.flags[1];
	}

	private final int setConnectionFlag(int flag) {
		return this.flags[1] = flag;
	}

	private static final int DIRECTION_START = 2;

	private final int direction2Id(Direction direction, boolean hard) {
		int i = DIRECTION_START + switch (direction) {
			case BACKWARD -> 1;
			case LEFT -> 2;
			case FORWARD -> 3;
			case RIGHT -> 4;
			case DOWN -> 5;
			case UP -> 6;
			default -> 0;
		};
		if (i != DIRECTION_START) {
			if (hard) {
				i += 6;
			}
		}
		return i;
	}

	public final boolean isConnect(Direction direction) {
		return isConnect(direction, false);
	}

	public final boolean isConnect(Direction direction, boolean hard) {
		int i = direction2Id(direction, hard);
		if (i != DIRECTION_START) {
			return PackUtil.test(getConnectionFlag(), i);
		} else {
			return false;
		}
	}

	public final int connectCount() {
		return this.connectCount(false);
	}

	public final int connectCount(boolean hard) {
		int count = 0;
		for (Direction direction : Direction.values()) {
			if (this.isConnect(direction, hard)) {
				count++;
			}
		}
		return count;
	}

	public final DungeonRoomInfo setConnection(Direction direction, boolean value, boolean hard) {
		int i = direction2Id(direction, false);
		if (i != DIRECTION_START) {
			int flag = this.getConnectionFlag();
			if (value) {
				flag = this.setConnectionFlag(PackUtil.set(flag, i, true));
				if (hard) {
					flag = this.setConnectionFlag(PackUtil.set(flag, i + 6, true));
				}
			} else {
				if (!hard) {
					flag = this.setConnectionFlag(PackUtil.set(flag, i, false));
				}
				flag = this.setConnectionFlag(PackUtil.set(flag, i + 6, false));
			}
		}
		return this;
	}

	public final DungeonRoomInfo setConnections(Collection<Direction> connections, boolean value, boolean hard) {
		for (Direction connection : connections) {
			this.setConnection(connection, value, hard);
		}
		return this;
	}

	public final List<Direction> getNotConnected() {
		return getNotConnected(Direction.VALUES, true);
	}

	public final List<Direction> getNotConnected(Direction[] directions) {
		return getNotConnected(directions, true);
	}

	public final List<Direction> getNotConnected(Direction[] directions, boolean hard) {
		List<Direction> dirs = new ArrayList<>();
		for (Direction direction : directions) {
			if (!this.isConnect(direction, hard)) {
				dirs.add(direction);
			}
		}
		return dirs;
	}

	public final void copyFrom(DungeonRoomInfo info) {
		this.flags[0] = info.flags[0];
		this.flags[1] = info.flags[1];
		this.data.putAll(info.data);
	}

	@Override
	public final boolean equals(Object obj) {
		return obj instanceof DungeonRoomInfo room && this.pos.equals(room.pos);
	}

	public List<String> displayString() {
		List<String> list = new ArrayList<>();
		list.add(String.format("Pos: %s", this.pos()));
		list.add(String.format("Chunk: %s", this.chunk()));
		list.add(String.format("Distance: %s", this.getDistance()));
		list.add(String.format("IsWall: %s", this.isWall()));
		list.add(String.format("Isentrance: %s", this.isEntrance()));
		list.add(String.format("Connections: %s", CollectionBuilder.list(String.class)
			.add(this.connectionDisplayString(Direction.UP))
			.add(this.connectionDisplayString(Direction.DOWN))
			.add(this.connectionDisplayString(Direction.NORTH))
			.add(this.connectionDisplayString(Direction.SOUTH))
			.add(this.connectionDisplayString(Direction.WEST))
			.add(this.connectionDisplayString(Direction.EAST))
		.removeNull().build()));

		String[] dataArray = JsonUtils.toJson(this.data).replace("\t", "  ").split("\n");
		list.add("Data: " + dataArray[0]);
		for (int i = 1; i < dataArray.length; i++) {
			list.add(dataArray[i]);
		}
		return list;
	}

	private String connectionDisplayString(Direction direction) {
		String name = null;
		if (this.isConnect(direction)) {
			name = direction.getName();
			if (this.isConnect(direction, true)) {
				name = name.toUpperCase();
			}
		}
		return name;
	}

}
