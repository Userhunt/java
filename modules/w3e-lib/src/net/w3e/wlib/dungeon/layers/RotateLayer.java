package net.w3e.wlib.dungeon.layers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.Vec3I;
import net.skds.lib2.mat.Direction.Axis;
import net.w3e.wlib.collection.MapT.MapTString;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.json.ILayerData;
import net.w3e.wlib.mat.VecUtil;
import net.w3e.wlib.mat.WBoxI;

@DefaultJsonCodec(RotateLayer.RotateLayerDataJsonAdapter.class)
public class RotateLayer extends ListLayer<DungeonRoomInfo> {

	private static final BiFunction<DungeonRoomInfo, Vec3I, DungeonException> EXCEPTION = (old, pos) -> new DungeonException(String.format("Cant rotate room. %s -> %s", old.pos(), pos));

	public static final String TYPE = "rotate";
	private final Direction rotation;
	private final transient Map<Direction, Direction> wrapRotation = new HashMap<>();
	private final transient List<DungeonLayer> layers = new ArrayList<>();

	public RotateLayer(DungeonGenerator generator, Direction rotation) {
		super(TYPE, generator);
		this.rotation = rotation;
	}

	@Override
	public final ListLayer<DungeonRoomInfo> withDungeon(DungeonGenerator generator) {
		return new RotateLayer(generator, this.rotation);
	}

	public final boolean isValidRotation() {
		return this.rotation.isHorizontal() && this.rotation != Direction.SOUTH;
	}

	@Override
	public final void regenerate(boolean composite) throws DungeonException {
		if (this.isValidRotation()) {
			super.regenerate(composite);
			this.forEach(room -> {
				this.list.add(room.room());
				this.removeRoom(room.pos());
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

			this.layers.clear();
			this.layers.addAll(this.layers());
		}
	}

	@Override
	public final int generate() throws DungeonException {
		if (!this.isValidRotation()) {
			return 100;
		}

		for (int index = 0; index < 10 && !this.list.isEmpty(); index++) {
			DungeonRoomInfo old = this.list.removeFirst();
			Vec3I pos = VecUtil.rotateI(old.pos(), this.rotation);
			DungeonRoomCreateInfo info = this.putOrGet(pos);
			if (!info.isInside()) {
				throw EXCEPTION.apply(old, pos);
			}
			DungeonRoomInfo room = info.room();
			room.setEnterance(old.isEnterance());
			room.setWall(old.isWall());
			room.setDistance(old.getDistance());

			for (Entry<Direction, Direction> entry : this.wrapRotation.entrySet()) {
				Direction key = entry.getKey();
				Direction value = entry.getValue();
				if (old.isConnect(key, true)) {
					room.setConnection(value, true, true);
					continue;
				}
				if (old.isConnect(key, false)) {
					room.setConnection(value, true, false);
					continue;
				}
			}
			room.data().clear();
			room.data().putAll(old.data());
			for (DungeonLayer layer : this.layers) {
				layer.rotate(this.rotation, room, this.wrapRotation);
			}
		}

		return this.progress();
	}

	public static final DungeonGenerator rotate(DungeonGenerator generator, Direction rotation) throws DungeonException {
		RotateLayer layer = new RotateLayer(generator, rotation);
		layer.regenerate(false);
		while (layer.generate() < 100) {}
		return generator;
	}

	public static final Map<Vec3I, DungeonRoomInfo> rotate(Map<Vec3I, DungeonRoomInfo> rooms, Direction rotation) throws DungeonException {
		DungeonGenerator generator = new DungeonGenerator(0, WBoxI.of(rooms.values().stream().map(DungeonRoomInfo::pos).toList()), new MapTString(), Collections.emptyList());
		for (DungeonRoomInfo room : rooms.values()) {
			DungeonRoomCreateInfo info = generator.put(room);
			if (!info.isInside()) {
				throw EXCEPTION.apply(room, null);
			}
		}
		return rotate(generator, rotation).getRooms();
	}

	private static class RotateLayerDataJsonAdapter extends JsonReflectiveBuilderCodec<RotateLayerDataJsonAdapter.RotateLayerData> {

		public RotateLayerDataJsonAdapter(Type type, JsonCodecRegistry registry) {
			super(type, RotateLayerData.class, registry);
		}

		@SuppressWarnings({"FieldMayBeFinal"})
		public static class RotateLayerData implements ILayerData<RotateLayer> {
	
			private Direction rotation = Direction.SOUTH;
	
			@Override
			public RotateLayer withDungeon(DungeonGenerator generator) {
				return new RotateLayer(generator, this.rotation);
			}
		}
	}
}
