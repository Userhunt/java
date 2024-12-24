package net.w3e.wlib.collection;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModifiedQueue<T extends ModifiedQueue.QueueTask> {

	public final Logger LOGGER = LogManager.getLogger("ModifiedQueue@" + this.hashCode());

	private final int preferedTime;
	private int tick = 0;
	private int run = 0;

	private final ConcurrentLinkedQueue<QueueEntry> entries = new ConcurrentLinkedQueue<>();

	public ModifiedQueue() {
		this(10);
	}

	public ModifiedQueue(int preferedTime) {
		this.preferedTime = preferedTime;
	}

	public final void add(T e) {
		this.add(this.preferedTime, e);
	}

	public final void add(int preferedTime, T e) {
		if (e != null) {
			QueueEntry entry = new QueueEntry(preferedTime, e);
			if (this.run != 0) {
				entry.tick = this.tick;
			}
			this.entries.add(entry);
		}
	}

	public final void run() {
		this.tick++;
		this.run++;
		if (!this.entries.isEmpty()) {
			Iterator<ModifiedQueue<T>.QueueEntry> iterator = this.entries.iterator();
			while(iterator.hasNext()) {
				ModifiedQueue<T>.QueueEntry entry = iterator.next();
				if (entry.tick < this.tick) {
					if (entry.run(true)) {
						this.entries.remove(entry);
					}
				} else {
					break;
				}
			}
		}
		this.run--;
	}

	public final void stop() {
		this.entries.clear();
	}

	public final int size() {
		return this.entries.size();
	}

	public final void forEach(Predicate<T> function) {
		forEach(function, false);
	}

	@SuppressWarnings("unchecked")
	public final void forEach(Predicate<T> function, boolean instant) {
		QueueForTask forTask = () -> {
			ModifiedQueue.this.run++;
			if (!ModifiedQueue.this.entries.isEmpty()) {
				Iterator<QueueEntry> iterator = ModifiedQueue.this.entries.iterator();
				while(iterator.hasNext()) {
					QueueEntry entry = iterator.next();
					if (entry.tick < ModifiedQueue.this.tick) {
						if (entry.test(false)) {
							if (function.test((T)entry.task)) {
								iterator.remove();
							}
						}
					} else {
						break;
					}
				}
			}
			ModifiedQueue.this.run--;
			return true;
		};
		if (instant) {
			forTask.isDone();
		} else {
			this.entries.add(new QueueEntry(this.preferedTime, forTask));
		}
	}

	private class QueueEntry {

		private int time;
		private int tick;
		private final int preferedTime;
		private final QueueTask task;

		public QueueEntry(int preferedTime, QueueTask task) {
			this.preferedTime = preferedTime;
			this.task = task;
		}

		public final boolean test(boolean all) {
			return all || !(this.task instanceof QueueForTask);
		}

		public boolean run(boolean all) {
			if (test(all)) {
				if (this.preferedTime != -1 && this.time != -1) {
					if (this.time >= this.preferedTime) {
						LOGGER.warn("take too long " + this.task);
						this.time = -1;
					} else if (all) {
						this.time++;
					}
				}
				return this.task.isDone();
			} else {
				return false;
			}
		}
	}

	@FunctionalInterface
	public static interface QueueTask {
		boolean isDone();
	}

	@FunctionalInterface
	private static interface QueueForTask extends QueueTask {}
}
