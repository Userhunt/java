package net.api.window;

public interface IBackgroundExecutor {
	void stop();
	Runnable stopAndClose(boolean close);
	boolean isStop();
	void clear();
	void print(Runnable runnable);
	void print(Object object);
}
