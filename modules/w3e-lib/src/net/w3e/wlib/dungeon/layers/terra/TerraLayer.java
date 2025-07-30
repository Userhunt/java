package net.w3e.wlib.dungeon.layers.terra;

import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.layers.ISetupRoomLayer;
import net.w3e.wlib.dungeon.layers.ListLayer;

public abstract class TerraLayer<T> extends ListLayer<DungeonRoomInfo> implements ISetupRoomLayer {

	protected final transient String defKey;
	protected final T defValue;
	protected final int stepRate;
	protected final boolean createRoomIfNotExists;

	public TerraLayer(ConfigType<? extends TerraLayer<T>> configType, DungeonGenerator generator, String defKey, T defValue, int stepRate, boolean createRoomIfNotExists) {
		super(configType, generator);
		this.defKey = defKey;
		this.defValue = defValue;
		this.stepRate = stepRate;
		this.createRoomIfNotExists = createRoomIfNotExists;
	}

	@Override
	public void setupRoom(DungeonRoomInfo room) {
		room.data().put(this.defKey, this.defValue);
	}

	@Override
	public final float generate() throws DungeonException {
		if (filled == -1) {
			this.generateList(room -> {
				return GenerateListHolder.success(room.room());
			}, this.createRoomIfNotExists);
			return 0.001f;
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
