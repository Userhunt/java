package net.w3e.wlib.dungeon.layers.path.lab;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.direction.DungeonChances;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

@DefaultCodec(LabDFSLayer.LabDFSLayerJsonAdapter.class)
public class LabDFSLayer extends LabSimpleLayer {

	public static final String TYPE = "path/lab/dfs";

	private final transient List<DungeonRoomInfo> prev = new LinkedList<>();

	public LabDFSLayer(DungeonGenerator generator, int stepCount, DungeonChances connectionChances) {
		super(JSON_MAP.PATH_LAB_DFS, generator, stepCount, connectionChances);
	}

	@Override
	public DungeonLayer createGenerator(DungeonGenerator generator) {
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
				for (Direction connection : room.getHardNotConnected()) {
					Vec3I targetPos = room.getPos().addI(connection);
					if (testIsInside(targetPos) && this.get(targetPos).isWall()) {
						this.point = room;
						this.direction = null;
						return;
					}
				}
			}
		}
	}

	static class LabDFSLayerJsonAdapter extends ReflectiveBuilderCodec<LabDFSLayer> {

		public LabDFSLayerJsonAdapter(Type type, CodecRegistry registry) {
			super(type, LabDFSLayerData.class, registry);
		}

		private static class LabDFSLayerData extends LabSimpleLayerData<LabDFSLayer> {
			@Override
			protected LabDFSLayer build(int stepCount, DungeonChances directionChances) {
				return new LabDFSLayer(null, stepCount, directionChances);
			}
		}
	}

}
