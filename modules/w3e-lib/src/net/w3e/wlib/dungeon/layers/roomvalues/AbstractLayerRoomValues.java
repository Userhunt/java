package net.w3e.wlib.dungeon.layers.roomvalues;

import java.util.Random;

import lombok.AllArgsConstructor;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.terra.DifficultyLayer;
import net.w3e.wlib.dungeon.layers.terra.TemperatureLayer;
import net.w3e.wlib.dungeon.layers.terra.WetLayer;

@AllArgsConstructor
public abstract class AbstractLayerRoomValues<T extends BaseLayerRoomRange> {

	private final int temp;
	private final int wet;
	private final int difficulty;
	private final int distance;

	public AbstractLayerRoomValues(DungeonRoomInfo room) {
		this(
			getOr(room, TemperatureLayer.KEY),
			getOr(room, WetLayer.KEY),
			getOr(room, DifficultyLayer.KEY),
			room.getDistance() != -1 ? room.getDistance() : Integer.MIN_VALUE
		);
	}

	public final boolean test(Random random, T values) {
		if (values.temp != null && !values.temp.test(this.temp)) {
			return false;
		}
		if (values.wet != null && !values.wet.test(this.wet)) {
			return false;
		}
		if (values.difficulty != null && !values.difficulty.test(this.difficulty)) {
			return false;
		}
		if (values.distance != null && !values.distance.test(this.distance)) {
			return false;
		}
		if (values.chance != null && !values.chance.test(random.nextInt(100) + 1)) {
			return false;
		}
		return testImpl(values);
	}

	protected abstract boolean testImpl(T values);

	protected static final int getOr(DungeonRoomInfo room, String key) {
		return getOr(room, key, Integer.MIN_VALUE);
	}

	protected static final int getOr(DungeonRoomInfo room, String key, int notSet) {
		MapTString data = room.data();
		if (data.containsKey(key)) {
			int value = data.getInt(key);
			if (value != Integer.MIN_VALUE) {
				return value;
			}
		}
		return Integer.MIN_VALUE;
	}

}
