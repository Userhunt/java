package net.w3e.app.api;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.w3e.lib.utils.FileUtils;

public class ImageUtil {

	public static BufferedImage capture(Rectangle rectangle) {
		try {
			return new Robot().createScreenCapture(rectangle);
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage deepCopy(BufferedImage source) {
		ColorModel cm = source.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = source.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	// TODO через растер
	public static BufferedImage toRGBA(BufferedImage source) {
		//BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		//Graphics g = image.getGraphics();
		//g.drawImage(source, 0, 0, null);
		//g.dispose();
		//return image;

		BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);  
		ColorConvertOp op = new ColorConvertOp(source.getColorModel().getColorSpace(), image.getColorModel().getColorSpace(), null);
		op.filter(source, image);
		return image;
	}

	public static BufferedImage toGRAY(BufferedImage source) {
		BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);  
		ColorConvertOp op = new ColorConvertOp(source.getColorModel().getColorSpace(), image.getColorModel().getColorSpace(), null);
		op.filter(source, image);
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

	public static void save(BufferedImage image, String path){
		if (path.contains("/") || path.contains("\\")) {
			FileUtils.createParentDirs(new File(path));
		}
		File imageFile = new File(path + ".png");
		try {
			ImageIO.write(image, "png", imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
