package net.w3e.base.dungeon.layers.terra;

import java.util.ArrayList;
import java.util.List;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.layers.IListLayer;
import net.w3e.base.dungeon.layers.ISetupLayer;
import net.w3e.base.math.BMatUtil;

public abstract class TerraLayer<T> extends DungeonLayer implements ISetupLayer, IListLayer {

	protected final String defKey;
	protected final T defValue;
	private final List<DungeonRoomInfo> list = new ArrayList<>();
	protected int filled = -1;

	public TerraLayer(DungeonGenerator generator, String defKey, T defValue, int stepRate) {
		super(generator);
		this.defKey = defKey;
		this.defValue = defValue;
	}

	@Override
	public void setup(DungeonRoomInfo room) {
		room.data().put(this.defKey, this.defValue);
	}

	@Override
	public void regenerate() {
		this.forEach(room -> {}, false);
		this.filled = -1;
		this.list.clear();
	}

	@Override
	public final int generate() {
		if (filled == -1) {
			this.forEach(room -> {
				this.list.add(room.room());
			}, true);
			this.filled = this.list.size();
		}
		for (int i = 0; i < 50; i++) {
			if (!this.list.isEmpty()) {
				generate(this.list.remove(0));
			}
		}
		return BMatUtil.round(this.size() * 100f / this.filled);
	}

	@Override
	public final int size() {
		return this.filled - this.list.size();
	}

	@Override
	public final int filled() {
		return this.filled;
	}

	protected abstract void generate(DungeonRoomInfo room);
}
