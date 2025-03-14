package net.w3e.app.gui.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface JImageGuiUtils {

	BufferedImage getImage();

	void setImage(BufferedImage image);

	void updateImage();

	default void setColor(int x, int y, Color color) {
		this.setColor(x, y, color.getRGB());
	}

	default void setColor(int x, int y, int color) {
		this.getImage().setRGB(x, y, color);
	}

	default void fillImageWhite() {
		this.fillImage(Color.WHITE);
	}

	default void fillImage(Color color) {
		fillImage(color.getRGB());
	}

	default void fillImage(int color) {
		BufferedImage image = this.getImage();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				image.setRGB(x, y, color);
			}
		}
	}
}
