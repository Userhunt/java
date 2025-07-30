package net.w3e.wlib.dungeon.layers;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;

public class DistanceLayer extends ListLayer<DungeonRoomInfo> {

	public static final String TYPE = "distance";
	public static final String KEY = "distance";

	/**
	 * json
	 */
	private DistanceLayer() {
		super(JSON_MAP.DISTANCE, null);
	}

	public DistanceLayer(DungeonGenerator generator) {
		super(JSON_MAP.DISTANCE, generator);
	}

	@Override
	public final DistanceLayer withDungeon(DungeonGenerator generator) {
		return new DistanceLayer(generator);
	}

	@Override
	public final void setupLayer(boolean composite) throws DungeonException {}

	@Override
	public final float generate() {
		if (this.filled == -1) {
			this.generateList(room -> {
				room.room().setDistance(-1);
				return room.isentrance() ? GenerateListHolder.success(room.room()) : GenerateListHolder.fail();
			});
			return 0.001f;
		}
		if (!this.list.isEmpty()) {
			DungeonRoomInfo entrance = this.list.remove(0);
			Object2IntArrayMap<DungeonRoomInfo> rooms = new Object2IntArrayMap<>();
			rooms.put(entrance, 0);
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
				if (!next.notExistsOrWall() && !next.isentrance()) {
					rooms.put(next.room(), distance + 1);
				}
			}
		}
	}

	public static final DistanceLayer example(DungeonGenerator generator) {
		return new DistanceLayer(generator);
	}
}
