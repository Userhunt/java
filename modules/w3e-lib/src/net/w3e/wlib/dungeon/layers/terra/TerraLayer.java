package net.w3e.wlib.dungeon.layers.terra;

import lombok.RequiredArgsConstructor;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.layers.ISetupRoomLayer;
import net.w3e.wlib.dungeon.layers.ListLayer;
import net.w3e.wlib.dungeon.room.DungeonRoomInfo;

public abstract class TerraLayer<T> extends ListLayer<DungeonRoomInfo> implements ISetupRoomLayer {

	protected final transient String defKey;
	protected final T defValue;
	protected final int stepRate;
	protected final boolean createRoomIfNotExists;

	protected final transient TerraGenerator generator = createRoomGenerator();

	public TerraLayer(ConfigType<? extends TerraLayer<T>> configType, DungeonGenerator generator, String defKey, T defValue, int stepRate, boolean createRoomIfNotExists) {
		super(configType, generator);
		this.defKey = defKey;
		this.defValue = defValue;
		this.stepRate = stepRate;
		this.createRoomIfNotExists = createRoomIfNotExists;
	}

	@Override
	public void setupRoom(DungeonRoomInfo room) {
		room.getData().put(this.defKey, this.defValue);
	}

	@Override
	public final float generate() throws DungeonException {
		return this.generator.generate();
	}

	protected abstract TerraGenerator createRoomGenerator();

	@RequiredArgsConstructor
	public abstract static class IListLayerTerraHelper<T, L extends ListLayer<T>> {

		protected final L layer;

		protected abstract boolean isCreateRoomIfNotExists();
		protected abstract int getStepRate();
		protected abstract void generateRoom(T value);

		@SuppressWarnings("unchecked")
		public final float generate() throws DungeonException {
			if (layer.getFilled() == -1) {
				layer.generateList(room -> {
					return (GenerateListHolder<T>) GenerateListHolder.success(room.room());
				}, this.isCreateRoomIfNotExists());
				return 0.001f;
			}

			int stepRate = this.getStepRate();
			for (int i = 0; i < stepRate; i++) {
				if (!layer.list.isEmpty()) {
					this.generateRoom(layer.list.removeFirst());
					continue;
				}
				break;
			}

			return layer.progress();
		}
	}

	protected abstract class TerraGenerator extends IListLayerTerraHelper<DungeonRoomInfo, TerraLayer<?>> {

		public TerraGenerator(TerraLayer<?> layer) {
			super(layer);
		}

		@Override
		protected final boolean isCreateRoomIfNotExists() {
			return this.layer.createRoomIfNotExists;
		}

		@Override
		protected final int getStepRate() {
			return this.layer.stepRate;
		}

	}
}
