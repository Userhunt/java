package net.api.window;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jetbrains.annotations.NotNull;

import net.api.CustomOutputStream;
import net.api.window.jcomponent.JConsole;
import net.w3e.base.BooleanEnum;
import net.w3e.base.holders.number.IntHolder;

public class BackgroundExecutor extends FrameWin implements IBackgroundExecutor {

	private final JTextArea textArea = new JConsole();
	private final CustomOutputStream printStream = new CustomOutputStream(textArea, true);
	private final JProgressBar bar = new JProgressBar();
	private final boolean updateParentPosition;
	private final Execute run;
	private final Done done;
	private final WaitTimer timer;
	private boolean stop;
	private volatile BooleanEnum close = BooleanEnum.NULL;

	public BackgroundExecutor(String frameTitle, FrameWin parent, @NotNull Execute run, @Nullable Done done, boolean hideParent, boolean updateParentPosition) {
		super(frameTitle, parent, false);
		this.parent.setVisible(!hideParent);
		this.updateParentPosition = updateParentPosition;
		this.setLocation(parent.getLocation());
		this.setSize(1200, 800);
		this.setVisible(true);
		this.setResizable(false);
		this.run = run;
		this.done = done;
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

	@Override
	protected final boolean showParentWhenClose() {
		return this.updateParentPosition;
	}

	public final void run() {
		if (!this.timer.isStop()) {
			return;
		}
		this.stop = false;
		IntHolder progress = new IntHolder();
		timer.set(() -> {
			int old = progress.getAsInt();
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
			boolean update = false;
			if (this.isStop() && BackgroundExecutor.this.bar.getForeground() != Color.red) {
				BackgroundExecutor.this.bar.setForeground(Color.red);
				update = true;
			}
			if (i != old) {
				BackgroundExecutor.this.bar.setValue(i);
				update = true;
			}
			if (update) {
				BackgroundExecutor.this.bar.update(BackgroundExecutor.this.bar.getGraphics());
			}

			BackgroundExecutor.this.printStream.disable();

			progress.setValue(i);
			if (i == -1 || i == 100 || this.stop) {
				this.timer.stop(false, true);
				if (this.done != null) {
					this.done.done(this);
				}
				this.close = BooleanEnum.FALSE;
				if (this.close == BooleanEnum.TRUE) {
					this.onClose();
				}
			}
		}, 10, false);
	}

	public final void print(Runnable runnable) {
		this.printStream.print(runnable);
	}

	@Override
	protected final void onCloseIns() {
		this.timer.stop();
		BackgroundExecutor.this.printStream.disable();
		stopTick();
		if (this.close == BooleanEnum.TRUE) {
			this.close = BooleanEnum.FALSE;
			this.dispose();
		}
	}

	public static class BackgroundExecutorBuilder {
		private final String frameTitle;
		@NotNull
		private final FrameWin parent;
		private Execute execute;
		private Done done;

		private boolean updateParentPosition = true;
		private boolean parentVisible = false;

		public BackgroundExecutorBuilder(String frameTitle, @NotNull FrameWin parent) {
			this.frameTitle = frameTitle;
			this.parent = parent;
		}

		public final BackgroundExecutorBuilder setExecute(Execute execute) {
			this.execute = execute;
			return this;
		}

		public final BackgroundExecutorBuilder setDone(Done done) {
			this.done = done;
			return this;
		}

		public final BackgroundExecutorBuilder setParentVisible(boolean parentVisible) {
			this.parentVisible = parentVisible;
			return this;
		}

		public final BackgroundExecutorBuilder setUpdateParentPosition(boolean updateParentPosition) {
			this.updateParentPosition = updateParentPosition;
			return this;
		}

		public final void run() {
			this.build().run();
		}

		public final void runThread(Consumer<BackgroundExecutor> execute, IntSupplier progress) {
			ExecutorService thread = Executors.newFixedThreadPool(1);
			BackgroundExecutor exe = new BackgroundExecutor(this.frameTitle, this.parent, (oldProgress, executor) -> {
				int i = progress.getAsInt();
				if (i > 100 || executor.isStop()) {
					thread.shutdownNow();
				}
				return i;
			}, this.done, !this.parentVisible, this.updateParentPosition);
			exe.run();
			thread.execute(() -> execute.accept(exe));
		}

		public final BackgroundExecutor build() {
			return new BackgroundExecutor(this.frameTitle, this.parent, this.execute, this.done, !this.parentVisible, this.updateParentPosition);
		}
	}

	@FunctionalInterface
	public static interface Execute {
		int run(int oldProgress, IBackgroundExecutor executor);
	}

	public static interface Done {
		void done(IBackgroundExecutor executor);
	}

	@Override
	public final void stop() {
		this.stop = true;
	}

	@Override
	public final Runnable stopAndClose() {
		if (close == BooleanEnum.FALSE) {
			return () -> {
				this.dispose();
			};
		}
		this.stop();
		this.close = BooleanEnum.TRUE;
		return () -> {
			ExecutorService es = Executors.newCachedThreadPool();
			es.execute(() -> {
				while(this.close == BooleanEnum.TRUE) {
					Inputs.sleep(100);
				}
			});
			es.shutdown();
			try {
				es.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.dispose();
		};
	}

	@Override
	public final boolean isStop() {
		return this.stop || this.close != BooleanEnum.NULL;
	}

	@Override
	public final void clear() {
		this.textArea.setText("");
	}
}
