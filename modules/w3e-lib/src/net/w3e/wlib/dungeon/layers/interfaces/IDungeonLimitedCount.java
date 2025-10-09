package net.w3e.wlib.dungeon.layers.interfaces;

public interface IDungeonLimitedCount extends Cloneable {
	DungeonInfoCountHolder count();

	default boolean addCount() {
		return this.isUnlimitedCount() || this.substractCount();
	}

	default boolean substractCount() {
		if (this.count().getValue() > 0) {
			this.count().decrement();
			return this.isLimitReachedCount();
		}
		return false;
	}

	default boolean isLimitReachedCount() {
		return this.count().getValue() == 0;
	}

	default boolean isUnlimitedCount() {
		return this.count().getValue() == -1;
	}

	IDungeonLimitedCount clone();
	boolean notValid();
}
