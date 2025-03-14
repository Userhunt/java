package net.w3e.wlib.dungeon.layers.path.lab;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.direction.DungeonChances;

@DefaultJsonCodec(LabDFSLayer.LabDFSLayerJsonAdapter.class)
public class LabDFSLayer extends LabSimpleLayer {

	public static final String TYPE = "path/lab/dfs";

	private final transient List<DungeonRoomInfo> prev = new LinkedList<>();

	protected LabDFSLayer(DungeonGenerator generator, int stepCount, DungeonChances connectionChances) {
		super(JSON_MAP.PATH_LAB_DFS, generator, stepCount, connectionChances);
	}

	@Override
	public DungeonLayer withDungeon(DungeonGenerator generator) {
		return new LabDFSLayer(generator, this.stepCount, this.connectionChances);
	}

	@Override
	protected void onAddRoom(DungeonRoomInfo room) {
		super.onAddRoom(room);
		this.prev.add(room);
	}

	@Override
	protected void generateNextPathPoint() {
		if (!this.prev.isEmpty()) {
			System.out.println(this.prev);
			int stepId = this.stepCount * 2;
			while (stepId > 1 && !this.prev.isEmpty()) {
				stepId--;
				DungeonRoomInfo room = this.prev.removeLast();
				for (Direction connection : room.getNotConnected()) {
					Vec3I targetPos = room.pos().addI(connection);
					if (testIsInside(targetPos) && this.get(targetPos).isWall()) {
						this.point = room;
						this.direction = null;
						return;
					}
				}
			}
		}
	}

	static class LabDFSLayerJsonAdapter extends JsonReflectiveBuilderCodec<LabDFSLayer> {

		public LabDFSLayerJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, LabDFSLayerData.class, registry);
		}

		private static class LabDFSLayerData extends LabSimpleLayerData<LabDFSLayer> {
			@Override
			protected LabDFSLayer withDungeon(DungeonGenerator generator, int stepCount, DungeonChances directionChances) {
				return new LabDFSLayer(generator, stepCount, directionChances);
			}
		}
	}

	public static final LabDFSLayer example(DungeonGenerator generator) {
		return new LabDFSLayer(generator, 3, new DungeonChances(10, 4, 4, 4, 0, 0, 1, 1, 0, 0));
	}
}
