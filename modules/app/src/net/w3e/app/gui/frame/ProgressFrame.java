package net.w3e.app.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.utils.Holders.FloatHolder;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.gui.utils.JGuiUtils;

public final class ProgressFrame extends ConsoleFrame {

	private static final Color FOREGROUND_COLOR = new Color(0, 120, 215);
	private static final int BAR_MAX = 1000;

	private final JProgressBar bar = new JProgressBar();

	private final LinkedBlockingQueue<ProgressTask> tasks = new LinkedBlockingQueue<>();

	private final ProgressThread thread = new ProgressThread();

	private final JCheckBox pauseBox;

	@Getter
	private boolean stop = false;
	@Setter
	@Getter
	private volatile boolean paused = false;

	public ProgressFrame(String title) {
		super(title);

		addBorder(10);
		setMinimumSize(new Dimension(300, 150));

		JPanel panel = new JPanel();
		JGuiUtils.addBorder(panel, 0, 0, 10, 0);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		//panel.setLayout(new HorizontalLayout());

		this.bar.setStringPainted(true);
		this.bar.setMaximum(BAR_MAX);
		panel.add(this.bar);

		panel.add(Box.createHorizontalStrut(5));

		JButton clear = new JButton("Clear Task");
		JGuiUtils.setSize(clear, 85, 26);
		clear.addActionListener(_ -> clearTasks());
		panel.add(clear);

		panel.add(Box.createHorizontalStrut(5));

		pauseBox = new JCheckBox("Pause");
		JGuiUtils.setSize(pauseBox, 60, 26);
		pauseBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean paused = ((JCheckBox)e.getSource()).isSelected();
				if (paused) {
					pause();
				} else {
					resume();
				}
			}
		});
		panel.add(pauseBox);

		panel.add(Box.createHorizontalStrut(5));

		JButton stop = new JButton("Strop");
		JGuiUtils.setSize(stop, 60, 26);
		stop.addActionListener(_ -> stop());
		panel.add(stop);

		this.add(panel, BorderLayout.NORTH);

		this.addCloseEvent(_ -> {
			stop();
			resumePaused();
		});
	}

	public void testTask() {
		FloatHolder progress = new FloatHolder(0);

		addTask((_) -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {}
			progress.increment(0.01f);
			System.out.println(progress);
			return progress.getValue();
		});
	}

	public float getProgress() {
		return this.bar.getValue() / BAR_MAX;
	}

	public void addTask(ProgressTask run) {
		this.tasks.add(run);
	}

	public void clearTasks() {
		this.tasks.clear();
	}

	public void stop() {
		this.stop = true;
		this.thread.interrupt();
		SKDSLogger.detachPrintStream(this.printStream);
		resumePaused();
	}

	public void stopAndWait() {
		if (this.stop) {
			resumePaused();
			return;
		}
		stop();
		synchronized (this.thread) {
			try {
				this.thread.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void pause() {
		pause(false);
	}

	public void pause(boolean error) {
		if (!this.paused) {
			this.paused = true;
			this.bar.setForeground(error ? Color.RED : Color.GRAY);
			this.bar.update(bar.getGraphics());
		}
		this.pauseBox.setSelected(this.paused);
	}

	private void resumePaused() {
		synchronized (this) {
			this.notifyAll();
		}
	}

	public void resume() {
		if (this.paused) {
			this.paused = false;
			resumePaused();
			synchronized (thread) {
				thread.notify();
			}
			this.bar.setForeground(FOREGROUND_COLOR);
			this.bar.update(bar.getGraphics());
		}
		this.pauseBox.setSelected(this.paused);
	}

	public void close() {
		this.stop();
		this.dispose();
	}

	public interface ProgressTask {
		float tick(ProgressFrame frame) throws InterruptedException;
	}

	private class ProgressThread extends Thread {

		public ProgressThread() {
			super("ProgressFrame");
			this.start();
		}

		@Override
		public void run() {
			while (!ProgressFrame.this.stop) {
				ProgressTask task;
				int oldProgress = -1;
				try {
					task = tasks.take();
				} catch (InterruptedException e) {
					break;
				}
				while (!ProgressFrame.this.stop) {
					while (ProgressFrame.this.paused) {
						synchronized (this) {
							try {
								this.wait();
							} catch (InterruptedException e) {
								break;
							}
						}
					}
					enableConsole();
					float p;

					try {
						p = task.tick(ProgressFrame.this);
						if (p < 0) {
							pause(true);
							break;
						}
						disableConsole();
					} catch (Exception e) {
						pause(true);
						e.printStackTrace();
						disableConsole();
						break;
					}

					int progress = (int)(p * BAR_MAX);
					if (oldProgress != progress) {
						if (p < 1f) {
							progress = Math.min(progress, (int)((.99f) * BAR_MAX));
						}
						bar.setValue(progress);
						bar.setString(String.format("%.2f", p * 100) + " %");
						bar.update(bar.getGraphics());
					}

					if (progress >= BAR_MAX) {
						break;
					}
					continue;
				}
			}
			synchronized (thread) {
				thread.notify();
			}
		}
	}
}
