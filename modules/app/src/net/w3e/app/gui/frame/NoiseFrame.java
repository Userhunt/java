package net.w3e.app.gui.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.skds.lib2.mat.FastMath;
import net.w3e.app.gui.components.JButtonGroup;
import net.w3e.app.gui.components.JImageFitPanel;
import net.w3e.app.gui.components.JImagePanel;
import net.w3e.app.gui.utils.JFrameGuiUtils;
import net.w3e.wlib.mat.OpenSimplex2S;
import net.w3e.wlib.mat.WMatUtil;

public class NoiseFrame extends JFrame implements ChangeListener, JFrameGuiUtils {

	private final JSlider seedSlider;
	private final JSlider maxSlider;
	private final JSlider minSlider;
	private final JSlider scaleSlider;
	private final JSlider niceSlider;
	private final JButtonGroup<Mode> mode = new JButtonGroup<>();
	private final JLabel values = new JLabel("null");
	private final JImagePanel image;
	private final int size = 256;

	public NoiseFrame() {
		super("Noise");

		this.addBorder();
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		this.seedSlider = this.initSlider(0);
		this.maxSlider = this.initSlider(100);
		this.minSlider = this.initSlider(-100);
		this.scaleSlider = this.initSlider(1);
		this.niceSlider = this.initSlider(0);
		this.niceSlider.setMinimum(0);
		this.niceSlider.setMaximum(255);

		this.add(new JLabel("Seed"));
		this.add(this.seedSlider);
		this.add(Box.createVerticalStrut(10));
		this.add(new JLabel("Max"));
		this.add(this.maxSlider);
		this.add(Box.createVerticalStrut(10));
		this.add(new JLabel("Min"));
		this.add(this.minSlider);
		this.add(Box.createVerticalStrut(10));
		this.add(new JLabel("Scale"));
		this.add(this.scaleSlider);
		this.add(Box.createVerticalStrut(10));
		this.add(new JLabel("Nice"));
		this.add(this.niceSlider);
		this.add(Box.createVerticalStrut(10));
		this.add(values);
		this.add(Box.createVerticalStrut(10));

		this.initRadioButton(Mode.normal);
		this.initRadioButton(Mode.range);
		this.initRadioButton(Mode.nice);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(1, 1, 1, 1));
		panel.setPreferredSize(new Dimension(500 + 10, 500 + 10));
		this.image = new JImageFitPanel(new BufferedImage(this.size, this.size, BufferedImage.TYPE_INT_ARGB), 500, 500, false);
		panel.add(image);
		panel.setBackground(Color.RED);
		this.add(panel);

		this.change();
	}

	private final JSlider initSlider(int value) {
		JSlider slider = new JSlider(-100, 100, value);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing(10);
		slider.setMajorTickSpacing(50);
		slider.addChangeListener(this);
		return slider;
	}

	private final void initRadioButton(Mode mode) {
		JRadioButton radio = this.mode.new JRadioButton(mode.name().substring(0, 1).toUpperCase() + mode.name().substring(1), mode);
		radio.setSelected(mode.selected);
		radio.addChangeListener(this);

		this.mode.add(radio);

		this.add(radio);
		this.add(Box.createVerticalStrut(10));
	}

	private final void change() {
		int seed = this.seedSlider.getValue();
		double min = this.minSlider.getValue() / 100d;
		double max = this.maxSlider.getValue() / 100d;
		double scale = this.scaleSlider.getValue();
		int nice = this.niceSlider.getValue();
		this.values.setText(String.format("Seed: %s, min: %s, max: %s, scale: %s, nice: %s", seed, min, max, scale < 0 ? String.format("1 / %s", -this.scaleSlider.getValue()) : this.scaleSlider.getValue(), nice));
		if (scale < 0) {
			scale = -1d / scale;
		}

		double[][] array = new double[this.size][];
		for (int i = 0; i < this.size; i++) {
			array[i] = new double[this.size];
		}

		for (int x = 0; x < this.size; x++) {
			for (int z = 0; z < this.size; z++) {
				array[x][z] = OpenSimplex2S.noise2(seed, (x - this.size / 2) * scale, (z - this.size / 2) * scale);
			}
		}

		int green = Color.GREEN.getRGB();
		int blue = Color.BLUE.getRGB();

		Mode mode = this.mode.getSelectedButtonArg();
		if (mode == null) {
			return;
		}

		for (int x = 0; x < this.size; x++) {
			for (int z = 0; z < this.size; z++) {
				double value = array[x][z];
				switch (mode) {
					case normal -> {
						if (min != -1d && value <= min) {
							this.image.setColor(x, z, blue);
							continue;
						}
						if (max != 1d && value >= max) {
							this.image.setColor(x, z, green);
							continue;
						}
						int color = FastMath.round(WMatUtil.mapRange(value, min, max, 0, 255));
						this.image.setColor(x, z, new Color(color, color, color));
					}
					case range -> {
						if (value >= min && value <= max) {
							this.image.setColor(x, z, blue);
						} else {
							this.image.setColor(x, z, green);
						}
					}
					case nice -> {
						if (value < 0) {
							this.image.setColor(x, z, new Color(0, 0, FastMath.round(WMatUtil.mapRange(value, -1, 0, 0, 255 - nice) + nice)));
						} else {
							int color = 255 - FastMath.round(WMatUtil.mapRange(value, 0, 1, 0, 255 - nice));
							this.image.setColor(x, z, new Color(0, color, 0));
						}
					}
				}
			}
		}
		this.image.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		change();
	}

	private enum Mode {
		normal(true),
		range,
		nice;

		private final boolean selected;

		private Mode() {
			this(false);
		}

		private Mode(boolean selected) {
			this.selected = selected;
		}
	}
}
