package net.w3e.base.dungeon.layers.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.w3e.base.collection.CollectionUtils;
import net.w3e.base.collection.ArraySet.ArraySetStrict;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.DungeonLayer.IPathLayer;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonPos;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.direction.DungeonChances;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WVector3I;

public class WormLayer extends DungeonLayer implements IPathLayer {

	public final DungeonPos[] centers;
	public final WormDungeonStepChances stepChances;
	public final DungeonChances directionChances;
	public final DungeonChances connectionChances;
	private final List<DungeonPos>[] entries = CollectionUtils.createArrayList(DungeonPos.class, 2);

	public WormLayer(DungeonGenerator generator, DungeonPos[] centers, WormDungeonStepChances stepChances, DungeonChances directionChances, DungeonChances connectionChances) {
		super(generator);
		this.centers = centers;
		this.stepChances = stepChances;
		this.directionChances = directionChances;
		this.connectionChances = connectionChances;
	}

	@Override
	public final void regenerate(boolean composite) {
		this.entries[0] = new ArraySetStrict<>();
		this.entries[1] = new ArraySetStrict<>();
		for(DungeonPos pos : this.centers) {
			this.add(pos);
		}
	}

	private final void add(DungeonPos pos) {
		this.add(pos.pos(), pos.getDirection(this.random()), true);
	}

	@Override
	public final void add(WVector3I pos, WDirection direction) {
		this.add(pos, direction, false);
	}

	private final void add(WVector3I pos, WDirection direction, boolean enterance) {
		this.entries[1].add(new DungeonPos(pos, direction, enterance));
	}

	@Override
	public final int generate() {
		this.entries[0] = this.entries[1];
		this.entries[1] = new ArraySetStrict<>();
		for (DungeonPos entry : this.entries[0]) {
			WVector3I pos = entry.pos();

			int d = this.stepChances.get(this.random());
			WDirection direction = entry.getDirection(this.random());
			Collection<WDirection> connections = new ArrayList<>();
			connections.add(direction);
			boolean success = true;

			DungeonRoomCreateInfo info = this.putOrGet(pos);
			info.room().setWall(false);
			if (entry.enterance()) {
				info.room().setEnterance(true);
				if (!this.get(pos.add(direction)).notExistsOrWall()) {
					WDirection old = direction;
					entry = new DungeonPos(pos, null, true);
					while((direction = entry.getDirection(this.random())) == old) {}
					connections.clear();
					connections.add(direction);
				}
			}
			info.room().setConnections(connections, true, true);
			if (info.isInside() && info.notExistsOrWall()) {
				info.room().setConnections(connections, true, true);
			}
			connections.add(direction.opposite());
			while(d > 0) {
				d--;
				if (d == 0) {
					connections.remove(direction);
				}
				pos = pos.add(direction);
				info = this.putOrGet(pos);
				info.room().setWall(false);
				info.room().setConnections(this.connectionChances.generate(this.random(), direction), true, false);
				success = info.isInside() && info.notExistsOrWall();
				if (!success) {
					if (!info.notExistsOrWall() && !this.get(pos.add(direction.opposite())).notExistsOrWall()) {
						connections.remove(direction);
						info.room().setConnections(connections, true, true);
					}
					break;
				}
				info.room().setConnections(connections, true, true);
			}
			if (success) {
				connections = this.directionChances.generate(this.random(), direction);
				DungeonRoomInfo room = info.room();
				room.setConnections(connections, true, false);
				for (WDirection connection : connections) {
					if (connection == WDirection.UP || connection == WDirection.DOWN) {
						this.add(room.pos().add(connection), null);
					} else {
						this.add(room.pos(), connection);
					}
				}
			}
		}
		return Math.max(10 - this.entries[1].size(), 0) * 10;
	}

	public static record WormDungeonStepChances(int min, int max) {
		public final int get(Random random) {
			int step = this.min < 1 ? 1 : this.min;
			if (this.min < this.max && this.min > 0) {
				step = random.nextInt(this.max - this.min + 1) + this.min;
			}
			return step;
		}
	}

	public static final WormLayer example(DungeonGenerator generator) {
		return new WormLayer(
			generator,
			new DungeonPos[]{DungeonPos.EMPTY_ENTERANCE, DungeonPos.EMPTY_ENTERANCE}, 
			new WormDungeonStepChances(1, 3), 
			new DungeonChances(20, 15, 10, 5, 0, 0, 1, 5, 0, 0), 
			new DungeonChances(1, 1, 1, 1, 0, 0, 1, 1, 0, 0)
		);
	}
}
