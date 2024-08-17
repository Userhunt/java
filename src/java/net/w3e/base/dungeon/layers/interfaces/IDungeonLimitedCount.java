package net.w3e.base.dungeon.layers.interfaces;

import net.w3e.base.holders.number.IntHolder;

public interface IDungeonLimitedCount {
	IntHolder count();

	default boolean addCount() {
		return this.isUnlimitedCount() || this.substractCount();
	}

	default boolean substractCount() {
		if (this.count().getAsInt() > 0) {
			this.count().remove();
			return this.isLimitReachedCount();
		}
		return false;
	}

	default boolean isLimitReachedCount() {
		return this.count().getAsInt() == 0;
	}

	default boolean isUnlimitedCount() {
		return this.count().getAsInt() == -1;
	}
}
