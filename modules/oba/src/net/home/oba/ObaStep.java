package net.home.oba;

import java.awt.image.BufferedImage;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import net.api.ImageUtil;
import net.home.oba.ObaConfig.Save;
import net.w3e.base.RGBA;
import net.w3e.base.jar.JarUtil;

public class ObaStep {

	public static final RGBA TEXT = new RGBA(255, 229, 152);
	private static final Int2ObjectArrayMap<BufferedImage> NUMBERS = new Int2ObjectArrayMap<>();
	static {
		load(8);
		for (int i = 0; i < 10; i++) {
			load(i);
		}
	}

	private static final void load(int i) {
		if (!NUMBERS.containsKey(i)) {
			NUMBERS.put(i, ImageUtil.read(JarUtil.getResourceAsStream("oba/assets/numbers/" + i + ".png")));
		}
	}

	private int timer;
	private BufferedImage image;
	private int value = 0;

	public void increase() {
		this.value++;
	}

	public void tick(BufferedImage image) {
		this.timer--;
		if (timer <= 0) {
			this.value = 0;
			this.timer = 10;
			this.image = ImageUtil.toRGBA(image.getSubimage(200, 63, 175, 28));
			this.loop();
			if (ObaConfig.getSave(Save.number)) {
				ImageUtil.save(this.image, "oba/number");
			}
		}
	}

	public int value() {
		return this.value;
	}

	private void loop() {
		boolean find = false;
		while(this.findStart()) {
			find = false;
			for (Entry<BufferedImage> entry : NUMBERS.int2ObjectEntrySet()) {
				BufferedImage number = entry.getValue();
				if (ImageUtil.difImagesNoAlpha(number, this.image) == 0) {
					this.value *= 10;
					this.value += entry.getIntKey();
					int x = number.getWidth() + 2;
					this.image = this.image.getSubimage(x, 0, this.image.getWidth() - x, 28);
					find = true;
					break;
				}
			}
			if (find) {
				continue;
			}
			if (this.image.getWidth() > 10) {
				this.image = this.image.getSubimage(1, 0, this.image.getWidth() - 1, 28);
			} else {
				return;
			}
		}
	}

	private boolean findStart() {
		for (int x = 0; x < this.image.getWidth(); x++) {
			for (int y = 0; y < 28; y++) {
				if (this.image.getRGB(x, y) == TEXT.packBGRA()) {
					this.image = this.image.getSubimage(x, 0, this.image.getWidth() - x, 28);
					return true;
				}
			}
		}
		return false;
	}
}
