package net.api.window;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JProgressBar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.api.CustomOutputStream;
import net.api.window.jcomponent.JConsole;
import net.w3e.base.BooleanEnum;
import net.w3e.base.holders.ObjectHolder;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.math.BMatUtil;

public class BackgroundExecutor extends FrameWin implements IBackgroundExecutor {

	private final JConsole textArea = new JConsole(1100, 700);
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
		this.setVisible(true);
		this.run = run;
		this.done = done;
		this.timer = new WaitTimer(frameTitle);

		this.bar.setStringPainted(true);

		JButton button = new JButton("Stop");
		FrameWin.setSize(button, 60, 26);
		button.addActionListener(FrameWin.onClick(() -> this.stop = true));

		this.add(FrameWin.horisontalPanelBuilder().add(this.bar, button).setWidth(1100).setMinWidth(1100).build());

		this.add(Box.createVerticalStrut(10));

		this.add(this.textArea.scroll);

		this.pack();
	}

	@Override
	protected final boolean showParentWhenClose() {
		return this.updateParentPosition;
	}

	public final IntHolder run() {
		if (!this.timer.isStop()) {
			return null;
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
		return progress;
	}

	@Override
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

		public final IntHolder run() {
			return this.build().run();
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

		public final BackgroundExecutor multiple(int tickRate, BackgroundExecutor... executors) {
			if (executors.length == 0) {
				return null;
			}
			int size = executors.length;
			List<BackgroundExecutor> list = new ArrayList<>(Stream.of(executors).toList());
			for (BackgroundExecutor executor : list) {
				executor.setVisible(false);
			}

			ObjectHolder<IntHolder> holder = new ObjectHolder<>();

			BackgroundExecutor exe = new BackgroundExecutor(this.frameTitle, this.parent, (oldProgress, executor) -> {
				IntHolder progress = holder.get();
				if ((progress == null || progress.get() >= 100) && !list.isEmpty()) {
					BackgroundExecutor frame = list.remove(0);
					frame.setVisible(true);
					progress = frame.run();
					holder.set(progress);
				}
				int p1 = BMatUtil.round((size - list.size()) * 100d / size);
				int p2 = progress.getAsInt();
				int p3 = BMatUtil.round(((double)p2) / size);
				executor.print(() -> {
					System.out.println(String.format("Stage: %s. Progress of stage: %s. Progress: %s", p1, p2, p3));
				});
				Inputs.sleep(tickRate);
				return p1 + p3;
			}, this.done, !this.parentVisible, this.updateParentPosition);
			
			return exe;
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
