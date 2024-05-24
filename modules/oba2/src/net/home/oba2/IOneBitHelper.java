package net.home.oba2;

public interface IOneBitHelper {
	void info(Object str);
	void debug(Object str);
	void error(Object str);
	void error(Exception e);
	void waitKey(OneBitState state);
}
