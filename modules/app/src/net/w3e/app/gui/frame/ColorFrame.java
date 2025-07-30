package net.w3e.app.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.skds.lib2.awtutils.SwingDocListener;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec2.Vec2I;
import net.skds.lib2.utils.ColorUtils;
import net.skds.lib2.utils.ImageUtils;
import net.w3e.app.gui.components.JNumberTextField;
import net.w3e.app.gui.utils.JFrameGuiUtils;
import net.w3e.app.gui.utils.JGuiUtils;
import net.w3e.wlib.ColorUtil;
import net.w3e.wlib.ColorUtil.ColorPackFlag;

public class ColorFrame extends JFrame implements JFrameGuiUtils {

	private final HuePanel hue = new HuePanel();
	private final RGBAPanel rgba = new RGBAPanel();

	public ColorFrame() {
		super("Color");

		addBorder();
		this.add(this.hue, BorderLayout.CENTER);
		this.add(this.rgba, BorderLayout.SOUTH);
	}

	private class HuePanel extends JPanel implements MouseListener, MouseMotionListener {

		HuePanel() {
			setPreferredSize(new Dimension(400, 400));
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}

		private class Data {
			private final int w;
			private final int h;
			private final Vec2I center;
			private final int r;
			private final int r2;
			private final int rm;
			private final int rm2;

			public Data() {
				this.w = getWidth();
				this.h = getHeight();
				this.center = new Vec2I(w / 2, h / 2);
				this.r = Math.min(w, h) / 2;
				this.r2 = r * r;
				this.rm = r / 2;
				this.rm2 = rm * rm;
			}

			private boolean isIn(int x, int y) {
				float d2 = this.center.squareDistanceToF(x, y);
				return d2 > this.rm2 && d2 < this.r2;
			}

			private Color hue(int x, int y) {
				float hue = .5f + (float) Math.atan2(y - this.center.yi(), x - this.center.xi()) / FastMath.TWO_PI;
				return new Color(ColorUtils.packARGB(ColorUtils.hueRGB(hue), 255));
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			Data data = new Data();

			BufferedImage image = ImageUtils.drawPerPixel(data.w, data.h, (x, y) -> {
				if (!data.isIn(x, y)) {
					return 0;
				}
				return data.hue(x, y).hashCode();
			});

			g2d.drawImage(image, 0, 0, data.w, data.h, null);
		}

		private void updateColor(Point point) {
			Data data = new Data();
			if (data.isIn(point.x, point.y)) {
				Color color = data.hue(point.x, point.y);
				rgba.r = color.getRed() / 255f;
				rgba.g = color.getGreen() / 255f;
				rgba.b = color.getBlue() / 255f;
				rgba.a = color.getAlpha() / 255f;
				rgba.update();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			updateColor(e.getPoint());
		}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {
			updateColor(e.getPoint());
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}

	private class RGBAPanel extends JPanel {

		private static final int WIDTH = 28;
		private static final int HEIGHT = 26;
		private static final int PRECISION = 4;

		private float r = 1;
		private float g = 1;
		private float b = 1;
		private float a = 1;

		private final Int2ObjectArrayMap<RGBAField> fields = new Int2ObjectArrayMap<>();
		private boolean update = true;
		private final JPanel colorPanel;

		public RGBAPanel() {
			this.setLayout(new BorderLayout());
			for(ColorPackFlag flag : ColorPackFlag.values()) {
				int index = flag.index + 1;
				this.fields.put(index, null);
				this.fields.put(-index, null);
			}
			for(int key : new IntOpenHashSet(fields.keySet())) {
				this.fields.put(key, null);
			}

			for(int key : new IntOpenHashSet(fields.keySet())) {
				this.fields.put(key, null);
			}

			int width = RGBAPanel.WIDTH + 18;
			JPanel lines = new JPanel();
			lines.setLayout(new BoxLayout(lines, BoxLayout.Y_AXIS));
			JPanel line1 = this.createLine(width, true);
			JPanel line2 = this.createLine(width, false);
			lines.add(line1);
			lines.add(line2);
			this.add(lines, BorderLayout.CENTER);

			this.colorPanel = new JPanel() {
				@Override
				public Dimension getSize() {
					return new Dimension(getParent().getWidth() - 10, getParent().getHeight() - 10);
				}
			};

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setBackground(Color.BLACK);
			JGuiUtils.addBorder(panel, 5);
			panel.add(colorPanel);
			this.add(panel, BorderLayout.SOUTH);

			this.update();
		}

		private final JPanel createLine(int width, boolean i) {
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
			createNumberField(panel, width, i, ColorPackFlag.R);
			panel.add(Box.createHorizontalStrut(5));
			createNumberField(panel, width, i, ColorPackFlag.G);
			panel.add(Box.createHorizontalStrut(5));
			createNumberField(panel, width, i, ColorPackFlag.B);
			panel.add(Box.createHorizontalStrut(5));
			createNumberField(panel, width, i, ColorPackFlag.A);
			return panel;
		}

		private final void createNumberField(JPanel panel, int width, boolean i, ColorPackFlag flag) {
			JLabel label = new JLabel();
			label.setText(flag.name());
			panel.add(label);

			JNumberTextField field = new JNumberTextField(i ? 3 : 3 + RGBAPanel.PRECISION, i ? JNumberTextField.NUMERIC : JNumberTextField.DECIMAL);
			field.setAllowNegative(false);
			if (!i) {
				field.setPrecision(RGBAPanel.PRECISION);
			}
			RGBAField f = new RGBAField(this, field, flag, i);

			field.getDocument().addDocumentListener(new SwingDocListener(_ -> {
				if (update) {
					f.edit();
				}
			}));

			JGuiUtils.setSize(field, width, RGBAPanel.HEIGHT);

			panel.add(field);

			this.fields.put(f.pack(), f);
		}

		private final float clamp(float value) {
			return FastMath.clamp(value, 0f, 1f);
		}

		private final void update() {
			this.updateFields();
			this.updateColor();
		}

		public final void updateFields() {
			Color rgba = new Color(this.clamp(this.r), this.clamp(this.g), this.clamp(this.b), this.clamp(this.a));
			this.r = rgba.getRed();
			this.g = rgba.getGreen();
			this.b = rgba.getBlue();
			this.a = rgba.getAlpha();
			this.update = false;
			for (RGBAField runnable : fields.values()) {
				runnable.apply();
			}
			this.update = true;
		}

		public final void updateColor() {
			this.colorPanel.setBackground(new Color((int)this.r, (int)this.g, (int)this.b, (int)this.a));
			//this.backgroundPanel.repaint();
		}

		private record RGBAField(RGBAPanel panel, JNumberTextField field, ColorPackFlag flag, boolean i) {
			public byte pack() {
				return (byte) ((i ? 1 : -1) * (flag.index + 1));
			}

			public final void edit() {
				float value = panel.clamp(this.i ? (float)ColorUtil.toDouble(field.getInt()) : field.getFloat());
				value = panel.clamp(value);
				value = ColorUtil.toInt(value);
				switch(flag) {
					case A:
						panel.a = value;
						break;
					case B:
						panel.b = value;
						break;
					case G:
						panel.g = value;
						break;
					case R:
						panel.r = value;
						break;
				}
				boolean old = panel.update;
				panel.update = false;
				panel.fields.get(this.pack() * -1).apply();
				panel.update = old;
				panel.updateColor();
			}

			private final void apply() {
				Color color = new Color((int)panel.r, (int)panel.g, (int)panel.b, (int)panel.a);
				if (i) {
					field.setNumber(flag.getInt(color));
				} else {
					field.setNumber((float)flag.getDouble(color));
				}
			}

			@Override
			public String toString() {
				return String.format("{flag:%s,int:%s}", this.flag, this.i);
			}
		}
	}
}
