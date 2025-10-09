package net.w3e.wlib.dungeon.layers.path.lab;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.collection.CollectionUtils;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.direction.DungeonChances;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

@DefaultCodec(LabHAKLayer.LabHAKLayerJsonAdapter.class)
public class LabHAKLayer extends LabSimpleLayer {

	public static final String TYPE = "path/lab/hak";

	private final transient Set<DungeonRoomInfo> rooms = CollectionUtils.identityLinkedSet();

	public LabHAKLayer(DungeonGenerator generator, int stepCount, DungeonChances connectionChances) {
		super(JSON_MAP.PATH_LAB_HAK, generator, stepCount, connectionChances);
	}

	@Override
	public final DungeonLayer createGenerator(DungeonGenerator generator) {
		return new LabHAKLayer(generator, this.stepCount, this.connectionChances);
	}

	@Override
	protected List<DungeonRoomInfo> generatePre() {
		List<DungeonRoomInfo> rooms = super.generatePre();
		this.rooms.addAll(rooms);
		return rooms;
	}

	@Override
	protected void generateNextPathPoint() {
		boolean found = false;
		Iterator<DungeonRoomInfo> iterator = this.rooms.iterator();
		while (iterator.hasNext()) {
			DungeonRoomInfo room = iterator.next();
			if (!room.isWall()) {
				found = false;
				for (Direction connection : room.getHardNotConnected()) {
					Vec3I targetPos = room.getPos().addI(connection);
					if (testIsInside(targetPos) && this.get(targetPos).isWall()) {
						this.point = room;
						this.direction = null;
						found = true;
						return;
					}
				}
				if (!found) {
					//log.debug("remove " + room.pos());
					iterator.remove();
				}
			}
		}
	}

	static class LabHAKLayerJsonAdapter extends ReflectiveBuilderCodec<LabHAKLayer> {

		public LabHAKLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, LabHAKLayerData.class, registry);
		}

		private static class LabHAKLayerData extends LabSimpleLayerData<LabHAKLayer> {
			@Override
			protected LabHAKLayer build(int stepCount, DungeonChances directionChances) {
				return new LabHAKLayer(null, stepCount, directionChances);
			}
		}
	}

}
