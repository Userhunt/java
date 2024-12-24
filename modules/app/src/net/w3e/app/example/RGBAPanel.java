package net.w3e.app.example;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.skds.lib2.awtutils.SwingDocListener;
import net.skds.lib2.mat.FastMath;
import net.w3e.app.api.window.FrameWin;
import net.w3e.app.api.window.jcomponent.JNumberTextField;
import net.w3e.lib.utils.ColorUtil;
import net.w3e.lib.utils.ColorUtil.ColorPackFlag;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

public class RGBAPanel extends JPanel {

	private static final int WIDTH = 28;
	private static final int HEIGHT = 26;
	private static final int PRECISION = 4;

	private float r = 1;
	private float g = 1;
	private float b = 1;
	private float a = 1;

	private final Int2ObjectArrayMap<RGBAField> fields = new Int2ObjectArrayMap<>();
	private boolean update = true;
	private final JPanel backgroundPanel = new JPanel();
	private final JPanel colorPanel = new JPanel();

	/**
	 * 
	 * @param i - int fields
	 * @param d - double fields
	 */
	public RGBAPanel(boolean i, boolean d) {
		for(ColorPackFlag flag : ColorPackFlag.values()) {
			int index = flag.index + 1;
			if (i) {
				this.fields.put(index, null);
			}
			if (d) {
				this.fields.put(-index, null);
			}
		}
		for(int key : new IntOpenHashSet(fields.keySet())) {
			this.fields.put(key, null);
		}

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		for(int key : new IntOpenHashSet(fields.keySet())) {
			this.fields.put(key, null);
		}

		int width = this.width(i, d);
		if (i) {
			this.createLine(width, true);
		}
		if (d) {
			this.createLine(width, false);
		}
		if (i || d) {
			this.backgroundPanel.setLayout(null);
			this.backgroundPanel.setBackground(Color.BLACK);

			width = ((width + 22) * 4 + 6 * 3);

			FrameWin.setSize(backgroundPanel, width, RGBAPanel.HEIGHT + 10);

			this.colorPanel.setBounds(5, 5, width - 10, RGBAPanel.HEIGHT);

			this.backgroundPanel.add(this.colorPanel);

			this.add(backgroundPanel);

			FrameWin.setSize(this, width, this.getPreferredSize().height);
		}

		this.update();
	}

	private final void createLine(int width, boolean i) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
		createNumberField(panel, width, i, ColorPackFlag.R);
		panel.add(Box.createHorizontalStrut(5));
		createNumberField(panel, width, i, ColorPackFlag.G);
		panel.add(Box.createHorizontalStrut(5));
		createNumberField(panel, width, i, ColorPackFlag.B);
		panel.add(Box.createHorizontalStrut(5));
		createNumberField(panel, width, i, ColorPackFlag.A);
		this.add(panel);
	}

	private final void createNumberField(JPanel panel, int width, boolean i, ColorPackFlag flag) {
		JLabel label = new JLabel();
		label.setText(flag.name());
		panel.add(label);

		JNumberTextField field = new JNumberTextField(i ? 3 : 1, i ? JNumberTextField.NUMERIC : JNumberTextField.DECIMAL);
		field.setAllowNegative(false);
		if (!i) {
			field.setPrecision(RGBAPanel.PRECISION);
		}
		RGBAField f = new RGBAField(this, field, flag, i);

		field.getDocument().addDocumentListener(new SwingDocListener(e -> {
			if (update) {
				f.edit();
			}
		}));

		FrameWin.setSize(field, width, RGBAPanel.HEIGHT);

		panel.add(field);

		this.fields.put(f.pack(), f);
	}

	private final float clamp(float value) {
		return FastMath.clamp(value, 0f, 1f);
	}

	public final int width(boolean i, boolean d) {
		if (i || d) {
			int w = RGBAPanel.WIDTH;
			if (d) {
				w += 18;
			}
			return w;
		} else {
			return RGBAPanel.HEIGHT;
		}
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
		this.backgroundPanel.repaint();
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
			panel.update = false;
			panel.fields.get(this.pack() * -1).apply();
			panel.update = true;
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
