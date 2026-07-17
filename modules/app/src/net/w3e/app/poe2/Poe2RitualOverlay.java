package net.w3e.app.poe2;

import lombok.Getter;
import net.skds.lib2.utils.platforms.PlatformFeatures;
import net.w3e.wlib.awtutils.components.JImageFitPanel;
import net.w3e.wlib.awtutils.frame.AppJFrame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import static net.w3e.app.poe2.Poe2JFrame.FRAME_OFFSET;
import static net.w3e.app.poe2.Poe2JFrame.FRAME_SIZE;

public class Poe2RitualOverlay extends AppJFrame implements KeyListener {

	private static final Color ALPHA = new Color(0, true);
	@Getter
	private final Poe2JFrame poe2JFrame;
	private final JImageFitPanel image;

	public Poe2RitualOverlay(Poe2JFrame poe2JFrame) {
		super();
		this.poe2JFrame = poe2JFrame;

		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setUndecorated(true);
		setBackground(ALPHA);

		this.image = new JImageFitPanel(new BufferedImage((12 - 1) * FRAME_OFFSET + FRAME_SIZE, (10 - 1) * FRAME_OFFSET + FRAME_SIZE, BufferedImage.TYPE_INT_ARGB), 100, 100);

		for (int x = 0; x < 12; x++) {
			final int xp = x * FRAME_OFFSET;
			for (int y = 0; y < 10; y++) {
				final int yp = y * FRAME_OFFSET;

				for (int i = 0; i < FRAME_SIZE; i++) {
					for (int j = 0; j < FRAME_SIZE; j++) {
						if (i == 0 || i == FRAME_SIZE - 1 || j == 0 || j == FRAME_SIZE - 1) {
							this.image.setColor(xp + i, yp + j, Color.WHITE);
						}
					}
				}
				this.image.setColor(xp + FRAME_SIZE / 2, yp + FRAME_SIZE / 2, Color.WHITE);
			}
		}

		this.image.setOpaque(false);

		this.add(this.image);

		PlatformFeatures.getInstance().addKeyListener(this);
		this.addCloseEvent(_ -> PlatformFeatures.getInstance().removeKeyListener(this));
		updateData(false);
		pack();
		this.setVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		var config = this.poe2JFrame.getConfig();
		if (e.getKeyCode() == KeyEvent.VK_W) {
			config.setY(config.getY() - 1);
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			config.setX(config.getX() - 1);
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			config.setY(config.getY() + 1);
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			config.setX(config.getX() + 1);
		}
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			config.setSize(config.getSize() - 1);
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			config.setSize(config.getSize() + 1);
		}
		updateData(true);

		System.out.println("size " + config.getSize());
	}

	private void updateData(boolean revalidate) {
		var config = this.poe2JFrame.getConfig();

		var size = this.poe2JFrame.getConfig().getSize();
		setPreferredSize(new Dimension(size, size));
		setSize(new Dimension(size, size));
		//this.image.setPreferredSize(new Dimension(size, size));

		this.setLocation(config.getX(), config.getY());
		if (revalidate) {
			this.revalidate();
			this.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
