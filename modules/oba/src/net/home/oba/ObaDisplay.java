package net.home.oba;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import net.api.ImageUtil;
import net.api.window.AbstractFrameWin;
import net.api.window.jcomponent.JConsole;
import net.api.window.jcomponent.JImage;
import net.home.oba.ObaConfig.Display;
import net.home.oba.ObaConfig.Save;

public class ObaDisplay extends AbstractFrameWin {

	private final OneBitAdventure main;
	private final JImage MAP;
	private final JTextArea PATH;
	private final JTextArea REWARD;
	private final JTextArea BASE;
	private final JConsole CONSOLE;

	public ObaDisplay(OneBitAdventure main) {
		super("OBA Display", null, false, null);
		this.main = main;

		int scale = 17;

		JComponent component = null;
		this.MAP = new JImage(PointMap.createImage(), scale);
		this.MAP.setBounds(5, 25, 11 * scale, 15 * scale);
		this.add(MAP);

		component = this.MAP;
		this.PATH = new JConsole(273, 275, false);
		this.PATH.setBounds(component.getX() + component.getWidth() + 5, 5, 273, 275);
		this.add(PATH);

		component = this.PATH;
		this.REWARD = new JConsole(345, 275, false);
		this.REWARD.setBounds(component.getX() + component.getWidth() + 5, 5, 345, 275);
		this.add(REWARD);

		component = this.REWARD;
		this.BASE = new JConsole(345, 275, false);
		this.BASE.setBounds(component.getX() + component.getWidth() + 5, 5, 345, 275);
		this.add(BASE);

		component = this.BASE;
		this.CONSOLE = new JConsole(500, 275);
		this.CONSOLE.scroll.setLocation(new Point(0, 5 + component.getY() + component.getHeight()));
		this.CONSOLE.scroll.setSize(this.CONSOLE.scroll.getPreferredSize());
		this.add(this.CONSOLE.scroll);

		this.setSize(component.getX() + component.getWidth() + 20, this.CONSOLE.scroll.getY() + this.CONSOLE.scroll.getHeight() + 49);
	}

	@Override
	protected void onClose() {
		main.closeDisplay();
	}

	public void tick(BufferedImage capture) {
		boolean displayMap = ObaConfig.getDisplay(Display.map);
		boolean saveMap = ObaConfig.getSave(Save.map);

		if (displayMap || saveMap) {
			BufferedImage image = PointMap.createImage();
			if (displayMap) {
				this.MAP.setImage(image);
			}
			if (saveMap) {
				ImageUtil.save(image, "oba/map");
			}
		}

		if (ObaConfig.getDisplay(Display.path)) {
			display(this.PATH, 1);
		}

		if (ObaConfig.getDisplay(Display.reward)) {
			display(this.REWARD, 2);
		}

		if (ObaConfig.getDisplay(Display.base)) {
			display(this.BASE, 3);
		}

		if (ObaConfig.getSave(Save.screen)) {
			ImageUtil.save(capture, "oba/screen");
		}

		if (ObaConfig.getSave(Save.error)) {
			PointMap.iterate((x, y, point) -> {
				if (point.getType() instanceof PointType.PointUnsetType) {
					point.save();
				}
			});
		}
	}

	public void log(String str) {
		OneBitAdventure.MSG_UTIL.debug(str);
		String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
		this.CONSOLE.append(String.format("[%s]: %s\n", date, str));
		this.CONSOLE.setCaretPosition(this.CONSOLE.getDocument().getLength());
		this.CONSOLE.update(this.CONSOLE.getGraphics());
	}

	private final void display(JTextArea area, int mode) {
		List<StringBuilder> builders = new ArrayList<>();
		for (int i = 0; i < 16; i++) {
			builders.add(new StringBuilder());
		}
		PointMap.printReward(mode, builders);
		area.setText("");
		PointMap.formatReward(builders);
		for (StringBuilder builder : builders) {
			area.append(
				builder.toString() + "\n"
			);
		}
		area.update(area.getGraphics());
	}
}
