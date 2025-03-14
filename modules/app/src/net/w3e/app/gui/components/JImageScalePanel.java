package net.w3e.app.gui.components;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class JImageScalePanel extends JImagePanel {

	private double scale;

	public JImageScalePanel(BufferedImage image, double scale) {
		super(image);
		setScale(scale);
	}

	public final void setScale(double scale) {
		if (scale > 0) {
			this.scale = scale;
		}
	}

	@Override
	protected void paintImage(Graphics g) {
		g.drawImage(this.image, 0, 0, (int)(this.image.getWidth() * this.scale), (int)(this.image.getHeight() * this.scale), null);
	}
}
