package net.w3e.app.gui.utils;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.skds.lib2.shapes.AABB;
import net.w3e.wlib.mat.WAlign;
import net.w3e.wlib.mat.WAlign.WAlignData;
import net.w3e.wlib.mat.WIntRectangle;

public interface JFrameGuiUtils extends JGuiUtils {

	//boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-Ddebug");

	default JFrame getAsJFrame() {
		return (JFrame)this;
	}

	default JComponent getAsJComponent() {
		return (JComponent)getAsJFrame().getContentPane();
	}

	default JFrame getParentFrame() {
		return null;
	}

	default void initScreen() {
		initScreen(this.getAsJFrame(), WAlign.centerCenterExt);
	}

	@Override
	default void addBorder(int border) {
		JGuiUtils.addBorder(getAsJFrame().getRootPane(), border);
	}

	static void initScreen(JFrame frame) {
		initScreen(frame, null);
	}

	static void initScreen(JFrame frame, WAlign align) {
		Dimension size = frame.getMinimumSize();
		frame.setMinimumSize(new Dimension(Math.max(size.width, 300), Math.max(size.height, 50)));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		if (align != null) {
			Rectangle bounds = getSelectedGraphicDevice(frame).getDefaultConfiguration().getBounds();
			WAlignData alignData = align.apply(new WIntRectangle(0, bounds.width, 0, bounds.height), frame.getWidth(), frame.getHeight());
			frame.setLocation(alignData.x() + bounds.x, alignData.y() + bounds.y);
		}

		//frame.setLocation(-frame.getWidth() / 2, -frame.getHeight() / 2);
		//frame.setLocationRelativeTo(null);
		//setResizable(false);
		//frame.setVisible(true);
	}

	default void atRightPosition(JFrame target) {
		atRightPosition(getAsJFrame(), target);
	}

	static void atRightPosition(JFrame self, JFrame target) {
		Point location = target.getLocation();
		self.setLocation((int)location.getX() + target.getSize().width + 5, (int)location.getY());
	}

	default GraphicsDevice getSelectedGraphicDevice() {
		return getSelectedGraphicDevice(getAsJFrame());
	}

	static GraphicsDevice getSelectedGraphicDevice(JFrame frame) {
		//GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
		int x = frame.getX();
		int y = frame.getY();
		final AABB frameBounds = new AABB(x, y, 0, x + frame.getWidth(), y + frame.getHeight(), 1);

		GraphicsDevice selected = null;
		double distance = -1;

		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if (gs.length == 0) {
			throw new RuntimeException("No Screens Found");
		}
		selected = gs[0];
		if (gs.length == 1) {
			return gs[0];
		}

		for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			Rectangle bounds = device.getDefaultConfiguration().getBounds();
			x = bounds.x;
			y = bounds.y;
			AABB screenBounds = new AABB(x, y, 0, x + bounds.width, y + bounds.height, 1);
			if (screenBounds.intersects(frameBounds)) {
				screenBounds = screenBounds.intersection(frameBounds);
				double d = screenBounds.dimensions().lengthSquared();
				if (d > distance) {
					d = distance;
					selected = device;
				}
			}
		}
		return selected;
	}

	static GraphicsDevice getGraphicDevice(int screen) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if(screen > -1 && screen < gs.length) {
			return gs[screen];
		} else if(gs.length > 0) {
			return gs[0];
		} else {
			throw new RuntimeException("No Screens Found");
		}
	}

	default void showFullOnScreen(int screen) {
		showFullOnScreen(screen, getAsJFrame());
	}

	static void showFullOnScreen(int screen, JFrame frame) {
		getGraphicDevice(screen).setFullScreenWindow(frame);
	}

	default void showOnScreen(int screen) {
		showOnScreen(screen, getAsJFrame());
	}

	static void showOnScreen(int screen, JFrame frame) {
		Rectangle bounds = getGraphicDevice(screen).getDefaultConfiguration().getBounds();
		frame.setLocation(bounds.x, bounds.y);
	}

	default void addCloseEvent(Consumer<WindowEvent> run) {
		addCloseEvent(getAsJFrame(), run);
	}

	static void addCloseEvent(JFrame frame, Consumer<WindowEvent> run) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				run.accept(e);
			}
		});
	}
}
