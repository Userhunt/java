package net.w3e.poe;

import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.gui.AppJFrame;

public class PoeGui extends AppJFrame {

	public static void main(String[] args) {
		SKDSLogger.replaceOuts();
		new PoeGui().setVisible(true);
	}

	public PoeGui() {
		super("Poe2");

		initScreen();
	}
}
