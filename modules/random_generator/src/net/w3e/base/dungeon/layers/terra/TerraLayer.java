package net.w3e.base.dungeon.layers.terra;

import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.dungeon.layers.ISetupLayer;
import net.w3e.base.dungeon.layers.ListLayer;

public abstract class TerraLayer<T> extends ListLayer<DungeonRoomInfo> implements ISetupLayer {

	protected final String defKey;
	protected final T defValue;

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
	public final int generate() {
		if (filled == -1) {
			this.generateList(room -> {
				return GenerateListHolder.success(room.room());
			}, true);
			return 1;
		}

		for (int i = 0; i < 50; i++) {
			if (!this.list.isEmpty()) {
				generate(this.list.remove(0));
				continue;
			}
			break;
		}

		return this.progress();
	}

	protected abstract void generate(DungeonRoomInfo room);
}
