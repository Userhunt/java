package net.w3e.app.utils.poe2;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.skds.lib2.mat.Vec2I;
import net.skds.lib2.utils.ImageUtils;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.api.ImageUtil;
import net.w3e.app.api.window.Inputs;
import net.w3e.wlib.ColorUtil;

public class PoeCreaftHelper {

	private static final int X = 2952;
	private static final int Y = 591;
	private static final int SIZE = 49;
	private static final int HALF_SIZE = SIZE / 2;
	
	public static void main(String[] args) throws IOException {
		SKDSLogger.replaceOuts();
		System.out.println("poe");
		Map<Vec2I, Slot> inventory = loadInventory();
		System.out.println(inventory.size());
		removeColumn(inventory, 12);
		removeEmpty(inventory);

		List<Vec2I> poses = found(inventory, 
			"poe2/input/1.png",
			"poe2/input/2.png",
			"poe2/input/3.png",
			"poe2/input/4.png",
			"poe2/input/5.png",
			"poe2/input/6.png"
		);

		System.out.println("poses " + poses.size());
		System.out.println("all " + inventory.size());

		final int x = X + SIZE;
		final int y = Y + HALF_SIZE;
		while (poses.size() >= 3) {
			Inputs.ctrl(() -> {
				for (int i = 0; i < 3; i++) {
					Inputs.sleep(100);
					Vec2I slot = poses.removeFirst().subI(1, 1);
					Inputs.click(x + slot.xi() * SIZE, y + slot.yi() * SIZE);
					Inputs.sleep(100);
				}
				craft();
			});
		}
	}

	private static Map<Vec2I, Slot> loadInventory() {
		BufferedImage image = ImageUtil.capture(new Rectangle(X, Y, 631, 263));
		ImageUtil.save(image, "poe2/out");
		int width = SIZE;
		int height = SIZE;
		int[] xArray = new int[]{1, 53, 106, 159, 211, 264, 317, 369, 422, 475, 527, 580};
		int[] yArray = new int[]{1, 53, 106, 159, 211};
		Map<Vec2I, Slot> images = new HashMap<>();

		for (int x = 0; x < xArray.length; x++) {
			for (int y = 0; y < yArray.length; y++) {
				BufferedImage sub = image.getSubimage(
					xArray[x], 
					yArray[y], 
				width, height);
				int xPos = x + 1;
				int yPos = y + 1;
				Slot slot = new Slot(xPos, yPos, sub);
				images.put(new Vec2I(xPos, yPos), slot);
			}
		}
		return images;
	}

	private static void removeColumn(Map<Vec2I, Slot> inventory, int x) {
		for (int i = 0; i < 5; i++) {
			inventory.remove(new Vec2I(x, i + 1));
		}
	}

	private static void removeEmpty(Map<Vec2I, Slot> inventory) {
		inventory.entrySet().removeIf(e -> e.getValue().isEmpty());
	}

	private static List<Vec2I> found(Map<Vec2I, Slot> inventory, String... filter) throws IOException {
		List<BufferedImage> images = new ArrayList<>();
		for (String f : filter) {
			FileInputStream filterFile = new FileInputStream(f);
			BufferedImage filterImage = ImageUtils.readPNG(filterFile);
			images.add(filterImage);
			filterFile.close();
		}

		List<Vec2I> poses = new ArrayList<>();
		for (int x = 1; x <= 11; x++) {
			a: for (int y = 1; y <= 5; y++) {
				Vec2I pos = new Vec2I(x, y);
				Slot slot = inventory.get(pos);
				if (slot != null) {
					for (BufferedImage img : images) {
						if (ImageUtil.difImages(img, slot.image) <= 0.1) {
							poses.add(pos);
							continue a;
						}
					}
					slot.save();
				}
			}
		}
		return poses;
	}

	private static record Slot(int x, int y, BufferedImage image) {
		public void save() {
			ImageUtil.save(this.image, "poe2/" + this.x + "_" + this.y);
		}

		public boolean isEmpty() {
			int color = this.image.getRGB(0, 0);
			if (color == -16317182) {
				//System.out.println("b " + this.x + " " + this.y);
				return true;
			}
			if (color == -16251646) {
				//System.out.println("c " + this.x + " " +  this.y);
				return true;
			}
			return false;
		}
	}

	private static void craft() {
		int i = 10;
		final int x = 2300;
		final int y = 860;
		while (i > 0) {
			i--;
			Inputs.click(x, y);
			Inputs.sleep(100);
			Inputs.move(x, y - 500);
			Inputs.sleep(1000);
			Inputs.ctrl(() -> {
				Inputs.click();
			});
			Inputs.sleep(250);
			int[] pos = Inputs.mousePos();
			if (pos[0] != x || pos[1] != y - 500) {
				System.exit(0);
				System.out.println("exit");
			}
		}
	}
}
