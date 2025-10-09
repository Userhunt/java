package net.w3e.wlib.dungeon;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.skds.lib2.io.codec.annotation.TransientComponent;
import net.skds.lib2.mat.vec3.Vec3I;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.mat.VecUtil;
import net.w3e.wlib.mat.WBoxI;

public record DungeonGeneratorResult(WBoxI dimension, Map<Vec3I, Map<Vec3I, DungeonRoomInfo>> chunks, @TransientComponent float progress, @TransientComponent DungeonLayer lastLayer) {

	public DungeonRoomCreateInfo get(Vec3I pos) {
		Vec3I chunkPos = VecUtil.pos2Chunk(pos);
		Map<Vec3I, DungeonRoomInfo> chunk = this.chunks.get(chunkPos);
		if (chunk == null) {
			return new DungeonRoomCreateInfo(new DungeonRoomInfo(pos, chunkPos), false, false);
		}
		DungeonRoomInfo room = chunk.get(pos);
		if (room == null) {
			return new DungeonRoomCreateInfo(new DungeonRoomInfo(pos, chunkPos), false, false);
		}
		return new DungeonRoomCreateInfo(room, true, true);
	}

	public DungeonGeneratorResult copy() {
		Map<Vec3I, Map<Vec3I, DungeonRoomInfo>> chunksNew = new HashMap<>();
		for (Entry<Vec3I, Map<Vec3I, DungeonRoomInfo>> entry1 : this.chunks.entrySet()) {
			Map<Vec3I, DungeonRoomInfo> chunkNew = new HashMap<>();
			for (Entry<Vec3I, DungeonRoomInfo> entry2 : entry1.getValue().entrySet()) {
				Vec3I pos = entry2.getKey();
				DungeonRoomInfo roomInfo = new DungeonRoomInfo(entry2.getKey());
				roomInfo.copyFrom(entry2.getValue());
				chunkNew.put(pos, roomInfo);
			}
			chunksNew.put(entry1.getKey(), chunkNew);
		}

		return new DungeonGeneratorResult(this.dimension, chunksNew, this.progress, this.lastLayer);
	}

	public boolean canceled() {
		return this.progress < 0;
	}

	public boolean isDone() {
		return this.progress >= 100;
	}

	public DungeonGeneratorResult cancel() {
		return new DungeonGeneratorResult(dimension, chunks, -this.progress, this.lastLayer);
	}
}
