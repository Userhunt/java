package net.w3e.base.dungeon.json;

import net.w3e.base.json.adapters.WTypedJsonAdapter;

public class DungeonLayerJsonAdapter extends WTypedJsonAdapter<String, ILayerAdapter<?>> {

	@Override
	protected final String parseKey(String key) {
		return key;
	}
	@Override
	protected boolean canReplace(String key) {
		return false;
	}

	@Override
	public final DungeonLayerJsonAdapter copy() {
		return copy(true);
	}

	public final DungeonLayerJsonAdapter copy(boolean canReplace) {
		DungeonLayerJsonAdapter copy = new DungeonLayerJsonAdapter() {
			@Override
			protected boolean canReplace(String key) {
				return canReplace;
			}
		};
		copy.map.putAll(this.map);
		return copy;
	}
}
