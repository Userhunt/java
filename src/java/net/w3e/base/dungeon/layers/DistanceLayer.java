package net.w3e.base.dungeon.layers;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;

public class DistanceLayer extends ListLayer<DungeonRoomInfo> implements ILayerAdapter<DistanceLayer> {

	public static final DistanceLayer INSTANCE = new DistanceLayer(null);

	public DistanceLayer(DungeonGenerator generator) {
		super(generator);
	}

	@Override
	public final DistanceLayer withDungeon(DungeonGenerator generator) {
		return new DistanceLayer(generator);
	}

	@Override
	public final int generate() {
		if (this.filled == -1) {
			this.generateList(room -> {
				room.room().setDistance(-1);
				return room.isEnterance() ? GenerateListHolder.success(room.room()) : GenerateListHolder.fail();
			});
			return 1;
		}
		if (!this.list.isEmpty()) {
			DungeonRoomInfo enterance = this.list.remove(0);
			Object2IntArrayMap<DungeonRoomInfo> rooms = new Object2IntArrayMap<>();
			rooms.put(enterance, 0);
			while (!rooms.isEmpty()) {
				ObjectIterator<Entry<DungeonRoomInfo>> iterator = rooms.object2IntEntrySet().iterator();
				Entry<DungeonRoomInfo> next = iterator.next();
				iterator.remove();
				fill(rooms, next);
			}
		}

		return this.progress();
	}

	private final void fill(Object2IntMap<DungeonRoomInfo> rooms, Object2IntMap.Entry<DungeonRoomInfo> entry) {
		DungeonRoomInfo room = entry.getKey();
		int distance = entry.getIntValue();
		int old = room.getDistance();
		if (old == -1 || old > distance) {
			room.setDistance(distance);
			WVector3I pos = room.pos();
			for (WDirection direction : WDirection.values()) {
				DungeonRoomCreateInfo next = this.get(pos.add(direction.getRelative()));
				if (!next.notExistsOrWall() && !next.isEnterance()) {
					rooms.put(next.room(), distance + 1);
				}
			}
		}
	}
}
