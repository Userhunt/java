package net.w3e.base.api.window;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.w3e.base.api.StopEvent;

import javax.swing.JFrame;

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
		return new FrameWinListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runnable.run();
			}
		};
	}

	public abstract static class FrameWinListener implements ActionListener {}
}
