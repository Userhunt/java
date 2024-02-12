package net.api.window;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import net.w3e.base.tuple.number.WLongTuple;

public class WaitTimer extends Timer {

	private final ExecutorService service = Executors.newFixedThreadPool(1);
	private final WLongTuple time = new WLongTuple();
	private WaitTimerTask task;

	public WaitTimer(String name) {
		super(name);
	}

	public final void set(Runnable run, int delay) {
		set(run, delay, true);
	}

	public final void set(Runnable run, int delay, boolean interrupt) {
		if (delay < 0) {
			throw new IllegalArgumentException("Negative delay.");
		}
		stop(interrupt);
		this.time.set(0);
		this.task = new WaitTimerTask(run, delay);
		if (run instanceof TimerExt ext) {
			ext.onSleep = (sleep) -> {
				this.time.set(System.currentTimeMillis() + Math.max(delay, sleep));
			};
		}
		super.schedule(task, delay, 5);
	}

	public abstract static class TimerExt implements Runnable {

		private Consumer<Integer> onSleep;
		private int sleep;

		@Override
		public final void run() {
			this.sleep = 0;
			runExt();
			if (this.onSleep != null) {
				this.onSleep.accept(this.sleep);
			}
		}

		protected abstract void runExt();

		public final void sleep(int i) {
			Inputs.sleep(i);
			this.sleep += i;
		}
	}

	public final boolean isStop() {
		return this.task == null || this.task.isStop();
	}



	public final void stop() {
		this.stop(true);
	}

	public final void stop(boolean interrupt) {
		if (this.task != null) {
			this.task.stop(interrupt);
			this.task = null;
		}
	}

	private class WaitTimerTask extends TimerTask {

		private final Runnable run;
		private Future<?> last;
		private int delay;
		private volatile boolean stop;

		public WaitTimerTask(Runnable run, int delay) {
			this.run = run;
			this.delay = delay;
		}

		public void stop(boolean interrupt) {
			this.stop = true;
			if (this.last != null) {
				try {
					this.last.cancel(interrupt);
					this.last = null;
				} catch (Exception e) {}
			}
			this.cancel();
		}

		public boolean isStop() {
			try {
				return 
					this.stop || 
					this.last == null ||
					this.last.isDone() && this.last.get() == null;
			} catch (CancellationException e) {
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}

		@Override
		public void run() {
			if (this.stop) {
				return;
			}
			long time = System.currentTimeMillis();
			WaitTimer timer = WaitTimer.this;
			if (time >= timer.time.get() && isStop()) {
				this.last = service.submit(run);
				timer.time.set(time + delay - 5);
			}
		}
	}

	@Deprecated
	@Override
	public void schedule(TimerTask task, Date firstTime, long period) {
		throw new UnsupportedOperationException("Unsupported method 'schedule'");
	}

	@Deprecated
	@Override
	public void schedule(TimerTask task, Date time) {
		throw new UnsupportedOperationException("Unsupported method 'schedule'");
	}

	@Deprecated
	@Override
	public void schedule(TimerTask task, long delay, long period) {
		throw new UnsupportedOperationException("Unsupported method 'schedule'");
	}

	@Deprecated
	@Override
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
		throw new UnsupportedOperationException("Unsupported method 'schedule'");
	}

	@Deprecated
	@Override
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		throw new UnsupportedOperationException("Unsupported method 'schedule'");
	}

	@Deprecated
	@Override
	public void schedule(TimerTask task, long delay) {
		throw new UnsupportedOperationException("Unsupported method 'schedule'");
	}

}
