package net.w3e.wlib.dungeon.json;

public class DungeonJsonAdaptersString {

	public static void initString() {
		DungeonKeySupplier.setTYPE(String.class);
		DungeonJsonAdapters.INSTANCE.register();
	}
}
