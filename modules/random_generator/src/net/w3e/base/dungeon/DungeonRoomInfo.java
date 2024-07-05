package net.w3e.base.dungeon;

import java.util.Collection;
import java.util.function.Supplier;

import net.w3e.base.PackUtil;
import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;

public record DungeonRoomInfo(WVector3 pos, WVector3 chunk, int[] flags, MapTString data) {

	public static final DungeonRoomInfo create(WVector3 pos, Supplier<MapTString> factory) {
		return create(pos, pos.toChunk(), factory);
	}

	public static final DungeonRoomInfo create(WVector3 pos, WVector3 chunk, Supplier<MapTString> factory) {
		return new DungeonRoomInfo(pos, pos.toChunk(), new int[]{-1, 0}, factory.get()).setWall(true);
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
		return PackUtil.test(this.flags[1], 1);
	}

	public final DungeonRoomInfo setWall(boolean value) {
		this.flags[1] = PackUtil.set(this.flags[1], 1, value);
		return this;
	}

	public final boolean isEnterance() {
		return PackUtil.test(this.flags[1], 2);
	}

	public final DungeonRoomInfo setEnterance(boolean value) {
		this.flags[1] = PackUtil.set(this.flags[1], 2, value);
		return this;
	}

	private final int getConnectionFlag() {
		return this.flags[1];
	}

	private final int setConnectionFlag(int flag) {
		return this.flags[1] = flag;
	}

	private static final int DIRECTION_START = 2;

	private final int direction2Id(WDirection direction, boolean hard) {
		int i = DIRECTION_START + switch (direction) {
			case NORTH -> 1;
			case EAST -> 2;
			case SOUTH -> 3;
			case WEST -> 4;
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

	public final boolean isConnect(WDirection direction) {
		return isConnect(direction, false);
	}

	public final boolean isConnect(WDirection direction, boolean hard) {
		int i = direction2Id(direction, hard);
		if (i != DIRECTION_START) {
			return PackUtil.test(getConnectionFlag(), i);
		} else {
			return false;
		}
	}

	public final DungeonRoomInfo setConnection(WDirection direction, boolean value, boolean hard) {
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

	public final DungeonRoomInfo setConnections(Collection<WDirection> connections, boolean value, boolean hard) {
		for (WDirection connection : connections) {
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
