package net.api;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.w3e.base.math.BMatUtil;

public class ImageUtil {

	public static BufferedImage capture(Rectangle rectangle) {
		try {
			return new Robot().createScreenCapture(rectangle);
		} catch (AWTException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage scale(BufferedImage image, double scale) {
		int w = BMatUtil.round(image.getWidth() * scale);
		int h = BMatUtil.round(image.getHeight() * scale);
		BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return scaleOp.filter(image, after);
	}

	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static BufferedImage toRGBA(BufferedImage bi) {
		BufferedImage image = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(bi, 0, 0, null);
		return image;
	}

	public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
		// The images must be the same size.
		if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
			return false;
		}

		int width  = imgA.getWidth();
		int height = imgA.getHeight();

		// Loop over every pixel.
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Compare the pixels for equality.
				if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
					return false;
				}
			}
		}

		return true;
	}

	public static float difImages(BufferedImage imgA, BufferedImage imgB) {
		float diff = 0;
		int w = imgA.getWidth();
		int h = imgA.getHeight();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				Color a = new Color(imgA.getRGB(x,y));
				Color b = new Color(imgB.getRGB(x,y));
				diff += Math.abs(a.getRed() - b.getRed());
				diff += Math.abs(a.getBlue() - b.getBlue());
				diff += Math.abs(a.getGreen() - b.getGreen());
			}
		}
		return diff / (w * h * 3);
		//return ((float)(diff)) / ( w * h * 3)
	}

	public static float difImagesNoAlpha(BufferedImage mask, BufferedImage imgB) {
		float diff = 0;
		int w = Math.min(mask.getWidth(), imgB.getWidth());
		int h = Math.min(mask.getHeight(), imgB.getHeight());
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int color = mask.getRGB(x,y);
				if (color == 0) {
					continue;
				}
				Color a = new Color(color);
				Color b = new Color(imgB.getRGB(x,y));
				diff += Math.abs(a.getRed() - b.getRed());
				diff += Math.abs(a.getBlue() - b.getBlue());
				diff += Math.abs(a.getGreen() - b.getGreen());
			}
		}
		return diff / (w * h * 3);
		//return ((float)(diff)) / ( w * h * 3)
	}

	public static void save(BufferedImage capture, String path){
		if (path.contains("/") || path.contains("\\")) {
			new File(new File(path).getParent()).mkdirs();
		}
		File imageFile = new File(path + ".png");
		try {
			ImageIO.write(capture, "png", imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage read(InputStream resource) {
		try {
			return ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
