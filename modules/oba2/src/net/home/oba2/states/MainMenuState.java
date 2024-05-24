package net.home.oba2.states;

import net.home.oba2.OneBitAdventure.ObaHelper;

import java.awt.image.BufferedImage;

import net.home.oba2.OneBitAdventure;
import net.home.oba2.OneBitState;

public class MainMenuState extends OneBitState {

	private static final int COLOR_1 = -15921907;
	private final ObaHelper helper;
	private boolean main;
	private boolean left;

	private MainMenuState(ObaHelper helper, boolean main, boolean left) {
		this.helper = helper;
		this.main = main;
		this.left = left;
	}

	public static final MainMenuState test(OneBitState oldState, ObaHelper helper) {
		BufferedImage gray = helper.getGray();
		if (gray.getRGB(0, 3) == COLOR_1) {
			boolean wasMain = oldState instanceof MainMenuState;
			if (gray.getRGB(0, helper.height() - 1) == COLOR_1) {
				if (wasMain) {
					return new MainMenuState(helper, true, false);
				} else {
					return new MainMenuState(helper, true, true);
				}
			} else {
				return new MainMenuState(helper, false, false);
			}
		}
		return null;
	}

	@Override
	public final void play(ObaHelper helper) {
		if (this.main) {
			if (this.left) {
				helper.debug("Move to left");
				if (helper.isPlay()) {
					this.left = false;
					for (int i = 0; i < 2; i++) {
						for (int j = 0; j < 15; j++) {
							helper.focus();
							sleep(10);
							a();
							sleep(10);
						}
					}
					int[] pos1 = helper.getPos(.5, .6);
					int[] pos2 = helper.getPos(.5, .4);
					drag(pos1[0], pos1[1], pos2[0], pos2[1]);
					helper.focusBack();
				}
			} else {
				helper.waitKey(this);
				helper.info("Select the character. Use \"a\"/\"d\" or numbers for select. \"s\" or enter to select");
			}
		} else {
			helper.waitKey(this);
			helper.info("Press enter to continue or esc to go back");
		}
	}

	@Override
	public final void handleKey(OneBitAdventure main, String text) {
		if (this.main) {
			boolean empty = text.isEmpty();
			text = text.toLowerCase().replaceAll(" ", "");
			if (text.equals("a")) {
				helper.focus();
				sleep(10);
				a();
			} else if (text.equals("d")) {
				helper.focus();
				sleep(10);
				d();
			} else if (text.equals("r")) {
				this.left = true;
				this.play(this.helper);
			} else {
				if (text.equals("s") || empty) {
					helper.focus();
					sleep(10);
					int[] pos = helper.getPos(.5, .5);
					click(pos[0], pos[1]);
				} else {
					try {
						int i = Integer.parseInt(text);
						helper.focus();
						sleep(1000);
						while(i != 0) {
							if (i < 0) {
								a();
								i++;
							} else {
								d();
								i--;
							}
							sleep(10);
						}
					} catch (Exception e) {
						main.error(e);
					}
				}
			}
		} else {
			this.helper.focus();
			if (text == null) {
				esc();
			} else {
				int[] pos = this.helper.getPos(.5, .3);
				click(pos[0], pos[1]);
				sleep(2000);
			}
		}
	}
}
