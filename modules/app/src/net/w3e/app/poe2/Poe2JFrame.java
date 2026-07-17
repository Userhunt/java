package net.w3e.app.poe2;

import lombok.Getter;
import net.skds.lib2.awtutils.layouts.HorizontalLayout;
import net.skds.lib2.awtutils.layouts.LayoutMode;
import net.skds.lib2.mat.vec2.Vec2I;
import net.skds.lib2.utils.Holders;
import net.w3e.app.WUser32;
import net.w3e.wlib.awtutils.InputsEmulator;
import net.w3e.wlib.awtutils.frame.AppJFrame;
import net.w3e.wlib.awtutils.utils.JGuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Poe2JFrame extends AppJFrame {

	public static final int FRAME_SIZE = 9;
	public static final int FRAME_OFFSET = FRAME_SIZE + 2;

	private Poe2RitualOverlay overlay;
	@Getter
	private Poe2Config config = Poe2Config.read();

	public Poe2JFrame() {
		super("Poe 2");

		JPanel panel = new JPanel();
		panel.setLayout(new HorizontalLayout(5, LayoutMode.CENTER));
		panel.add(this.createButton("Настроить", _ -> {
			if (this.overlay != null) {
				this.overlay.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
				this.overlay = null;
			} else {
				this.overlay = new Poe2RitualOverlay(this);
				this.overlay.addCloseEvent(_ -> {
					this.overlay = null;
				});
			}
		}));
		panel.add(this.createButton("Сохранить", _ -> {
			config.save();
		}));
		panel.add(this.createButton("Отложить", _ -> {
			iterate(false);
		}));
		/*panel.add(this.createButton("Купить", _ -> {
			iterate(true);
		}));*/
		panel.add(this.createButton("Перезагрузить", _ -> {
			this.config = Poe2Config.read();
		}));

		this.add(panel, BorderLayout.CENTER);

		this.addCloseEvent(_ -> {
			System.exit(0);
		});
		this.initScreen();
	}

	private void iterate(boolean buy) {
		long poe = WUser32.getHWnd("Path of Exile 2");
		if (poe == 0) {
			return;
		}
		InputsEmulator.sleep(500);

		var clipboard = InputsEmulator.getClipboard();
		float size = ((float) this.config.getSize()) / ((12 - 1f) * FRAME_OFFSET + FRAME_SIZE);
		int offset = (int) (FRAME_SIZE / 2 * size);
		final int prefix = "Класс предмета:".length() + 1;

		WUser32.setFocusAndForeground(poe);
		InputsEmulator.sleep(1);

		if (!buy) {
			final int xp = (int) (10 * FRAME_OFFSET * size + this.config.getX()) + offset;
			final int yp = (int) ((-1.25 * FRAME_OFFSET + FRAME_OFFSET) * size + this.config.getY()) + offset;
			InputsEmulator.click(xp, yp);
			InputsEmulator.sleep(1);
		}

		long time = System.currentTimeMillis();
		String gloves = copyGlove();
		InputsEmulator.sleep(1);
		if (System.currentTimeMillis() - time > 1000) {
			System.err.println("cancel");
			return;
		}

		var items = this.getConfig().getItems();
		Holders.BooleanHolder save = new Holders.BooleanHolder(false);
		int emptyX = 0;
		Set<Vec2I> checked = new HashSet<>();
		for (int x = 0; x < 12; x++) {
			final int xp = (int) (x * FRAME_OFFSET * size + this.config.getX()) + offset;
			emptyX++;
			if (emptyX >= 2) {
				break;
			}
			for (int y = 0; y < 10; y++) {
				final int yp = (int) ((y * FRAME_OFFSET + FRAME_OFFSET) * size + this.config.getY()) + offset;

				if (checked.contains(new Vec2I(x, y))) {
					emptyX = 0;
					continue;
				}

				copyGlove();

				InputsEmulator.mouseMove(xp, yp);
				InputsEmulator.copy();

				String text = waitForClipboardData();
				if (Objects.equals(gloves, text)) {
					continue;
				}
				if (!text.isBlank()) {
					try {
						emptyX = 0;
						text = text.substring(prefix);
						String[] split = text.split("\n");
						String name = split[2];

						Poe2Config.ItemClass itemClass = items.computeIfAbsent(split[0], _ -> {
							save.setValue(true);
							return new Poe2Config.ItemClass();
						});

						if (itemClass.getY() > 0) {
							var dy = itemClass.getY();
							if (itemClass.getX() > 1) {
								var dx = itemClass.getX();
								for (int i = 0; i < dx - 1; i++) {
									int p = x + i + 1;
									for (int j = 0; j < dy; j++) {
										checked.add(new Vec2I(p, j + y));
									}
								}
							}
							y += dy - 1;
						}

						Poe2Config.ItemState bl = itemClass.getItems().computeIfAbsent(name, _ -> {
							save.setValue(true);
							return Poe2Config.ItemState.NULL;
						});
						if (bl.isTrue()) {
							InputsEmulator.sleep(1);
							InputsEmulator.click(xp, yp);
							InputsEmulator.sleep(1);
						}
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}
		}
		InputsEmulator.setClipboard(clipboard);
		if (save.getValue()) {
			this.config.save();
			System.out.println("save");
		}
		System.out.println("done");
	}

	private String copyGlove() {
		InputsEmulator.enter();
		InputsEmulator.sleep(1);
		InputsEmulator.a();

		InputsEmulator.sleep(1);
		InputsEmulator.ROBOT.keyPress(KeyEvent.VK_CONTROL);
		InputsEmulator.sleep(1);
		InputsEmulator.ROBOT.keyPress(KeyEvent.VK_A);
		InputsEmulator.sleep(1);
		InputsEmulator.ROBOT.keyRelease(KeyEvent.VK_A);
		InputsEmulator.sleep(1);
		InputsEmulator.ROBOT.keyRelease(KeyEvent.VK_CONTROL);

		InputsEmulator.copy();

		InputsEmulator.key(KeyEvent.VK_BACK_SPACE);
		InputsEmulator.sleep(1);
		InputsEmulator.enter();
		InputsEmulator.sleep(1);
		//InputsEmulator.mouseMove(xg, yg);

		return waitForClipboardData();
	}

	private String waitForClipboardData() {
		long startTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - startTime < 500) {
			InputsEmulator.sleep(20);
			try {
				String text = InputsEmulator.getClipboardText();
				if (text != null && !text.isEmpty()) {
					return text;
				}
			} catch (Exception _) {
			}
		}
		return "";
	}

	private JButton createButton(String text, ActionListener function) {
		JButton button = new JButton(text);
		JGuiUtils.setSize(button, 150, 26);
		button.addActionListener(function);
		return button;
	}

	static void main() {
		Poe2JFrame frame = new Poe2JFrame();
		frame.setVisible(true);
		//new WindowOverlay();

		//frame.enableConsole();
		//frame.setSize(700, 500);

	}
}
