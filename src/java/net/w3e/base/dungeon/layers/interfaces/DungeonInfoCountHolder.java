package net.w3e.base.dungeon.layers.interfaces;

import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.holders.number.NumberHolder;

public class DungeonInfoCountHolder extends IntHolder {
	public DungeonInfoCountHolder() {}

	public DungeonInfoCountHolder(int value) {
		super(value);
	}

	public DungeonInfoCountHolder(NumberHolder<?, ?> holder) {
		super(holder);
	}

	@Override
	public final DungeonInfoCountHolder copy() {
		return new DungeonInfoCountHolder(this);
	}
}
