package net.w3e.wlib.dungeon.layers;

import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.ILayerAdapter;

public class ClearLayer extends DungeonLayer implements ILayerAdapter {

	public static final String TYPE = "clear";

	/**
	 * json
	 */
	private ClearLayer() {
		super(TYPE, null);
	}

	public ClearLayer(DungeonGenerator generator) {
		super(TYPE, generator);
	}

	@Override
	public final ClearLayer withDungeon(DungeonGenerator generator) {
		return new ClearLayer(generator);
	}

	@Override
	public final void regenerate(boolean composite) {}

	@Override
	public final int generate() {
		this.forEach(room -> {
			if (room.isWall()) {
				this.removeRoom(room.pos());
			}
		});
		return 100;
	}

	public static final ClearLayer example(DungeonGenerator generator) {
		return new ClearLayer(generator);
	}

}
