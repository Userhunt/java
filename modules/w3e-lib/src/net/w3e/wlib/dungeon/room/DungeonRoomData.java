package net.w3e.wlib.dungeon.room;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.codec.annotation.SkipSerialization;
import net.w3e.wlib.collection.CollectionUtils;
import net.w3e.wlib.collection.map.HashMapKString;
import net.w3e.wlib.collection.map.MapK;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;

public class DungeonRoomData extends DungeonRoomBaseData implements MapK<String> {

	public static final DungeonRoomData NULL = new DungeonRoomData();

	@Getter
	@Setter
	@SkipSerialization(defaultInt = -1)
	private int distance = -1;

	@Setter
	@Getter
	@SkipSerialization
	private DungeonKeySupplier biome = null;

	@SkipSerialization(predicate = CollectionUtils.MapNullPredicate.class)
	private final HashMapKString other = new HashMapKString();

	public DungeonRoomData copyFrom(DungeonRoomData data) {
		super.copyFrom(data);
		this.distance = data.distance;
		this.biome = data.biome;
		this.other.putAll(data.other);
		return this;
	}

	@Override
	public DungeonRoomData clone() {
		return new DungeonRoomData().copyFrom(this);
	}

	public void put(String key, Object value) {
		this.other.put(key, value);
	}

	@Override
	public <T> T getT(String key) {
		return this.other.getT(key);
	}

}
