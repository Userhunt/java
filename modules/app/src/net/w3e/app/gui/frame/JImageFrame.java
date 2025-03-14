package net.w3e.app.gui.frame;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import net.w3e.app.gui.components.JImageFitPanel;
import net.w3e.app.gui.components.JImagePanel;
import net.w3e.app.gui.utils.JFrameGuiUtils;
import net.w3e.app.gui.utils.JImageGuiUtils;

public class JImageFrame extends JFrame implements JFrameGuiUtils, JImageGuiUtils {

	protected final JImagePanel imagePanel;

	public JImageFrame(String frameTitle, int width, int height, int scale, Color background) {
		super(frameTitle);

		this.getRootPane().setBorder(BorderFactory.createEmptyBorder(scale, scale, scale, scale));
		this.getRootPane().setBackground(background);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.imagePanel = new JImageFitPanel(image, width * scale, height * scale);

		this.add(this.imagePanel);

		this.pack();
	}

	@Override
	public final BufferedImage getImage() {
		return this.imagePanel.getImage();
	}

	@Override
	public final void setImage(BufferedImage image) {}

	@Override
	public final void updateImage() {
		this.imagePanel.updateImage();
	}
	
	public static class JImageScreenBuilder {

		private String title = "";
		private int x;
		private int y;
		private int width = 10;
		private int height = 10;
		private int scale = 10;
		private Color background = Color.black;

		public final JImageScreenBuilder setTitle(String title) {
			if (title != null) {
				this.title = title;
			}
			return this;
		}

		public final JImageScreenBuilder setWidth(int width) {
			if (width > 0) {
				this.width = width;
			}
			return this;
		}

		public final JImageScreenBuilder setHeight(int height) {
			if (height > 0) {
				this.height = height;
			}
			return this;
		}

		public final JImageScreenBuilder setSize(int wh) {
			if (wh > 0) {
				return this.setSize(wh, wh);
			}
			return this;
		}

		public final JImageScreenBuilder setSize(int width, int height) {
			return this.setWidth(width).setHeight(height);
		}

		public final JImageScreenBuilder setScale(int scale) {
			if (scale > 0) {
				this.scale = scale;
			}
			return this;
		}

		public final JImageScreenBuilder setBackground(Color color) {
			if (color != null) {
				this.background = color;
			}
			return this;
		}

		public final JImageScreenBuilder setLocationUnder(JFrame screen) {
			if (screen != null) {
				Point location = screen.getLocation();
				this.x = location.x;
				this.y = location.y + screen.getHeight();
			}
			return this;
		}

		public final JImageFrame build() {
			return this.build(true);
		}

		public final JImageFrame build(boolean visible) {
			JImageFrame screen = new JImageFrame(this.title, this.width, this.height, this.scale, this.background);
			screen.setLocation(this.x, this.y);
			screen.setVisible(visible);
			return screen;
		}

		public final <T extends JImageFrame> T buildWith(Factory<T> factory) {
			T screen = factory.create(this.title, this.width, this.height, this.scale, this.background);
			screen.setLocation(this.x, this.y);
			return screen;
		}
	}

	public interface Factory<T extends JImageFrame> {
		T create(String frameTitle, int width, int height, int scale, Color background);
	}
}
