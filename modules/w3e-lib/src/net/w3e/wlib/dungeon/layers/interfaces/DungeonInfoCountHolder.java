package net.w3e.wlib.dungeon.layers.interfaces;

import net.skds.lib2.utils.Holders.IntHolder;

public class DungeonInfoCountHolder extends IntHolder {
	public DungeonInfoCountHolder() {}

	public DungeonInfoCountHolder(int value) {
		super(value);
	}

	public final DungeonInfoCountHolder copy() {
		return new DungeonInfoCountHolder(this.getValue());
	}
}
