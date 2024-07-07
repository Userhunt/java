package net.w3e.base.dungeon.layers.path;

import java.util.ArrayList;
import java.util.List;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonPos;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.DungeonLayer.IPathLayer;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;

public class PathRepeatLayer<T extends DungeonLayer & IPathLayer> extends DungeonLayer implements IPathLayer {

	private final T layer;
	private final int minumum;
	private final int count;

	public PathRepeatLayer(DungeonGenerator generator, T layer, int minumum, int count) {
		super(generator);
		this.layer = layer;
		this.minumum = minumum;
		this.count = count;
	}

	@Override
	public final void add(WVector3I pos, WDirection direction) {
		this.layer.add(pos, direction);
	}

	@Override
	public final void regenerate(boolean composite) {
		this.layer.regenerate(composite);
	}

	@Override
	public final int generate() {
		int value = this.layer.generate();
		if (value >= 100) {
			List<DungeonRoomInfo> rooms = new ArrayList<>();
			this.forEach(room -> {
				if (!room.room().isWall()) {
					rooms.add(room.room());
				}
			});
			WVector3I size = this.dungeonSize().add(new WVector3I(1, 1, 1));
			int s = size.getXI() * size.getYI() * size.getZI();
			int p = BMatUtil.round(rooms.size() * 1000f / s);
			if (p <= this.minumum) {
				int i = this.count;
				while (i > 0 && !rooms.isEmpty()) {
					DungeonRoomInfo room = rooms.remove(this.random().nextInt(rooms.size()));
					this.add(room.pos(), new DungeonPos().getDirection(this.random()));
				}
				return this.generate();
			}
		}
		return value;
	}

	public static final PathRepeatLayer<WormLayer> example(DungeonGenerator generator) {
		return new PathRepeatLayer<>(generator, WormLayer.example(generator), 50, 1);
	}
	
}
