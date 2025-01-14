package net.w3e.wlib.dungeon.layers;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.json.IDungeonJsonAdapter;

public class DistanceLayer extends ListLayer<DungeonRoomInfo> implements IDungeonJsonAdapter {

	public static final String TYPE = "distance";
	public static final String KEY = "distance";

	/**
	 * json
	 */
	private DistanceLayer() {
		super(TYPE, null);
	}

	public DistanceLayer(DungeonGenerator generator) {
		super(TYPE, generator);
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
			Vec3I pos = room.pos();
			for (Direction direction : Direction.values()) {
				DungeonRoomCreateInfo next = this.get(pos.addI(direction.getOpposite()));
				if (!next.notExistsOrWall() && !next.isEnterance()) {
					rooms.put(next.room(), distance + 1);
				}
			}
		}
	}

	public static final DistanceLayer example(DungeonGenerator generator) {
		return new DistanceLayer(generator);
	}
}
