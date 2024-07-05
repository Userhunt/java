package net.w3e.base.dungeon.layers;

import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.layers.terra.DifficultyLayer;
import net.w3e.base.dungeon.layers.terra.TemperatureLayer;
import net.w3e.base.dungeon.layers.terra.WetLayer;

public record BaseLayerRange(LayerRange temp, LayerRange wet, LayerRange difficulty, LayerRange distance) {

	public final boolean notValid() {
		if (this.temp != null && this.temp.notValid()) {
			return true;
		}
		if (this.wet != null && (this.wet.notValid() || this.wet.min() < 0 || this.wet.max() > 100)) {
			return true;
		}
		if (this.difficulty != null && this.difficulty.notValid()) {
			return true;
		}
		if (this.distance != null && this.distance.notValid()) {
			return true;
		}
		return false;
	}
	
	public final boolean test(BaseLayerRangeRoomValues values) {
		if (this.temp != null && !this.temp.test(values.temp)) {
			return false;
		}
		if (this.wet != null && !this.wet.test(values.wet)) {
			return false;
		}
		if (this.difficulty != null && !this.difficulty.test(values.difficulty)) {
			return false;
		}
		if (this.distance != null && !this.distance.test(values.distance)) {
			return false;
		}
		return true;
	}

	public record BaseLayerRangeRoomValues(int temp, int wet, int difficulty, int distance) {
		public BaseLayerRangeRoomValues(DungeonRoomInfo room) {
			this(
				getOr(room, TemperatureLayer.KEY),
				getOr(room, WetLayer.KEY),
				getOr(room, DifficultyLayer.KEY),
				room.getDistance() != -1 ? room.getDistance() : Integer.MIN_VALUE
			);
		}

		
		private static final int getOr(DungeonRoomInfo room, String key) {
			return getOr(room, key, Integer.MIN_VALUE);
		}

		private static final int getOr(DungeonRoomInfo room, String key, int notSet) {
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
}
