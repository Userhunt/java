package net.w3e.wlib.dungeon.layers;

import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.json.ILayerAdapter;

public class ClearLayer extends DungeonLayer implements ILayerAdapter {

	public static final String TYPE = "clear";

	public ClearLayer(DungeonGenerator generator) {
		super(generator);
	}

	@Override
	protected String keyName() {
		return TYPE;
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
