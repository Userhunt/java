package net.api.window;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import net.api.window.jcomponent.JImage;

public class ImageScreen extends AbstractFrameWin {

	private final BufferedImage image;

	public ImageScreen(String frameTitle, int width, int height, int scale, Color background) {
		super(frameTitle);
		this.image = image(1, 1, width, height, scale);
		width += 2;
		height += 2;
		BufferedImage backgroundImage = image(0, 0, width, height, scale);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				backgroundImage.setRGB(i, j, background.getRGB());
			}
		}

		this.setSize(width * scale + 16, height * scale + 39);
		this.setResizable(false);
	}

	private final BufferedImage image(int x, int y, int width, int height, int scale) {
		x *= scale;
		y *= scale;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		JImage jImage = new JImage(image, scale);
		jImage.setBounds(x, y, width * scale, height * scale);
		jImage.setPreferredSize(jImage.getSize());

		this.add(jImage);

		return image;
	}

	public final void setColor(int x, int y, int color) {
		this.image.setRGB(x, y, color);
	}

	public final void setColor(int x, int y, Color color) {
		this.image.setRGB(x, y, color.getRGB());
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
	}
}
