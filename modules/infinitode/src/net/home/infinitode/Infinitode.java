package net.home.infinitode;

import java.awt.Component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.api.window.FrameWin;
import net.home.FrameObject;
import net.home.MainFrame;

public class Infinitode extends FrameObject {

	private final JTable display = new JTable();

	private final String[] NAMES = new String[]{"Кол-во", "1","2","3","4","5","6","7","8","9","10","11","12"};

	private static final int[][] RESETS = new int[][]{
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{180, 310, 530, 910, 1560, 2690, 4610, 0, 0, 0, 0, 0},
	};

	public static void main(String[] args) {
		MainFrame.register(new Infinitode());
		MainFrame.run(args);
	}

	@Override
	protected void init(FrameWin fw, List<String> args) {
		this.reset(RESETS[0]);
		this.display.setMinimumSize(this.display.getPreferredSize());
		List<Component> list = new ArrayList<>();

		list.add(this.display);
		list.add(this.addCmonentListiner(new JButton("Посчитать"), e -> this.calculate()));
		for (int[] reset : RESETS) {
			list.add(this.addCmonentListiner(new JButton(String.format("Сбросить (%s, %s,..., %s)", reset[0], reset[1], reset[reset.length - 1])), e -> this.reset(reset)));
		}

		this.simpleColumn(fw.getContentPane(), list);

		fw.pack();
	}

	private final void calculate() {
		List<String> priceStrings = new ArrayList<>();
		List<String> whenStrings1 = new ArrayList<>();
		List<String> whenStrings2 = new ArrayList<>();

		priceStrings.add("Цена");
		whenStrings1.add("easy");
		whenStrings2.add("endless");

		IntList prices = new IntArrayList();

		for (int i = 1; i < NAMES.length; i++) {
			String price = this.display.getValueAt(1, i).toString();
			try {
				prices.add(Integer.valueOf(price).intValue());
			} catch (Exception e) {
				int v = 180;
				if (!prices.isEmpty()) {
					v = prices.getLast() + 100;
				}
				price = String.valueOf(v);
			}
			priceStrings.add(price);
		}

		int i = 0;

		for (int price : prices) {
			int[] a = this.calculate(2.0f, 60, i, price);
			int[] b = this.calculate(2.3f, 65, i, price);
			whenStrings1.add(String.format("%s, (%s)", a[0], a[1]));
			whenStrings2.add(String.format("%s, (%s)", b[0], b[1]));
			i++;
		}

		this.display.getValueAt(1, 1);

		this.display.setModel(new DefaultTableModel(new String[][]{
			NAMES,
			priceStrings.toArray(String[]::new),
			whenStrings1.toArray(String[]::new),
			whenStrings2.toArray(String[]::new),
		}, NAMES));
		this.display.getModel().addTableModelListener(e -> calculate());
	}

	private final int[] calculate(float percent, int max, int count, int price) {
		if (count == 0) {
			return new int[]{price, 0};
		} else {
			float d = 100 / percent;
			percent /= 100;
			float value = 0;
			while (true) {
				value += d;
				int v1 = (int)Math.ceil(value);
				int v2 = v1 - price;
				if (v1 < 0 || v2 < 0) {
					continue;
				}
				v1 *= percent;
				v2 *= percent;
				v1 = Math.min(v1, max);
				v2 = Math.min(v2, max);
				v1 *= count;
				v2 *= count + 1;
				if (v1 <= v2) {
					return new int[]{(int)Math.ceil(value), v2};
				}
			}
		}
	}

	private final void reset(int... prices) {
		List<String> list = new ArrayList<>();
		list.add("");
		for (int value : prices) {
			list.add(String.valueOf(value));
		}
		this.display.setModel(new DefaultTableModel(new String[][]{
			NAMES,
			list.toArray(String[]::new),
		}, NAMES));
		this.calculate();
	}

	@Override
	public final String fastKey() {
		return "infinitode";
	}

	@Override
	public final String getName() {
		return "Infinitode";
	}

	@Override
	public final int[] version() {
		return new int[]{1,0,1};
	}
}
