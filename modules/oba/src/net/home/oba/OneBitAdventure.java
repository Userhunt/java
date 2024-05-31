package net.home.oba;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.apache.logging.log4j.LogManager;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.api.ImageUtil;
import net.api.window.FrameWin;
import net.api.window.Inputs;
import net.home.FrameObject;
import net.home.MainFrame;
import net.home.oba.ObaConfig.Display;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.message.BMessageLoggerHelper;

public class OneBitAdventure extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new OneBitAdventure());
		MainFrame.run(args);
	}

	public static final BMessageLoggerHelper MSG_UTIL = new BMessageLoggerHelper(LogManager.getLogger("OBA"));

	private static final Rectangle FIELD = new Rectangle(555, 74, 570, 780);
	private static final Rectangle AREA = new Rectangle(0, 23, 1680, 987);

	private final ObaStep step = new ObaStep();

	private ObaDisplay display;
	private final JButton displayButton;
	private final JCheckBox run;
	private final JCheckBox printStep;

	public OneBitAdventure() {
		YPos y = new YPos(5);
		this.displayButton = this.addButton("Display", btn -> {
			if (this.display != null) {
				this.display.close();
			}
			this.display = new ObaDisplay(this);
			this.display.setVisible(true);
			Container fw = this.getFrame();
			java.awt.Point location = fw.getLocation();
			this.display.setLocation(location.x, location.y + fw.getHeight());
		}, y);

		this.run = this.addCheckBox("Run", box -> {
			ObaConfig.setRun(box.isSelected());
		}, height, y);

		this.printStep = this.addCheckBox("Print step", box -> {
			ObaConfig.setDisplay(Display.step, box.isSelected());
		}, height, y);
		this.run.setSelected(true);
	}

	protected void init(FrameWin fw, List<String> args) {
		fw.setLayout(null);
		fw.getRootPane().setBorder(null);
		ObaMove.init();
		fw.setSize(300, 41 + dY() * 3);

		fw.add(this.displayButton);
		fw.add(this.printStep);
		fw.add(this.run);

		ObaConfig.main(args, this);

		this.printStep.setSelected(ObaConfig.getDisplay(ObaConfig.Display.step));
		this.run.setSelected(ObaConfig.getRun());

		fw.tick(100, this::tick);

		this.printStep.setSelected(ObaConfig.getDisplay(Display.step));
	}

	private void tick() {
		if (!run.isSelected()) {
			return;
		}

		PointMap.reset();

		BufferedImage capture = ImageUtil.capture(AREA);

		if (capture.getRGB(0, 0) == -15659747) {
			Inputs.click(840, 308);
			Inputs.click(840, 308);
			sleep(2000);
			return;
		} else {
			capture = capture.getSubimage(555, 51, 570, 930);

			this.step.tick(capture);

			capture = ImageUtil.scale(capture, 1d/3d);
			capture = ImageUtil.toRGBA(capture);

			sleep(40);
			BufferedImage field2 = ImageUtil.scale(ImageUtil.capture(FIELD), 1d/3d);
			sleep(40);
			BufferedImage field3 = ImageUtil.scale(ImageUtil.capture(FIELD), 1d/3d);
			sleep(40);
			BufferedImage field4 = ImageUtil.scale(ImageUtil.capture(FIELD), 1d/3d);

			PointMap.fill(
				createMap(capture), 
				createMap(field2),
				createMap(field3),
				createMap(field4)
			);

			if (this.display != null) {
				this.display.tick(capture);
			}

			PointMap.step(this.printStep.isSelected(), (str) -> {
				if (this.display != null) {
					this.display.log(str);
				}
			});
		}
	}

	private final Int2ObjectArrayMap<Int2ObjectArrayMap<Point>> createMap(BufferedImage capture) {
		Int2ObjectArrayMap<Int2ObjectArrayMap<Point>> map = new Int2ObjectArrayMap<>();
		int sx = 0;
		int sy = 0;
		for (int index = 0; index < 11; index++) {
			map.put(index, new Int2ObjectArrayMap<>());
		}
		for (int y = 0; y < 15; y++) {
			int dy = y % 2 == 0 ? 15 : 16;
			for (int x = 0; x < 11; x++) {
				int dx = x % 2 == 0 ? 15 : 16;
				BufferedImage image = capture.getSubimage(sx, sy, dx, dy);
				sx += dx + 2;

				map.get(x).put(y, new Point(image, x, y));
			}
			sx = 0;
			sy += dy + 2;
		}
		return map;
	}

	public void clickDisplay() {
		this.displayButton.doClick();
	}

	public void closeDisplay() {
		if (this.display != null) {
			ObaDisplay disp = this.display;
			this.display = null;
			disp.close();
		}
	}

	protected void onClose() {
		closeDisplay();
	}

	public static class Point implements Comparable<Point> {

		public final BufferedImage image;
		public final int x;
		public final int y;
		private PointType type = PointType.UNSET;
		public int path = -1;
		public double base = 1;
		public double reward = 0;

		public Point(BufferedImage image, int x, int y) {
			this.image = image;
			this.x = x;
			this.y = y;
		}

		public final void set(PointType type) {
			if (type != null) {
				this.type = type;
			}
		}

		public final void save() {
			if (image == null) {
				MSG_UTIL.warn(String.format("%s %s is null", x, y));
				return;
			}
			ImageUtil.save(image, "oba/img/" + y + "_" + x + ".png");
		}

		@Override
		public final String toString() {
			String more = "";
			if (this.path != -1) {
				more += String.format(",path:%s", path);
			}
			if (this.base != 1) {
				more += String.format(",base:%s", base);
			}
			if (this.reward != 0) {
				more += String.format(",reward:%s", reward);
			}
			return String.format("{x:%s,y:%s,type:\"%s\"%s}", x, y, type, more);
		}

		public final boolean equalsPos(Point other) {
			return other == null ? false : (this.x == other.x && this.y == other.y);
		}

		public final boolean equalsImage(Point other) {
			return other == null ? true : ImageUtil.compareImages(this.image, other.image);
		}

		public final PointType getType() {
			return this.type;
		}

		public final boolean isIn(int x, int y) {
			return image != null && x >= 0 && x <= image.getWidth() - 1 && y >= 0 && y <= image.getHeight() - 1;
		}

		public final void iterate(IteratePoint function) {
			if (image == null) {
				return;
			}
			int xM = this.image.getWidth();
			int yM = this.image.getHeight();
			for (int x = 0; x < xM; x++) {
				for (int y = 0; y < yM; y++) {
					function.iterate(x, y, this.getPixel(x, y));
				}
			}
		}

		@FunctionalInterface
		public static interface IteratePoint {
			void iterate(int x, int y, int rgba);
		}

		public final int sizeImage() {
			return this.image == null ? -1 : this.image.getWidth() * this.image.getHeight();
		}

		public final int getPixel(int x, int y) {
			if (isIn(x, y)) {
				return image.getRGB(x, y);
			} else {
				return 0;
			}
		}

		public final void setPixel(int x, int y, int rgba) {
			if (isIn(x, y)) {
				this.image.setRGB(x, y, rgba);
			}
		}

		public final boolean is(PointType type) {
			return type.is(this.x, this.y, this.image);
		}

		public final boolean test(PointType type) {
			if (this.image != null && type.is(this.x, this.y, this.image)) {
				this.set(type);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public int compareTo(Point o) {
			return BMatUtil.round(o.reward * 100) - BMatUtil.round(this.reward * 100);
		}
	}

	@Override
	public String getName() {
		return "One Bit Adventure";
	}

	@Override
	public String fastKey() {
		return "OBA";
	}

	@Override
	public int[] version() {
		return new int[]{1,0,0};
	}
}
