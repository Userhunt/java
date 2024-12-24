package net.w3e.wlib.dungeon.layers.roomvalues;

import net.w3e.wlib.dungeon.DungeonRoomInfo;

public class BaseLayerRoomValues extends AbstractLayerRoomValues<BaseLayerRoomRange> {

	public BaseLayerRoomValues(DungeonRoomInfo room) {
		super(room);
	}

	@Override
	protected final boolean testImpl(BaseLayerRoomRange values) {
		return true;
	}
}
