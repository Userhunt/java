package net.api.window;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;

import net.api.window.jcomponent.JImageLabel;

public class ImageScreen extends AbstractFrameWin {

	protected final JImageLabel image;

	public ImageScreen(String frameTitle, int width, int height, int scale, Color background) {
		super(frameTitle);

		this.getRootPane().setBorder(BorderFactory.createEmptyBorder(1 * scale, 1 * scale, 1 * scale, 1 * scale));
		this.getRootPane().setBackground(background);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.image = new JImageLabel(image, width * scale, height * scale);

		this.add(this.image);

		this.pack();
	}

	public final void setColor(int x, int y, int color) {
		this.image.setColor(x, y, color);
	}

	public final void setColor(int x, int y, Color color) {
		this.setColor(x, y, color.getRGB());
	}

	public final void setColor(Color color) {
		setColor(color.getRGB());
	}

	public final void setColor(int color) {
		this.image.setColor(color);
	}

	public final void update() {
		this.image.repaint();
	}
	
	public final BufferedImage getImage() {
		return this.image.getImage();
	}

	public static class ImageScreenBuilder {

		private String title = "";
		private int x;
		private int y;
		private int width = 10;
		private int height = 10;
		private int scale = 10;
		private Color background = Color.black;

		public final ImageScreenBuilder setTitle(String title) {
			if (title != null) {
				this.title = title;
			}
			return this;
		}

		public final ImageScreenBuilder setWidth(int width) {
			if (width > 0) {
				this.width = width;
			}
			return this;
		}

		public final ImageScreenBuilder setHeight(int height) {
			if (height > 0) {
				this.height = height;
			}
			return this;
		}

		public final ImageScreenBuilder setSize(int wh) {
			if (wh > 0) {
				return this.setSize(wh, wh);
			}
			return this;
		}

		public final ImageScreenBuilder setSize(int width, int height) {
			return this.setWidth(width).setHeight(height);
		}

		public final ImageScreenBuilder setScale(int scale) {
			if (scale > 0) {
				this.scale = scale;
			}
			return this;
		}

		public final ImageScreenBuilder setBackground(Color color) {
			if (color != null) {
				this.background = color;
			}
			return this;
		}

		public final ImageScreenBuilder setLocation(AbstractFrameWin screen) {
			if (screen != null) {
				Point location = screen.getLocation();
				this.x = location.x;
				this.y = location.y + screen.getHeight();
			}
			return this;
		}

		public final ImageScreen build() {
			return this.build(true);
		}

		public final ImageScreen build(boolean visible) {
			ImageScreen screen = new ImageScreen(this.title, this.width, this.height, this.scale, this.background);
			screen.setLocation(this.x, this.y);
			screen.setVisible(visible);
			return screen;
		}

		public final <T extends ImageScreen> T buildWith(Factory<T> factory) {
			T screen = factory.create(this.title, this.width, this.height, this.scale, this.background);
			screen.setLocation(this.x, this.y);
			return screen;
		}
	}

	public static interface Factory<T extends ImageScreen> {
		T create(String frameTitle, int width, int height, int scale, Color background);
	}
}
