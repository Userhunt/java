package net.home.oba;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.home.oba.ObaConfig.Display;
import net.home.oba.ObaConfig.Save;
import net.w3e.base.api.ImageUtil;
import net.w3e.base.api.window.AbstractFrameWin;
import net.w3e.base.api.window.jcomponent.JConsole;
import net.w3e.base.api.window.jcomponent.JImage;

public class ObaDisplay extends AbstractFrameWin {

	private final OneBitAdventure main;
	private final JImage MAP;
	private final JTextArea PATH;
	private final JTextArea REWARD;
	private final JTextArea BASE;
	private final JTextArea CONSOLE;
	private final JScrollPane CONSOLE_SCROLL;

	public ObaDisplay(OneBitAdventure main) {
		super("OBA Display");
		this.main = main;

		int scale = 17;

		JComponent component = null;
		this.MAP = new JImage(PointMap.createImage(), scale);
		this.MAP.setBounds(5, 25, 11 * scale, 15 * scale);
		this.add(MAP);

		component = this.MAP;
		this.PATH = new JConsole();
		this.PATH.setBounds(component.getX() + component.getWidth() + 5, 5, 273, 275);
		this.add(PATH);

		component = this.PATH;
		this.REWARD = new JConsole();
		this.REWARD.setBounds(component.getX() + component.getWidth() + 5, 5, 345, 275);
		this.REWARD.setFont(FONT);
		this.add(REWARD);

		component = this.REWARD;
		this.BASE = new JConsole();
		this.BASE.setBounds(component.getX() + component.getWidth() + 5, 5, 345, 275);
		this.BASE.setFont(FONT);
		this.add(BASE);

		component = this.BASE;
		this.CONSOLE = new JConsole();
		//this.CONSOLE.setBounds(0, 5 + component.getY() + component.getHeight(), 300, 275);
		this.CONSOLE.setFont(FONT);
		this.CONSOLE_SCROLL = new JScrollPane(this.CONSOLE, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		CONSOLE_SCROLL.setBounds(0, 5 + component.getY() + component.getHeight(), this.PATH.getX() + this.PATH.getWidth(), 275);
		this.add(CONSOLE_SCROLL);

		this.setSize(component.getX() + component.getWidth() + 20, CONSOLE_SCROLL.getY() + CONSOLE_SCROLL.getHeight() + 49);
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
