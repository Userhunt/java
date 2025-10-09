package net.w3e.wlib.dungeon.layers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import net.skds.lib2.io.codec.PostDeserializeCall;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.interfaces.IDungeonLimitedCount;
import net.w3e.wlib.dungeon.registry.DungeonRegistryContext;
import net.w3e.wlib.dungeon.registry.DungeonRegistryObject;
import net.w3e.wlib.json.WJsonBuilder;
import net.w3e.wlib.log.LogUtil;

public abstract class ListRegistryLayer<E, D extends IDungeonLimitedCount> extends ListLayer<E> {

	@Getter
	protected final transient List<DungeonRegistryObject> registryList = new ArrayList<>();
	protected final transient List<D> workDataList = new ArrayList<>();

	protected ListRegistryLayer(ConfigType<? extends ListLayer<E>> configType, DungeonGenerator generator, Collection<DungeonRegistryObject> values) {
		super(configType, generator);
		this.registryList.addAll(values);
	}

	@Override
	public void applyRegistryContext(DungeonRegistryContext registryContext) {
		this.registryList.forEach(e -> e.applyRegistry(registryContext, this::getData));
	}

	protected abstract D getData(DungeonRegistryContext registryContext, DungeonKeySupplier keyName);

	@Override
	@SuppressWarnings("unchecked")
	public void setupLayer(boolean composite) throws DungeonException {
		copyList(this.registryList, this.workDataList, e -> (D)((D)e.getObject()).clone());
		this.workDataList.removeIf(D::notValid);
	}
	
	protected abstract static class RegistryLayerDataBuilder<T, D extends IDungeonLimitedCount> implements PostDeserializeCall, WJsonBuilder<T> {

		protected List<DungeonRegistryObject> values;

		@Override
		public void postDeserialized() {
			this.isEmpty(this.values, "values");
			for (int i = 0; i < values.size(); i++) {
				DungeonRegistryObject object = values.get(i);
				if (object.isNull(IDungeonLimitedCount::notValid, IDungeonLimitedCount.class)) {
					throw new DungeonException(LogUtil.ILLEGAL.createMsg("values[" + i + "]"));
				}
			}
		}
		
	}
}
