package net.w3e.wlib.dungeon.layers;

import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;

public class ClearLayer extends DungeonLayer {

	public static final String TYPE = "clear";

	/**
	 * json
	 */
	private ClearLayer() {
		super(JSON_MAP.CLEAR, null);
	}

	public ClearLayer(DungeonGenerator generator) {
		super(JSON_MAP.CLEAR, generator);
	}

	@Override
	public final ClearLayer withDungeon(DungeonGenerator generator) {
		return new ClearLayer(generator);
	}

	@Override
	public final void setupLayer(boolean composite) {}

	@Override
	public final float generate() {
		this.forEach(room -> {
			if (room.isWall()) {
				this.removeRoom(room.pos());
			}
		});
		return 1f;
	}

	public static final ClearLayer example(DungeonGenerator generator) {
		return new ClearLayer(generator);
	}

}
