package net.w3e.wlib.dungeon;

import java.util.Collection;

import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;
import net.w3e.lib.utils.PackUtil;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.mat.VecUtil;

public record DungeonRoomInfo(Vec3I pos, Vec3I chunk, int[] flags, MapTString data) {

	public static final DungeonRoomInfo create(Vec3I pos, MapTString factory) {
		return create(pos, VecUtil.pos2Chunk(pos), factory);
	}

	public static final DungeonRoomInfo create(Vec3I pos, Vec3I chunk, MapTString factory) {
		return new DungeonRoomInfo(pos, chunk, new int[]{-1, 0}, factory).setWall(true);
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

	public final boolean isentrance() {
		return PackUtil.test(this.flags[1], 1);
	}

	public final DungeonRoomInfo setentrance(boolean value) {
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

	public final void copyFrom(DungeonRoomInfo info) {
		this.flags[0] = info.flags[0];
		this.flags[1] = info.flags[1];
		this.data.putAll(info.data);
	}

	@Override
	public final boolean equals(Object obj) {
		return obj instanceof DungeonRoomInfo room && this.pos.equals(room.pos);
	}
}
