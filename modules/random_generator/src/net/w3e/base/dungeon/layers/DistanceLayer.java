package net.w3e.base.dungeon.layers;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;

public class DistanceLayer extends DungeonLayer implements IListLayer {

	private final List<DungeonRoomInfo> enterances = new ArrayList<>();
	private int filled = -1;

	public DistanceLayer(DungeonGenerator generator) {
		super(generator);
	}

	@Override
	public final void regenerate(boolean composite) {
		this.enterances.clear();
		this.filled = -1;
	}

	@Override
	public final int generate() {
		if (this.filled == -1) {
			this.forEach(room -> {
				room.room().setDistance(-1);
				if (room.isEnterance()) {
					this.enterances.add(room.room());
				}
			}, false);
			this.filled = this.enterances.size();
			return 1;
		}
		if (!this.enterances.isEmpty()) {
			DungeonRoomInfo enterance = this.enterances.remove(0);
			Object2IntArrayMap<DungeonRoomInfo> rooms = new Object2IntArrayMap<>();
			rooms.put(enterance, 0);
			while (!rooms.isEmpty()) {
				ObjectIterator<Entry<DungeonRoomInfo>> iterator = rooms.object2IntEntrySet().iterator();
				Entry<DungeonRoomInfo> next = iterator.next();
				iterator.remove();
				fill(rooms, next);
			}
		}

		return BMatUtil.round(this.size() * 100f / this.filled);
	}

	private final void fill(Object2IntMap<DungeonRoomInfo> rooms, Object2IntMap.Entry<DungeonRoomInfo> entry) {
		DungeonRoomInfo room = entry.getKey();
		int distance = entry.getIntValue();
		int old = room.getDistance();
		if (old == -1 || old > distance) {
			room.setDistance(distance);
			WVector3 pos = room.pos();
			for (WDirection direction : WDirection.values()) {
				DungeonRoomCreateInfo next = this.get(pos.add(direction.relative));
				if (!next.notExistsOrWall() && !next.isEnterance()) {
					rooms.put(next.room(), distance + 1);
				}
			}
		}
	}

	@Override
	public final int size() {
		return this.filled - this.enterances.size();
	}

	@Override
	public final int filled() {
		return this.filled;
	}

}
