package net.w3e.base.generator.dungeon;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.w3e.base.BStringUtil;
import net.w3e.base.generator.PropertyType;
import net.w3e.base.registry.Registry;

public abstract class DungeonGenerator {

	public static final Registry<RoomType> ROOM_TYPES = new Registry<>("dungeon/room", "empty");

	public static final RoomType EMPTY = registerRoomType("empty", null);

	public static final RoomType registerRoomType(@NotNull String attribute, String id, String... flags) {
		RoomType type = new RoomType(attribute, id);
		ROOM_TYPES.register(type.getRegistryName(), type);
		return PropertyType.register(type);
	}

	protected final List<Room> modificators;

	public DungeonGenerator(List<Room> modificators) {
		this.modificators = modificators;
	}

	/*
	Данж
		Кубический
		Массив

	*/

	@Override
	public final String toString() {
		return String.format("{class:%s,hash:%s,modificators:%s}", BStringUtil.quote(this.getClass().getSimpleName()), this.hashCode(), this.modificators.stream().map(e -> BStringUtil.quote(e.type().getRegistryName())).toList());
	}
}
