package net.home.split_screen;

import java.util.List;

import net.home.main.FrameObject;
import net.w3e.base.api.network.KeyboardNetwork;
import net.w3e.base.api.window.FrameWin;

public class SplitScreen extends FrameObject {

	protected void init(FrameWin fw, List<String> args) {
		net.w3e.base.api.network.Network.init();
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
	
}
