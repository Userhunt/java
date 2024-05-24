package net.api.window.jcomponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import net.api.ImageUtil;

public class JImageLabel extends JPanel {
	private BufferedImage image;

	public JImageLabel(BufferedImage image, int width, int height) {
		this(image, width, height, true);
	}

	public JImageLabel(BufferedImage image, int width, int height, boolean copy) {
		super();
		this.image = copy ? ImageUtil.deepCopy(image) : image;
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				Dimension min = JImageLabel.this.getMinimumSize();
				Dimension max = JImageLabel.this.getMaximumSize();
				Dimension real = JImageLabel.this.getSize();
				JImageLabel.this.setSize(new Dimension((int)Math.max(min.getWidth(), Math.min(max.getWidth(), real.getWidth())), (int)Math.max(min.getHeight(), Math.min(max.getHeight(), real.getHeight()))));
				JImageLabel.this.repaint();
			}
		});
		this.setPreferredSize(new Dimension(width, height));
	}

	public final BufferedImage getImage() {
		return this.image;
	}

	public final void setImage(BufferedImage image) {
		if (image != null) {
			this.image = ImageUtil.deepCopy(image);
			this.repaint();
		}
	}

	public final void setColor(int x, int y, int color) {
		this.image.setRGB(x, y, color);
	}

	public final void setColor(int x, int y, Color color) {
		this.setColor(x, y, color.getRGB());
	}

	public final void setColor(Color color) {
		setColor(color.getRGB());
	}

	public final void setColor(int color) {
		for (int x = 0; x < this.image.getWidth(); x++) {
			for (int y = 0; y < this.image.getHeight(); y++) {
				setColor(x, y, color);
			}
		}
	}

	public final void fill() {
		this.fill(Color.WHITE);
	}

	public final void fill(Color color) {
		int c = Color.WHITE.getRGB();
		for (int x = 0; x < this.image.getWidth(); x++) {
			for (int y = 0; y < this.image.getHeight(); y++) {
				image.setRGB(x, y, c);
			}
		}
	}

	@Override
	public final void paint(Graphics g) {
		super.paint(g);
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