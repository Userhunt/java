package net.w3e.base.api;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;

public class StopEvent {
	
	private static final Int2ObjectAVLTreeMap<List<Runnable>> LIST = new Int2ObjectAVLTreeMap<>();

	public static void register(Runnable run) {
		register(run, 0);
	}

	public static void register(Runnable run, int i) {
		if (run != null) {
			LIST.computeIfAbsent(i, key -> new ArrayList<>()).add(run);
		}
	}

	public static void close() {
		for (List<Runnable> list : LIST.values()) {
			for (Runnable runnable : list) {
				runnable.run();
			}
		}
	}
}
