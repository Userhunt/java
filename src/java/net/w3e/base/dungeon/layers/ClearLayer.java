package net.w3e.base.dungeon.layers;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.math.vector.WDirection;

public class ClearLayer extends DungeonLayer implements ILayerAdapter<DungeonLayer> {

	public static final String TYPE = "clear";

	public ClearLayer(DungeonGenerator generator) {
		super(generator);
	}

	@Override
	protected final ClearLayer withDungeonImpl(DungeonGenerator generator) {
		return new ClearLayer(generator);
	}

	@Override
	public final void regenerate(WDirection rotation, boolean composite) {}

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
		return new ClearLayer(generator).setTypeKey(TYPE);
	}

}
