package net.api.window;

public interface IBackgroundExecutor {
	void stop();
	Runnable stopAndClose();
	boolean isStop();
	void clear();
}
