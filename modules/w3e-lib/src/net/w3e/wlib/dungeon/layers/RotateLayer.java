package net.w3e.wlib.dungeon.layers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.mat.vec3.Direction.Axis;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.room.DungeonRoomData;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;
import net.w3e.wlib.json.WJsonBuilder;
import net.w3e.wlib.mat.VecUtil;
import net.w3e.wlib.mat.WBoxI;

@DefaultCodec(RotateLayer.RotateLayerDataJsonAdapter.class)
public class RotateLayer extends ListLayer<DungeonRoomInfo> {

	private static final BiFunction<DungeonRoomInfo, Vec3I, DungeonException> EXCEPTION = (old, pos) -> new DungeonException(String.format("Cant rotate room. %s -> %s", old.getPos(), pos));

	public static final String TYPE = "rotate";
	private final Direction rotation;
	private final transient Map<Direction, Direction> wrapRotation = new HashMap<>();
	private final transient List<DungeonLayer> layers = new ArrayList<>();

	public RotateLayer(DungeonGenerator generator, Direction rotation) {
		super(JSON_MAP.ROTATE, generator);
		this.rotation = rotation;
	}

	@Override
	public final RotateLayer createGenerator(DungeonGenerator generator) {
		return new RotateLayer(generator, this.rotation);
	}

	public final boolean isValidRotation() {
		return this.rotation.isHorizontal() && this.rotation != Direction.SOUTH;
	}

	@Override
	public void setupLayer(boolean composite) throws DungeonException {
		if (this.isValidRotation()) {
			this.forEach(room -> {
				this.list.add(room.room());
				this.removeRoom(room.getPos());
			});
			this.filled = this.list.size();
			this.rotateDimension(this.rotation);

			int size = 0;
			Direction rot = this.rotation;
			while (rot != Direction.SOUTH) {
				rot = rot.rotateCounterclockwise(Axis.Y);
				size += 1;
			}

			this.wrapRotation.clear();
			for (Direction direction : Direction.values()) {
				if (direction.isHorizontal()) {
					Direction out = direction;
					for (int i = 0; i < size; i++) {
						out = out.rotateCounterclockwise(Axis.Y);
					}
					this.wrapRotation.put(direction, out);
				}
			}
		}
	}

	@Override
	public final float generate() throws DungeonException {
		if (!this.isValidRotation()) {
			return 1;
		}

		for (int index = 0; index < 10 && !this.list.isEmpty(); index++) {
			DungeonRoomInfo old = this.list.removeFirst();
			Vec3I pos = VecUtil.rotateI(old.getPos(), this.rotation);
			DungeonRoomCreateInfo info = this.putOrGet(pos);
			if (!info.isInside()) {
				throw EXCEPTION.apply(old, pos);
			}
			DungeonRoomInfo room = info.room();
			room.setEntrance(old.isEntrance());
			room.setWall(old.isWall());
			room.getData().setDistance(old.getData().getDistance());

			for (Entry<Direction, Direction> entry : this.wrapRotation.entrySet()) {
				Direction key = entry.getKey();
				Direction value = entry.getValue();
				if (old.isHardConnect(key)) {
					room.setHardConnection(value, true);
					continue;
				}
				if (old.isSoftConnect(key)) {
					room.setSoftConnection(value, true);
					continue;
				}
			}
			room.getData().copyFrom(old.getData());
			for (DungeonLayer layer : this.layers) {
				layer.rotate(this.rotation, room, this.wrapRotation);
			}
		}

		return this.progress();
	}

	public static final DungeonGenerator rotate(DungeonGenerator generator, Direction rotation) throws DungeonException {
		RotateLayer layer = new RotateLayer(generator, rotation);
		layer.setupLayer(false);
		while (layer.generate() < 1f) {}
		return generator;
	}

	public static final Map<Vec3I, DungeonRoomInfo> rotate(Map<Vec3I, DungeonRoomInfo> rooms, Direction rotation) throws DungeonException {
		DungeonGenerator generator = new DungeonGenerator(0, WBoxI.of(rooms.values().stream().map(DungeonRoomInfo::getPos).toList()), new DungeonRoomData(), Collections.emptyList());
		for (DungeonRoomInfo room : rooms.values()) {
			DungeonRoomCreateInfo info = generator.put(room);
			if (!info.isInside()) {
				throw EXCEPTION.apply(room, null);
			}
		}
		return rotate(generator, rotation).getRooms();
	}

	static class RotateLayerDataJsonAdapter extends ReflectiveBuilderCodec<RotateLayer> {

		public RotateLayerDataJsonAdapter(Type type, CodecRegistry registry) {
			super(type, RotateLayerData.class, registry);
		}

		@SuppressWarnings({"FieldMayBeFinal"})
		public static class RotateLayerData implements WJsonBuilder<RotateLayer> {

			private Direction rotation = Direction.SOUTH;

			@Override
			public RotateLayer build() {
				return new RotateLayer(null, this.rotation);
			}
		}
	}
}
