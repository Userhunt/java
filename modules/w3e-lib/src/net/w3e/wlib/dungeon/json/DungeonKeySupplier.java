package net.w3e.wlib.dungeon.json;

@FunctionalInterface
public interface DungeonKeySupplier {
	Object getRaw();
	@SuppressWarnings("unchecked")
	default <T> T get() {
		return (T)getRaw();
	}
}
