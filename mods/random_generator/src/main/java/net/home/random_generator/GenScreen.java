package net.home.random_generator;

import java.util.List;

import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.w3e.base.api.window.FrameWin;
import net.w3e.base.tuple.number.WIntTuple;

public class GenScreen extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new GenScreen());
		MainFrame.run(args);
	}

	protected void init(FrameWin fw, List<String> args) {
		GenRegistry.register();
		WIntTuple y = new WIntTuple(5);

		addButton("Wooden Sword", this::woodenSword, y, fw);
		addButton("Stone Sword", this::stoneSword, y, fw);
		addButton("Iron Sword", this::ironSword, y, fw);
		addButton("Golden Sword", this::goldenSword, y, fw);

		fw.setSize(350, y.get() + 36);
	}

	private void woodenSword() {
		MainFrame.LOGGER.warn("wooden sword");
		GenRegistry.GENERATOR.generate(0, 0, 0, 0, 0, GenRegistry.SWORD_LIST);
	}

	private void stoneSword() {
		MainFrame.LOGGER.warn("stone sword");
		GenRegistry.GENERATOR.generate(1, 0, 1, 0, 6, GenRegistry.SWORD_LIST);
	}

	private void ironSword() {
		MainFrame.LOGGER.warn("iron sword");
		GenRegistry.GENERATOR.generate(3, 1, 3, 0, 10, GenRegistry.SWORD_LIST);
	}

	private void goldenSword() {
		MainFrame.LOGGER.warn("iron sword");
		GenRegistry.GENERATOR.generate(4, 2, 2, 0, 10, GenRegistry.SWORD_LIST);
	}

	@Override
	public final String getName() {
		return "Random Generator";
	}

	@Override
	public final String fastKey() {
		return "rand_gen";
	}

	@Override
	public int[] version() {
		return new int[]{1,0,0};
	}
}
