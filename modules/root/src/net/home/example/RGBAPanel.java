package net.home.example;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
	private final JPanel panel = new JPanel();

	/**
	 * 
	 * @param i - int fields
	 * @param d - double fields
	 */
	public RGBAPanel(boolean i, boolean d) {
		for(RGBAFlag flag : RGBAFlag.values()) {
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
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBackground(Color.BLACK);

			width = ((width + 22) * 4 + 5 * 3);

			FrameWin.setSize(panel, width, RGBAPanel.HEIGHT + 10);

			this.panel.setBounds(5, 5, width - 10, RGBAPanel.HEIGHT);

			panel.add(this.panel);

			this.add(panel);

			FrameWin.setSize(this, width, this.getPreferredSize().height);
		}

		this.update();
	}

	private final void createLine(int width, boolean i) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
		createNumber(panel, width, i, RGBAFlag.R);
		panel.add(Box.createHorizontalStrut(5));
		createNumber(panel, width, i, RGBAFlag.G);
		panel.add(Box.createHorizontalStrut(5));
		createNumber(panel, width, i, RGBAFlag.B);
		panel.add(Box.createHorizontalStrut(5));
		createNumber(panel, width, i, RGBAFlag.A);
		this.add(panel);
	}

	private final void createNumber(JPanel panel, int width, boolean i, RGBA.RGBAFlag flag) {
		JLabel label = new JLabel();
		label.setText(flag.name());
		panel.add(label);

		JNumberTextField field = new JNumberTextField(i ? 3 : 1, i ? JNumberTextField.NUMERIC : JNumberTextField.DECIMAL);
		field.setAllowNegative(false);
		if (!i) {
			field.setPrecision(RGBAPanel.PRECISION);
		}
		RGBAField f = new RGBAField(this, field, flag, i);

		field.getDocument().addDocumentListener(new DocumentListener() {
			public final void changedUpdate(DocumentEvent e) {
				edit();
			}
			public final void removeUpdate(DocumentEvent e) {
				edit();
			}
			public final void insertUpdate(DocumentEvent e) {
				edit();
			}

			public final void edit() {
				if (update) {
					f.edit();
				}
			}
		});

		FrameWin.setSize(field, width, RGBAPanel.HEIGHT);

		panel.add(field);

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
