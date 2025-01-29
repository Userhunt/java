package net.w3e.app.utils;

import net.w3e.app.MainFrame;

public class UtilsScreen {
	
	public static void main(String[] args) {
		MainFrame.register(new InfinitodeScreen());
		//MainFrame.register(new Tf2Screen());
		MainFrame.run(args);
	}
}
