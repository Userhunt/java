package net.home.oba;

import java.util.List;

import net.home.main.MainArgs;
import net.home.main.MainArgs.MainArg;
import net.home.main.MainArgs.MainBoolArg;
import net.home.main.MainArgs.MainListEnumArg;
import net.w3e.base.PackUtil;

public class ObaConfig {

	private static final List<MainArg<OneBitAdventure>> ARGS = MainArgs.builder(OneBitAdventure.class)
	.add(new MainArg<>("class") {
		@Override
		protected boolean test(MainArgs<OneBitAdventure> args, OneBitAdventure frame, String value) {
			ObaConfig.setPlayer(value);
			return true;
		}
	})
	.add(new MainListEnumArg<>("display", Display.class) {
		@Override
		protected void apply(MainArgs<OneBitAdventure> args, OneBitAdventure frame, List<Display> list) {
			for (Display display : list) {
				ObaConfig.setDisplay(display, true);
			}
		}
		@Override
		protected void after(MainArgs<OneBitAdventure> args, OneBitAdventure frame) {
			if (ObaConfig.getDisplay(Display.run)) {
				new Thread() {
					public void run() {
						ObaMove.sleep(500);
						frame.clickDisplay();
					}
				}.start();
			}
		}
	})
	.add(new MainListEnumArg<>("save", Save.class) {
		@Override
		protected void apply(MainArgs<OneBitAdventure> args, OneBitAdventure frame, List<Save> list) {
			for (Save save : list) {
				ObaConfig.setSave(save, true);
			}
		}
	})
	.add(new MainBoolArg<>("play") {
		@Override
		protected boolean apply(MainArgs<OneBitAdventure> args, OneBitAdventure frame, Boolean value) {
			System.out.println(value);
			if (value != null) {
				ObaConfig.setRun(value);
			}
			return true;
		}
	})
	.build();

	public static void main(List<String> args, OneBitAdventure frame) {
		MainArgs.main(ARGS, args, frame);
	}

	public static interface ITexture {
		default String texture() {
			return this.name();
		}
		String name();
	}

	/* ==================== PLAYER ==================== */
	public static enum Player implements ITexture {
		unset() {
			@Override
			public String texture() {
				return "";
			}
		},
		butcher_bleed() {
			@Override
			public String texture() {
				return "butcher";
			}
		};
	}

	private static Player PLAYER;
	static {
		setPlayer(Player.unset);
	}

	public static void setPlayer(String player) {
		Player pl;
		try {
			pl = Player.valueOf(player);
		} catch (Exception e) {
			e.printStackTrace();
			pl = Player.unset;
		}
		setPlayer(pl);

	}

	public static void setPlayer(Player player) {
		if (player != null) {
			ObaConfig.PLAYER = player;
			PointType.PLAYER.reload(player.texture());
		}
	}

	public static Player getPlayer() {
		return ObaConfig.PLAYER;
	}


	/* ==================== DISPLAY ==================== */
	public static enum Display {
		map(1),
		path(2),
		reward(3),
		base(4),
		run(5),
		step(6);

		private final int i;

		private Display(int i) {
			this.i = i;
		}
	}

	private static byte DISPLAY = 0;

	public static void setDisplay(Display display, boolean mode) {
		DISPLAY = PackUtil.set(DISPLAY, display.i, mode);
	}

	public static boolean getDisplay(Display display) {
		return PackUtil.test(DISPLAY, display.i);
	}

	/* ==================== SAVE ==================== */
	public static enum Save {
		map(1),
		screen(2),
		number(3),
		error(4);

		private final int i;

		private Save(int i) {
			this.i = i;
		}
	}

	private static byte SAVE = 0;

	public static void setSave(Save save, boolean mode) {
		SAVE = PackUtil.set(SAVE, save.i, mode);
	}

	public static boolean getSave(Save save) {
		return PackUtil.test(SAVE, save.i);
	}

	/* ==================== RUN ==================== */

	private static boolean RUN = true;

	public static void setRun(boolean mode) {
		RUN = mode;
	}

	public static boolean getRun() {
		return RUN;
	}
}
