package net.w3e.app.api.window.jcomponent;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class JImage extends JPanel {

	private BufferedImage image;
	private double scale;

	public JImage(BufferedImage image, double scale) {
		super();
		setImage(image);
		setScale(scale);
	}

	public void setScale(double scale) {
		if (scale > 0) {
			this.scale = scale;
		}
	}

	public void setImage(BufferedImage image) {
		if (image != null) {
			boolean bl = this.image != null;
			this.image = image;
			if (bl) {
				repaint();
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image != null) {
			g.drawImage(this.image, 0, 0, (int)(this.image.getWidth() * this.scale), (int)(this.image.getHeight() * this.scale), null);
		}
	}
}
