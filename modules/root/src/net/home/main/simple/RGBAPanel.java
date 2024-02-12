package net.home.main.simple;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.api.window.FrameWin;
import net.api.window.jcomponent.JNumberTextField;
import net.w3e.base.RGBA;
import net.w3e.base.RGBA.RGBAFlag;
import net.w3e.base.math.BMatUtil;

public class RGBAPanel {

	private static final int WIDTH = 28;
	private static final int D = 10;
	private static final int HEIGHT = 26;
	private static final int PRECISION = 4;

	private float r = 1;
	private float g = 1;
	private float b = 1;
	private float a = 1;

	private final Int2ObjectArrayMap<RGBAField> fields = new Int2ObjectArrayMap<>();
	private boolean update = true;
	private final JPanel panel = new JPanel();

	public RGBAPanel() {
		for(RGBAFlag flag : RGBAFlag.values()) {
			int index = flag.index + 1;
			this.fields.put(index, null);
			this.fields.put(-index, null);
		}
	}

	public final int create(FrameWin fw, int x, int y, boolean i, boolean d) {
		int old = y;
		for(int key : new IntOpenHashSet(fields.keySet())) {
			this.fields.put(key, null);
		}

		int width = this.width(i, d);
		int height = RGBAPanel.HEIGHT + 5;
		int dX1 = width + RGBAPanel.D + 5;
		int dX2 = dX1 + dX1;
		int dX3 = dX2 + dX1;
		x += 5;
		if (i) {
			createNumber(fw, width, x, y, true, RGBAFlag.R);
			createNumber(fw, width, x + dX1, y, true, RGBAFlag.G);
			createNumber(fw, width, x + dX2, y, true, RGBAFlag.B);
			createNumber(fw, width, x + dX3, y, true, RGBAFlag.A);
			y += height;
		}
		if (d) {
			createNumber(fw, width, x, y, false, RGBAFlag.R);
			createNumber(fw, width, x + dX1, y, false, RGBAFlag.G);
			createNumber(fw, width, x + dX2, y, false, RGBAFlag.B);
			createNumber(fw, width, x + dX3, y, false, RGBAFlag.A);
			y += height;
		}
		if (i || d) {
			y += 5;
			JPanel panel = new JPanel();
			panel.setBackground(Color.BLACK);

			panel.setBounds(x - 5, y - 5, dX3 + dX1 + 5, RGBAPanel.HEIGHT + 10);

			this.panel.setBounds(x, y, dX3 + dX1 - 5, RGBAPanel.HEIGHT);

			fw.add(this.panel);
			fw.add(panel);

			y += 5;

			y += height;
		}

		this.update();

		return y - old;
	}

	private final void createNumber(FrameWin fw, int width, int x, int y, boolean i, RGBA.RGBAFlag flag) {
		JLabel label = new JLabel();
		label.setBounds(x, y, RGBAPanel.D, RGBAPanel.HEIGHT);
		label.setText(flag.name());
		fw.add(label);

		JNumberTextField field = new JNumberTextField(i ? 3 : 1, i ? JNumberTextField.NUMERIC : JNumberTextField.DECIMAL);
		field.setAllowNegative(false);
		if (!i) {
			field.setPrecision(RGBAPanel.PRECISION);
		}
		RGBAField f = new RGBAField(this, field, flag, i);

		field.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				edit();
			}
			public void removeUpdate(DocumentEvent e) {
				edit();
			}
			public void insertUpdate(DocumentEvent e) {
				edit();
			}
		
			public void edit() {
				if (update) {
					f.edit();
				}
			}
		});

		field.setBounds(x + D, y, width, RGBAPanel.HEIGHT);
		fw.add(field);

		this.fields.put(f.pack(), f);
	}

	private final float clamp(float value) {
		return BMatUtil.round(BMatUtil.clamp(value, 0f, 1f), RGBAPanel.PRECISION);
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
		RGBA rgba = new RGBA(this.clamp(this.r), this.clamp(this.g), this.clamp(this.b), this.clamp(this.a));
		this.r = rgba.r();
		this.g = rgba.g();
		this.b = rgba.b();
		this.a = rgba.a();
		this.update = false;
		for (RGBAField runnable : fields.values()) {
			runnable.apply();
		}
		this.update = true;
	}

	public final void updateColor() {
		this.panel.setBackground(new Color(this.r, this.g, this.b, 1));
	}

	private record RGBAField(RGBAPanel panel, JNumberTextField field, RGBAFlag flag, boolean i) {
		public byte pack() {
			return (byte) ((i ? 1 : -1) * (flag.index + 1));
		}

		public final void edit() {
			float value = panel.clamp(this.i ? (float)RGBA.calc(field.getInt()) : field.getFloat());
			value = panel.clamp(value);
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
			RGBA color = new RGBA(panel.r, panel.g, panel.b, panel.a);
			float value = (float)flag.getterDouble.get(color);
			if (i) {
				field.setNumber(flag.getterInt.get(color));
			} else {
				field.setNumber(value);
			}
		}

		@Override
		public String toString() {
			return String.format("{flag:%s,int:%s}", this.flag, this.i);
		}
	}
}
