package net.w3e.base.dungeon.layers.roomvalues;

import lombok.AllArgsConstructor;
import net.w3e.base.dungeon.layers.LayerRange;

@AllArgsConstructor
public class BaseLayerRoomRange {

	public static final BaseLayerRoomRange NULL = new BaseLayerRoomRange(null, null, null, null, null);

	public final LayerRange temp;
	public final LayerRange wet;
	public final LayerRange difficulty;
	public final LayerRange distance;
	public final LayerRange chance;

	public BaseLayerRoomRange(BaseLayerRoomRange range) {
		this.temp = range.temp;
		this.wet = range.wet;
		this.difficulty = range.difficulty;
		this.distance = range.distance;
		this.chance = range.chance;
	}

	public final boolean notValid() {
		if (this.temp != null && this.temp.notValid()) {
			return true;
		}
		if (this.wet != null && this.wet.notValid(0, 100)) {
			return true;
		}
		if (this.difficulty != null && this.difficulty.notValid()) {
			return true;
		}
		if (this.distance != null && this.distance.notValid()) {
			return true;
		}
		if (this.chance != null && this.chance.notValid(0, 100)) {
			return true;
		}
		return this.notValidImpl();
	}

	protected boolean notValidImpl() {
		return false;
	}

	public final boolean isNull() {
		return this.temp == null && this.wet == null && this.difficulty == null && this.distance == null && this.isNullImpl();
	}

	protected boolean isNullImpl() {
		return true;
	}
}