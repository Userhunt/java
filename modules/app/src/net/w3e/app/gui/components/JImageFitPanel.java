package net.w3e.app.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import net.skds.lib2.mat.FastMath;
import net.w3e.app.utils.ImageUtil;

public class JImageFitPanel extends JImagePanel {

	public JImageFitPanel(BufferedImage image, int width, int height) {
		this(image, width, height, true);
	}

	public JImageFitPanel(BufferedImage image, int width, int height, boolean copy) {
		super();
		this.image = copy ? ImageUtil.deepCopy(image) : image;
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				Dimension min = JImageFitPanel.this.getMinimumSize();
				Dimension max = JImageFitPanel.this.getMaximumSize();
				Dimension real = JImageFitPanel.this.getSize();
				int w = FastMath.clamp(real.width, min.width, max.width);
				int h = FastMath.clamp(real.height, min.height, max.height);
				JImageFitPanel.this.setSize(w, h);
				JImageFitPanel.this.repaint();
			}
		});
		this.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void paintImage(Graphics g) {
		Rectangle rect = calculate(new Rectangle(), this.getPreferredSize(), this.getSize());

		calculate(rect, new Dimension(this.image.getWidth(), this.image.getHeight()), new Dimension(rect.width, rect.height));

		g.drawImage(this.image, rect.x, rect.y, rect.width, rect.height, null);
	}

	private final Rectangle calculate(Rectangle rect, Dimension prefered, Dimension real) {
		int dx = (int)(real.getWidth() * 100 / prefered.getWidth());
		int dy = (int)(real.getHeight() * 100 / prefered.getHeight());

		int min = Math.min(dx, dy);

		int width = (int)(real.getWidth() * min / dx);
		int height = (int)(real.getHeight() * min / dy);

		rect.x += ((int)real.getWidth() - width) / 2;
		rect.y += ((int)real.getHeight() - height) / 2;

		rect.width = width;
		rect.height = height;
		return rect;
	}

}
