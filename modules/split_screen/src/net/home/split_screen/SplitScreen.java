package net.home.split_screen;

import java.util.List;

import net.api.network.KeyboardNetwork;
import net.api.window.FrameWin;
import net.home.main.FrameObject;

public class SplitScreen extends FrameObject {

	protected void init(FrameWin fw, List<String> args) {
		net.api.network.Network.init();
		KeyboardNetwork.init();
		KeyboardNetwork.register(0, key -> {
			System.out.println(key);
		});
		fw.addKeyListener(KeyboardNetwork.create(0));
	}

	@Override
	public final String getName() {
		return "Split Screen";
	}

	@Override
	public final String fastKey() {
		return "split_screen";
	}

	@Override
	public int[] version() {
		return new int[]{1,0,0};
	}
}
