package net.api.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.api.StopEvent;

public abstract class AbstractFrameWin extends JFrame {

	public static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	protected final AbstractFrameWin parent;
	protected final boolean stop;
	private final WaitTimer timer;

	public AbstractFrameWin(String frameTitle) {
		this(frameTitle, null, false);
	}

	public AbstractFrameWin(String frameTitle, AbstractFrameWin parent, boolean stop) {
		super(frameTitle);
		this.parent = parent;
		this.stop = stop;
		this.timer = new WaitTimer(frameTitle);

		this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				closeEvent();
			}
		});
	}

	@Deprecated
	public AbstractFrameWin(String frameTitle, AbstractFrameWin parent, boolean stop, Object fake) {
		super(frameTitle);
		this.parent = parent;
		this.stop = stop;
		this.timer = new WaitTimer(frameTitle);

		setLayout(null);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				closeEvent();
			}
		});
	}

	public void open(JFrame frameWin) {}

	private final void closeEvent() {
		this.stopTick();
		if (this.stop) {
			StopEvent.close();
		}
		onClose();
		if (this.parent != null) {
			this.parent.open(this);
		}
	}

	protected void onClose() {}

	public final void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	protected final void stopTick() {
		this.timer.stop();
	}

	public final void tick(int delay, Runnable run) {
		this.timer.set(run, delay);
	}

	public static final ActionListener onClick(Runnable runnable) {
		return (ActionEvent e) -> {
			runnable.run();
		};
	}

	public static final <T extends Component> ActionListener onClick(T component, Consumer<T> runnable) {
		return (e) -> {
			runnable.accept(component);
		};
	}

	public static final void setSize(Component component, int width, int height) {
		component.setPreferredSize(new Dimension(width, height));
		component.setMinimumSize(new Dimension(width, height));
		component.setMaximumSize(new Dimension(width, height));
	}

	protected final void atRightPosition(JFrame frame) {
		Point location = frame.getLocation();
		this.setLocation((int)location.getX() + frame.getSize().width + 5, (int)location.getY());
	}

	public static final HorisontalPanelBuilder horisontalPanelBuilder() {
		return new HorisontalPanelBuilder();
	}

	public static final VerticalPanelBuilder verticalPanelBuilder() {
		return new VerticalPanelBuilder();
	}

	public static abstract class PanelBuilder<T extends PanelBuilder<T>> {
		protected int space = 10;

		protected final List<Component> list = new ArrayList<>();

		@SuppressWarnings("unchecked")
		public final T add(Component... components) {
			for (Component component : components) {
				this.list.add(component);
			}
			return (T)this;
		}

		@SuppressWarnings("unchecked")
		public final T setSpace(int space) {
			this.space = space;
			return (T)this;
		}

		public abstract JPanel build();
	}

	public static class HorisontalPanelBuilder extends PanelBuilder<HorisontalPanelBuilder> {
		private int height = 26;
		private int width = 10;
		private int minWidth = 0;
		private int maxWidth = Short.MAX_VALUE;

		public final HorisontalPanelBuilder setWidth(int width) {
			this.width = width;
			return this;
		}

		public final HorisontalPanelBuilder setMinWidth(int width) {
			this.minWidth = width;
			return this;
		}

		public final HorisontalPanelBuilder setMaxWidth(int width) {
			this.maxWidth = width;
			return this;
		}

		public final HorisontalPanelBuilder setHeight(int height) {
			this.height = height;
			return this;
		}

		@Override
		public final JPanel build() {
			JPanel panel = new JPanel();
			int width = Math.min(this.maxWidth, this.width);
			width = Math.max(this.minWidth, width);
			panel.setPreferredSize(new Dimension(width, this.height));
			panel.setMinimumSize(new Dimension(Math.max(this.minWidth, (int)panel.getMinimumSize().getWidth()), this.height));
			panel.setMaximumSize(new Dimension(Math.min(this.maxWidth, (int)panel.getMaximumSize().getWidth()), this.height));
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

			Iterator<Component> iterator = this.list.iterator();
			while(iterator.hasNext()) {
				Component component = iterator.next();
				panel.add(component);
				if (iterator.hasNext() && space != -1) {
					panel.add(Box.createHorizontalStrut(this.space));
				}
			}

			return panel;
		}
	}

	public static class VerticalPanelBuilder extends PanelBuilder<VerticalPanelBuilder> {

		private int width = 100;
		private int height = 26;
		private int minHeight = 0;
		private int maxHeight = Short.MAX_VALUE;

		public final VerticalPanelBuilder setHeight(int height) {
			this.height = height;
			return this;
		}

		public final VerticalPanelBuilder setMinWidth(int height) {
			this.minHeight = height;
			return this;
		}

		public final VerticalPanelBuilder setMaxWidth(int height) {
			this.maxHeight = height;
			return this;
		}

		public final VerticalPanelBuilder setWidth(int width) {
			this.width = width;
			return this;
		}

		@Override
		public final JPanel build() {
			JPanel panel = new JPanel();
			int height = Math.min(this.maxHeight, this.height);
			height = Math.max(this.minHeight, height);
			panel.setPreferredSize(new Dimension(this.width, height));
			panel.setMinimumSize(new Dimension(this.width, Math.max(this.minHeight, (int)panel.getMinimumSize().getHeight())));
			panel.setMaximumSize(new Dimension(this.width, Math.min(this.maxHeight, (int)panel.getMaximumSize().getHeight())));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			Iterator<Component> iterator = this.list.iterator();
			while(iterator.hasNext()) {
				Component component = iterator.next();
				panel.add(component);
				if (iterator.hasNext() && space != -1) {
					panel.add(Box.createVerticalStrut(this.space));
				}
			}

			return panel;
		}
	}
}
