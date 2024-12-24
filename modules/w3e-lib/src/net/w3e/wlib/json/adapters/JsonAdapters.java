package net.w3e.wlib.json.adapters;

import net.skds.lib2.utils.json.JsonUtils;

public class JsonAdapters {
	public static void register() {
		JsonUtils.addAdapter(IntData.class, new IntData.IntDataAdapter());
	}
}
