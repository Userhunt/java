package net.home.oba2.states;

import net.home.oba2.OneBitAdventure.ObaHelper;

import net.api.ImageUtil;
import net.home.oba2.OneBitAssets;
import net.home.oba2.OneBitState;

public class PlayStateSetup extends OneBitState {

	@Deprecated
	public static final PlayStateSetup test(OneBitState oldState, ObaHelper helper) {
		if (oldState == null || oldState instanceof MainMenuState) {
			return new PlayStateSetup();
		}
		return new PlayStateSetup();
	}

	@Override
	public final void play(ObaHelper helper) {
		if (helper.isPlay()) {
			int width = 248;
			int height = 79;
			int[] pos = helper.getPosImage(.5, .5);
			helper.focus();
			while(!ImageUtil.compareImages(OneBitAssets.RandomLoader.END_ADVENTURE_BUTTON.get().value(), helper.getImage(pos[0] - width / 2, pos[1] + 120, width, height))) {
				esc();
				helper = helper.copy();
			}

			esc();
			sleep();
			this.ascension(helper);
			// class
			// items
			// quests
			// items
			// skills

			return;
		} else {
			sleep(5000);
		}
	}

	private void ascension(ObaHelper helper) {
		System.out.println("ascension");
		if (!ImageUtil.compareImages(OneBitAssets.RandomLoader.ASCENSION_ICON.get().value(), helper.getImage(199, 910, 70, 68))) {
			throw new IllegalStateException("Cant find ascension icon");
		}

		// открытие меню
		int[] pos = helper.getPos(.125, .925);
		click(pos[0], pos[1]);
		ObaHelper copy = helper.copy();

		pos = copy.getPosImage(.5, .865);
		if (!ImageUtil.compareImages(OneBitAssets.RandomLoader.ASCENSION_BUTTON.get().value(), copy.getImage(pos[0] - 244 - 5, pos[1] + 1, 244, 88))) {
			throw new IllegalStateException("Cant find ascension button icon");
		}

		this.ascensionScroll(copy);

		//move(pos[0], pos[1] - i);

		System.out.println("end ascension");
		sleep(5000);
		System.out.println("after sleep");
		helper.focus();
		esc();
		sleep();
		esc();
	}

	private final void ascensionScroll(ObaHelper copy) {
		// скролл
		int[] pos = copy.getPosImage(.25, .75);
		int i = 0;
		while(copy.getGray().getRGB(pos[0], pos[1]) != -9539986) {
			pos[1] -= 1;
			i++;
			if (i > 50) {
				throw new IllegalStateException("Something went wrong");
			}
		}
		while(copy.getGray().getRGB(pos[0], pos[1]) == -9539986) {
			pos[0] -= 1;
		}
		pos[0] -= 1;
		System.out.println(pos[0]);
		if (pos[0] < 50) {
			pos[1] -= 5;

			int j = 13;
			while(j > 0) {
				pos[1] -= 4;
				int color = copy.getGray().getRGB(pos[0], pos[1]);
				if (color == -15921907 || color == -9539986) {
					j--;
					continue;
				} else {
					break;
				}
			}
			System.out.println("j " + j);
			if (j == 0) {
				return;
			}
		}

		System.out.println("scroll to left");

		copy.save();
		i += 5;

		pos = copy.getPos(.25, .75);
	}

}
