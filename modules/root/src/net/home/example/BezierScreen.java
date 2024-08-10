package net.home.example;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import net.api.window.AbstractFrameWin;
import net.api.window.FrameWin;
import net.api.window.jcomponent.JButtonGroup;
import net.api.window.jcomponent.JImageLabel;
import net.home.MainFrame;
import net.w3e.base.collection.IdentityLinkedHashMap;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.BezierCurve;
import net.w3e.base.math.vector.IWVector;
import net.w3e.base.math.vector.d.WVector3D;
import net.w3e.base.math.vector.i.WVector2I;

public class BezierScreen extends AbstractFrameWin implements MouseListener, MouseMotionListener {

	private static final int SIZE = 350;
	private static final int HALF = SIZE / 2;
	private static final int SCALE = 2;
	private static final Font FONT = new Font("Arial", Font.BOLD, 12);

	private final List<JRadioButton> buttons = IntStream.rangeClosed(0, 2).mapToObj(i -> new JRadioButton(String.valueOf(i))).toList();
	private final JImageLabel image;

	private WVector2I first = new WVector2I(-SIZE / 2 + 50, 0);
	private WVector2I second = new WVector2I(SIZE / 2 - 50, 0);
	private final List<WVector2I> points = new ArrayList<>();
	private SelectedPoint selected = null;

	public BezierScreen(FrameWin frameWin) {
		super("Bezier");

		this.buttons.get(0).setSelected(true);

		JButtonGroup<JRadioButton> group = new JButtonGroup<>();
		JPanel radio = new JPanel();
		radio.setLayout(new BoxLayout(radio, BoxLayout.X_AXIS));
		for (JRadioButton button : this.buttons) {
			button.addChangeListener(e -> this.update(false));
			group.add(button);
			radio.add(button);
		}

		this.add(radio);

		BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		JPanel imagePanel = new JPanel();
		imagePanel.setBorder(BorderFactory.createEmptyBorder(SCALE, SCALE, SCALE, SCALE));
		imagePanel.setBackground(Color.black);
		this.image = new JImageLabel(image, SIZE * SCALE, SIZE * SCALE, false);
		this.image.addMouseListener(this);
		this.image.addMouseMotionListener(this);
		imagePanel.add(this.image);
		this.add(imagePanel);

		this.update(true);
		
		this.pack();
		this.atRightPosition(frameWin);
		this.setVisible(true);
	}

	private final WVector2I transform(IWVector<?> vector) {
		int x = vector.getXI() + HALF;
		int z = HALF - vector.getZI();
		return new WVector2I(x, z);
	}

	private final void update(boolean force) {
		int i = 0;
		boolean find = force;
		for (JRadioButton jRadioButton : this.buttons) {
			if (jRadioButton.isSelected()) {
				find = true;
				break;
			}
			i++;
		}
		if (!find) {
			return;
		}
		while (this.points.size() != i) {
			if (this.points.size() < i) {
				this.points.add(new WVector2I(0, 0));
			} else {
				this.points.removeLast();
			}
		}

		this.image.setColor(Color.white);

		Graphics2D g = (Graphics2D)this.image.getImage().getGraphics();
		g.setColor(Color.black);
		g.setFont(FONT);

		this.paintPoint(g, this.first, "A");
		this.paintPoint(g, this.second, "B");

		Float2ObjectFunction<WVector3D> function = null;
		if (i == 0) {
			function = (t) -> BezierCurve.curve2(t, this.first, this.second);
		} else if (i == 1) {
			function = (t) -> BezierCurve.curve3(t, this.first, this.points.get(0), this.second);
		} else if (i == 2) {
			function = (t) -> BezierCurve.curve4(t, this.first, this.points.get(0), this.points.get(1), this.second);
		}

		long time = System.currentTimeMillis();
		for (int j = 0; j <= 10000; j++) {
			Float t = (j / 10000f);
			WVector2I point = this.transform(function.apply(t));
			int x = point.getXI();
			int z = point.getZI();
			for (int k1 = 0; k1 < 2; k1++) {
				for (int k2 = 0; k2 < 2; k2++) {
					int x1 = x + k1;
					int z1 = z + k2;
					if (x1 >= 0 && x1 < SIZE && z1 >= 0 && z1 < SIZE) {
						this.image.setColor(x1, z1, Color.black);
					}
				}
			}
		}
		time = System.currentTimeMillis() - time;
		if (time > 2) {
			System.out.println(time);
		}

		i = 0;
		for (WVector2I point : this.points) {
			this.paintPoint(g, point, String.valueOf(i + 1));
			i++;
		}

		g.dispose();

		this.image.repaint();
	}

	private final void paintPoint(Graphics2D g, WVector2I point, String text) {
		try {
			point = this.transform(point);
			int x = point.getXI();
			int z = point.getZI();
			this.image.setColor(x, z, Color.black);
	
			g.setColor(Color.green);
			g.drawString(text, x - FONT.getSize() / 3, z - FONT.getSize() / 3);
			g.setColor(Color.black);
	
			int size = 6;
			int s = size / 2;
			g.drawArc(x - s, z - s, size, size, 0, 360);
			size -= 2;
			s = size / 2;
			g.drawArc(x - s, z - s, size, size, 0, 360);
		} catch (Exception e) {
			MainFrame.LOGGER.warn(text);
			MainFrame.LOGGER.warn(point);
			e.printStackTrace();
		}

	}

	@Override
	public final void mousePressed(MouseEvent e) {
		Map<WVector2I, SelectedPoint> map = new IdentityLinkedHashMap<>();
		if (this.points.size() >= 1) {
			map.put(this.points.get(0), SelectedPoint.LIST_0);
		}
		if (this.points.size() >= 2) {
			map.put(this.points.get(1), SelectedPoint.LIST_1);
		}
		map.put(this.first, SelectedPoint.FIRST);
		map.put(this.second, SelectedPoint.SECOND);

		Point mousePoint = e.getPoint();
		WVector2I mouse = new WVector2I(mousePoint.x, mousePoint.y);
		for (Entry<WVector2I, SelectedPoint> entry : map.entrySet()) {
			if (this.isAround(entry.getKey(), mouse)) {
				this.selected = entry.getValue();
				break;
			}
		}
	}

	private final boolean isAround(WVector2I point, WVector2I mouse) {
		point = this.transform(point).scale(2);
		return Math.abs(point.getXI() - mouse.getXI()) <= 6 && Math.abs(point.getZI() - mouse.getZI()) <= 6;
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		this.selected = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (this.selected != null) {
			Point mousePoint = e.getPoint();

			WVector2I mouse = new WVector2I(mousePoint.x, mousePoint.y).scale(.5, 0, -.5).add(-HALF, 0, HALF);
			mouse = new WVector2I(BMatUtil.clamp(mouse.getXI(), -HALF, HALF - 1), BMatUtil.clamp(mouse.getZI(), -HALF + 1, HALF));

			switch (this.selected) {
				case FIRST -> this.first = mouse;
				case SECOND -> this.second = mouse;
				case LIST_0 -> this.points.set(0, mouse);
				case LIST_1 -> this.points.set(1, mouse);
			}
			
			this.update(true);
		}
	}

	private enum SelectedPoint {
		FIRST,
		SECOND,
		LIST_0,
		LIST_1,
	}

	@Override
	public final void mouseClicked(MouseEvent e) {}

	@Override
	public final void mouseEntered(MouseEvent e) {}

	@Override
	public final void mouseExited(MouseEvent e) {}

	@Override
	public final void mouseMoved(MouseEvent e) {}

}
