package net.home.random_generator;

import java.util.List;

import net.api.window.FrameWin;
import net.home.main.FrameObject;
import net.home.main.MainFrame;

public class RandGenScreen extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new RandGenScreen());
		MainFrame.run(args);
	}

	protected void init(FrameWin fw, List<String> args) {

	}

	@Override
	public final String getName() {
		return "Random Generator1";
	}

	@Override
	public final String fastKey() {
		return "rand_gen";
	}

	@Override
	public final int[] version() {
		return new int[]{1,0,0};
	}
}
