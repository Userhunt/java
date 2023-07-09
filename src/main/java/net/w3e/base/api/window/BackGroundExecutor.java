package net.w3e.base.api.window;

import java.util.function.Consumer;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jetbrains.annotations.NotNull;

import net.w3e.base.api.CustomOutputStream;
import net.w3e.base.api.window.jcomponent.JConsole;
import net.w3e.base.tuple.number.WIntTuple;

public class BackGroundExecutor extends FrameWin {

	private final JTextArea textArea = new JConsole();
	private final CustomOutputStream printStream = new CustomOutputStream(textArea, true);
	private final JProgressBar bar = new JProgressBar();
	private final boolean interrupt;
	private final Execute run;
	private final WaitTimer timer;

	public BackGroundExecutor(String frameTitle, @NotNull FrameWin parent, boolean interrupt, @NotNull Execute run) {
		super(frameTitle, parent, false);
		parent.setVisible(false);
		this.setLocation(parent.getLocation());
		this.setSize(600, 600);
		this.setVisible(true);
		this.setResizable(false);
		this.interrupt = interrupt;
		this.run = run;
		this.timer = new WaitTimer(frameTitle);

		int y = 5;

		//progress
		this.bar.setBounds(5, y, 575, 26);
		this.bar.setStringPainted(true);
		this.add(bar);

		//console
		y += 30;
		JScrollPane scroll = new JScrollPane(this.textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(5, y, 575, 580 - y);
		this.add(scroll);
	}

	public final void run() {
		this.run((i) -> {});
	}

	public final void run( Consumer<Integer> result) {
		if (!this.timer.isStop()) {
			return;
		}
		WIntTuple progress = new WIntTuple();
		timer.set(() -> {
			int old = progress.get();
			BackGroundExecutor.this.printStream.enable();
			int i = -1;
			try {
				i = BackGroundExecutor.this.run.run(old);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if (i < -1) {
				i = -1;
			}
			if (i > 100) {
				i = 100;
			}
			BackGroundExecutor.this.bar.setValue(i);
			BackGroundExecutor.this.bar.update(BackGroundExecutor.this.bar.getGraphics());

			if (result != null) {
				result.accept(i);
			}

			BackGroundExecutor.this.printStream.disable();

			progress.set(i);
			if (i == -1 || i == 100) {
				this.timer.stop(this.interrupt);
			}
		}, 10, this.interrupt);
	}

	@Override
	protected final void onCloseIns() {
		this.timer.stop();
		BackGroundExecutor.this.printStream.disable();
		stopTick();
	}

	public static void run(String FrameTitle, @NotNull FrameWin parent, boolean interrupt, Execute run) {
		run(FrameTitle, parent, interrupt, run, (i) -> {});
	}

	public static void run(String FrameTitle, @NotNull FrameWin parent, boolean interrupt, Execute run, Consumer<Integer> result) {
		BackGroundExecutor executor = new BackGroundExecutor(FrameTitle, parent, interrupt, run);

		executor.run(result);
	}

	@FunctionalInterface
	public static interface Execute {
		int run(int old);
	}

}
