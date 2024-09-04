package net.w3e.base.dungeon.layers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import net.w3e.base.collection.MapT.MapTString;
import net.w3e.base.dungeon.DungeonException;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.json.ILayerAdapter;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.i.WBoxI;
import net.w3e.base.math.vector.i.WVector3I;

public class RotateLayer extends ListLayer<DungeonRoomInfo> {

	private static final BiFunction<DungeonRoomInfo, WVector3I, DungeonException> EXCEPTION = (old, pos) -> new DungeonException(String.format("Cant rotate room. %s -> %s", old.pos(), pos));

	public static final String TYPE = "rotate";
	private final WDirection rotation;
	private final Map<WDirection, WDirection> wrapRotation = new HashMap<>();
	private final List<DungeonLayer> layers = new ArrayList<>();

	public RotateLayer(DungeonGenerator generator, WDirection rotation) {
		super(generator);
		this.rotation = rotation;
	}

	@Override
	public final ListLayer<DungeonRoomInfo> withDungeonImpl(DungeonGenerator generator) {
		return new RotateLayer(generator, this.rotation);
	}

	public final boolean isValidRotation() {
		return this.rotation.isHorisontal() && this.rotation != WDirection.SOUTH;
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
			WDirection rot = this.rotation;
			while (rot != WDirection.SOUTH) {
				rot = rot.left();
				size += 1;
			}
	
			this.wrapRotation.clear();
			for (WDirection direction : WDirection.values()) {
				if (direction.isHorisontal()) {
					WDirection out = direction;
					for (int i = 0; i < size; i++) {
						out = out.left();
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
			WVector3I pos = old.pos().rotate(this.rotation);
			DungeonRoomCreateInfo info = this.putOrGet(pos);
			if (!info.isInside()) {
				throw EXCEPTION.apply(old, pos);
			}
			DungeonRoomInfo room = info.room();
			room.setEnterance(old.isEnterance());
			room.setWall(old.isWall());
			room.setDistance(old.getDistance());

			for (Entry<WDirection, WDirection> entry : this.wrapRotation.entrySet()) {
				WDirection key = entry.getKey();
				WDirection value = entry.getValue();
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

	public static final DungeonGenerator rotate(DungeonGenerator generator, WDirection rotation) throws DungeonException {
		RotateLayer layer = new RotateLayer(generator, rotation);
		layer.regenerate(false);
		while (layer.generate() < 100) {}
		return generator;
	}

	public static final Map<WVector3I, DungeonRoomInfo> rotate(Map<WVector3I, DungeonRoomInfo> rooms, WDirection rotation) throws DungeonException {
		DungeonGenerator generator = new DungeonGenerator(0, WBoxI.of(rooms.values().stream().map(DungeonRoomInfo::pos).toList()), MapTString::new, Collections.emptyList());
		for (DungeonRoomInfo room : rooms.values()) {
			DungeonRoomCreateInfo info = generator.put(room);
			if (!info.isInside()) {
				throw EXCEPTION.apply(room, null);
			}
		}
		return rotate(generator, rotation).getRooms();
	}

	@SuppressWarnings({"FieldMayBeFinal"})
	public static class RotateLayerData implements ILayerAdapter<RotateLayer> {

		private WDirection rotation = WDirection.SOUTH;

		@Override
		public RotateLayer withDungeon(DungeonGenerator generator) {
			return new RotateLayer(generator, this.rotation);
		}
	}
}
