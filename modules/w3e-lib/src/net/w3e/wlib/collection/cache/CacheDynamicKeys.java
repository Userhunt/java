package net.w3e.wlib.collection.cache;

import java.util.ArrayList;
import java.util.List;

public abstract class CacheDynamicKeys<T> extends CacheKeys<T> {

	private final List<DynamicKey<T>> dynamic = new ArrayList<>();

	@Override
	public final boolean remove(T value) {
		boolean find = false;
		for (DynamicKey<T> dynamicKey : this.dynamic) {
			if (dynamicKey.value == value) {
				find = true;
				this.dynamic.remove(dynamicKey);
				break;
			}
		}
		if (!find) {
			for (DynamicKey<T> dynamicKey : this.dynamic) {
				if (dynamicKey.value.equals(value)) {
					this.dynamic.remove(dynamicKey);
					break;
				}
			}
		}
		return super.remove(value);
	}

	@Override
	protected final void clearIns() {
		this.dynamic.clear();
		super.clearIns();
	}

	//register t, []
	//unregister t, []
	//unregister []
	//empty instance

	private record DynamicKey<T>(T value, String... dynamicReason) {}
}
