package net.w3e.wlib.dungeon.layers.terra;

import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.ISetupLayer;
import net.w3e.wlib.dungeon.layers.ListLayer;

public abstract class TerraLayer<T> extends ListLayer<DungeonRoomInfo> implements ISetupLayer {

	protected final String defKey;
	protected final T defValue;
	protected final int stepRate;
	protected final boolean fast;

	public TerraLayer(DungeonGenerator generator, String defKey, T defValue, int stepRate, boolean fast) {
		super(generator);
		this.defKey = defKey;
		this.defValue = defValue;
		this.stepRate = stepRate;
		this.fast = fast;
	}

	@Override
	public void setup(DungeonRoomInfo room) {
		room.data().put(this.defKey, this.defValue);
	}

	@Override
	public final int generate() throws DungeonException {
		if (filled == -1) {
			this.generateList(room -> {
				return GenerateListHolder.success(room.room());
			}, !this.fast);
			return 1;
		}

		for (int i = 0; i < this.stepRate; i++) {
			if (!this.list.isEmpty()) {
				generate(this.list.remove(0));
				continue;
			}
			break;
		}

		return this.progress();
	}

	protected abstract void generate(DungeonRoomInfo room) throws DungeonException;
}
