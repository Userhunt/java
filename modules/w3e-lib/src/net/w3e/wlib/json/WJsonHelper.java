package net.w3e.wlib.json;

import java.util.Collection;

import net.w3e.wlib.log.LogUtil;

public interface WJsonHelper {
	default <T> T nonNull(T obj, String message) {
		if (obj == null) throw new IllegalStateException(new NullPointerException(message));
		return obj;
	}

	default void lessThan(int value, String message) {
		this.lessThan(value, 0, message);
	}
	default void lessThan(int value, int min, String message) {
		if (value <= min) {
			throw new IllegalStateException(LogUtil.LESS_THAN.createMsg(message, value, min + 1));
		}
	}

	default void lessThan(float value, String message) {
		this.lessThan(value, 0, message);
	}
	default void lessThan(float value, float min, String message) {
		if (value < min) {
			throw new IllegalStateException(LogUtil.LESS_THAN.createMsg(message, value, min + 1E-6f));
		}
	}

	default <A> void isEmpty(Collection<A> array, String message) {
		this.nonNull(array, message);
		if (array.isEmpty()) {
			throw new IllegalStateException(LogUtil.IS_EMPTY.createMsg(message));
		}
	}

	default <A> void isEmpty(A[] array, String message) {
		this.nonNull(array, message);
		if (array.length == 0) {
			throw new IllegalStateException(LogUtil.IS_EMPTY.createMsg(message));
		}
	}

	default void isEmpty(String message) {
		throw new IllegalStateException(LogUtil.IS_EMPTY.createMsg(message));
	}

}
