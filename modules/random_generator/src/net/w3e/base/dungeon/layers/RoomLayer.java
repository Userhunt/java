package net.w3e.base.dungeon.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.layers.BaseLayerRange.BaseLayerRangeRoomValues;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.WDirection;

public class RoomLayer<T> extends DungeonLayer implements IListLayer, ISetupLayer {

	public static final String KEY = "room";

	private final List<RoomInfo<T>> roomList = new ArrayList<>();
	private final List<RoomInfo<T>> workList = new ArrayList<>();
	private final List<RoomPoint<T>> list = new ArrayList<>();
	private int filled = -1;
	private Progress progress;

	@SafeVarargs
	public RoomLayer(DungeonGenerator generator, RoomInfo<T>... rooms) {
		this(generator, Arrays.asList(rooms));
	}

	public RoomLayer(DungeonGenerator generator, Collection<RoomInfo<T>> rooms) {
		super(generator);
		this.roomList.addAll(rooms);
		this.roomList.removeIf(RoomInfo::notValid);
	}
	
	@Override
	public final void setup(DungeonRoomInfo room) {
		room.data().put(KEY, null);
	}

	@Deprecated
	@Override
	public final void regenerate(boolean composite) {
		this.list.clear();
		this.filled = -1;

		this.progress = Progress.createArray;

		this.workList.clear();
		this.roomList.stream().map(RoomInfo::copy).forEach(this.workList::add);
	}

	@Deprecated
	@Override
	public int generate() {
		Progress prevProgress = this.progress;
		float i = 0;
		if (this.progress == Progress.createArray) {
			this.filled = 0;
			this.forEach(room -> {
				if (!room.isWall()) {
					if (room.isEnterance()) {
						this.list.add(new RoomPoint<>(room.room()));
					}
					this.filled++;
				}
			});
			this.progress = Progress.fillRooms;
			i = 100;
		} else if (this.progress == Progress.fillRooms) {
			this.progress = Progress.finalizeRooms;
			i = 100;
		} else if (this.progress == Progress.finalizeRooms) {

			i = 100;
		}

		float partScale = 100f / Progress.values().length;

		return BMatUtil.round(prevProgress.ordinal() * partScale + i * partScale / 100);
	}

	private enum Progress {
		createArray,
		fillRooms,
		finalizeRooms
		;
	}

	@Override
	public final int size() {
		return this.filled - this.list.size();
	}

	@Override
	public final int filled() {
		return this.filled;
	}

	public record RoomPoint<T>(DungeonRoomInfo room, List<RoomInfo<T>> variants) {
		public RoomPoint(DungeonRoomInfo room) {
			this(room, new ArrayList<>());
		}
	}

	@Deprecated
	public static final RoomLayer<String> example(DungeonGenerator generator) {
		

		return new RoomLayer<>(generator);
	}

	public record RoomInfo<T>(Object2BooleanArrayMap<WDirection> directions, BaseLayerRange baseLayerRange, T value, boolean enterance, int[] count) {

		public RoomInfo(Object2BooleanArrayMap<WDirection> directions, BaseLayerRange baseLayerRange, T value, boolean enterance, int count) {
			this(directions, baseLayerRange, value, enterance, new int[]{count});
		}
		public RoomInfo(Object2BooleanArrayMap<WDirection> directions, BaseLayerRange baseLayerRange, boolean enterance, T value) {
			this(directions, baseLayerRange, value, enterance, -1);
		}

		public final boolean notValid() {
			return this.directions.isEmpty() || this.baseLayerRange.notValid();
		}

		public final boolean test(DungeonRoomInfo room) {
			return this.baseLayerRange.test(new BaseLayerRangeRoomValues(room));
		}

		public final boolean substract() {
			return this.count[0] >= 0 && this.count[0]-- <= 0;
		}

		public final RoomInfo<T> copy() {
			return new RoomInfo<T>(this.directions, this.baseLayerRange, this.value, enterance, this.count[0]);
		}
	}
}
