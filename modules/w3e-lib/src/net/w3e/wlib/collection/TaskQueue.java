package net.w3e.wlib.collection;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.skds.lib2.utils.FiniteTickable;
import net.skds.lib2.utils.Holders.IntHolder;

@CustomLog
@RequiredArgsConstructor
public class TaskQueue {

	private final int preferedTime;
	private int timer = 0;
	private int isRun = 0;

	private final ConcurrentLinkedQueue<TaskEntry> entries = new ConcurrentLinkedQueue<>();

	public TaskQueue() {
		this(10);
	}

	public final void add(FiniteTickable e) {
		this.add(this.preferedTime, e);
	}

	public final void add(int preferedTime, FiniteTickable e) {
		if (e != null) {
			this.entries.add(new TaskEntry(preferedTime, e));
		}
	}

	public final void run() {
		this.timer++;
		this.isRun++;
		if (!this.entries.isEmpty()) {
			Iterator<TaskEntry> iterator = this.entries.iterator();
			while(iterator.hasNext()) {
				TaskEntry entry = iterator.next();
				if (entry.minTimerForRun < this.timer) {
					if (!entry.run(true)) {
						this.entries.remove(entry);
					}
				} else {
					break;
				}
			}
		}
		this.isRun = 0;
	}

	public final void stop() {
		this.entries.clear();
	}

	public final int size() {
		return this.entries.size();
	}

	public final void forEach(Predicate<FiniteTickable> function) {
		forEach(function, true);
	}

	public final void forEach(Predicate<FiniteTickable> function, boolean instant) {
		QueueIteratorTask forTask = () -> {
			this.isRun++;
			if (!this.entries.isEmpty()) {
				Iterator<TaskEntry> iterator = this.entries.iterator();
				while(iterator.hasNext()) {
					TaskEntry entry = iterator.next();
					if (entry.minTimerForRun < this.timer) {
						if (entry.test(false)) {
							if (function.test(entry.task)) {
								iterator.remove();
							}
						}
					} else {
						break;
					}
				}
			}
			this.isRun--;
			return false;
		};
		if (instant) {
			forTask.tick();
		} else {
			this.add(forTask);
		}
	}


	private class TaskEntry {
		public int timer;
		public int minTimerForRun;

		private final int preferedTime;
		private final FiniteTickable task;

		public TaskEntry(int preferedTime, FiniteTickable task) {
			this.preferedTime = preferedTime;
			this.task = task;
			if (TaskQueue.this.isRun != 0) {
				this.minTimerForRun = TaskQueue.this.timer;
			}
		}

		public final boolean test(boolean all) {
			return all || !(this.task instanceof QueueIteratorTask);
		}

		public boolean run(boolean all) {
			if (test(all)) {
				if (this.preferedTime != -1 && this.timer != -1) {
					if (this.timer >= this.preferedTime) {
						log.warn("take too long " + this.task);
						this.timer = -1;
					} else if (all) {
						this.timer++;
					}
				}
				return this.task.tick();
			} else {
				return true;
			}
		}
	}

	@FunctionalInterface
	private static interface QueueIteratorTask extends FiniteTickable {}

	public static void main(String[] args) {
		TaskQueue queue = new TaskQueue();
		FiniteTickable task1 = () -> {
			System.out.println("1");
			return false;
		};

		log.info("normal");
		queue.add(task1);
		queue.run();
		queue.run();

		log.info("add while run");
		queue.add(() -> {
			System.out.println("2.1");
			queue.add(task1);
			System.out.println("2.2");
			return false;
		});
		queue.run();
		System.out.println("2.3");
		queue.run();
		queue.run();

		log.info("prefered time");
		IntHolder tuple = new IntHolder(11);
		queue.add(() -> {
			System.out.println("3." + (12-tuple.getValue()));
			tuple.decrement();
			return tuple.getValue() > 0;
		});
		for (int i = 0; i < 11; i++) {
			System.out.println("i " + (i + 1));
			queue.run();
		}

		log.info("for");
		queue.add(task1);
		System.out.println("for false");
		queue.forEach(e -> {
			System.out.println(e);
			return false;
		});
		System.out.println("for after");
		queue.run();
		queue.add(task1);
		System.out.println("for true");
		queue.forEach(e -> {
			System.out.println(e);
			return false;
		});
		System.out.println("for after");
		queue.run();
	}
}
