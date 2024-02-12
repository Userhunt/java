package net.api.window;

import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jetbrains.annotations.NotNull;

import net.api.CustomOutputStream;
import net.api.window.jcomponent.JConsole;
import net.w3e.base.tuple.number.WIntTuple;

public class BackgroundExecutor extends FrameWin implements IBackgroundExecutor {

	private final JTextArea textArea = new JConsole();
	private final CustomOutputStream printStream = new CustomOutputStream(textArea, true);
	private final JProgressBar bar = new JProgressBar();
	private final Execute run;
	private final WaitTimer timer;
	private boolean stop;

	public BackgroundExecutor(String frameTitle, @NotNull FrameWin parent, @NotNull Execute run) {
		super(frameTitle, parent, false);
		parent.setVisible(false);
		this.setLocation(parent.getLocation());
		this.setSize(1200, 800);
		this.setVisible(true);
		this.setResizable(false);
		this.run = run;
		this.timer = new WaitTimer(frameTitle);

		int y = 5;

		//progress
		this.bar.setBounds(5, y, 1110, 26);
		this.bar.setStringPainted(true);
		this.add(bar);

		JButton button = new JButton("Stop");
		button.setBounds(1120, y, 60, 26);
		button.addActionListener(FrameWin.onClick(() -> this.stop = true));
		this.add(button);

		//console
		y += 30;
		JScrollPane scroll = new JScrollPane(this.textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(5, y, 1175, 780 - y);
		this.add(scroll);
	}

	public final void run() {
		this.run((i) -> {});
	}

	public final void run(Consumer<Integer> result) {
		if (!this.timer.isStop()) {
			return;
		}
		this.stop = false;
		WIntTuple progress = new WIntTuple();
		timer.set(() -> {
			int old = progress.get();
			BackgroundExecutor.this.printStream.enable();
			int i = -1;
			try {
				i = BackgroundExecutor.this.run.run(old, this);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if (i < -1) {
				i = -1;
			}
			if (i > 100) {
				i = 100;
			}
			BackgroundExecutor.this.bar.setValue(i);
			if (this.stop || i == -1) {
				BackgroundExecutor.this.bar.setForeground(java.awt.Color.red);
			}
			BackgroundExecutor.this.bar.update(BackgroundExecutor.this.bar.getGraphics());

			if (result != null) {
				result.accept(i);
			}

			BackgroundExecutor.this.printStream.disable();

			progress.set(i);
			if (i == -1 || i == 100 || this.stop) {
				this.timer.stop(false);
			}
		}, 10, false);
	}

	@Override
	protected final void onCloseIns() {
		this.timer.stop();
		BackgroundExecutor.this.printStream.disable();
		stopTick();
	}

	public static void run(String FrameTitle, @NotNull FrameWin parent, Execute run) {
		run(FrameTitle, parent, run, (i) -> {});
	}

	public static void run(String FrameTitle, @NotNull FrameWin parent, Execute run, Consumer<Integer> result) {
		BackgroundExecutor executor = new BackgroundExecutor(FrameTitle, parent, run);

		executor.run(result);
	}

	@FunctionalInterface
	public static interface Execute {
		int run(int oldProgress, IBackgroundExecutor executor);
	}

	@Override
	public final void stop() {
		this.stop = true;
	}

	@Override
	public final boolean isStop() {
		return this.stop;
	}

	@Override
	public final void clear() {
		this.textArea.setText("");
	}
}
