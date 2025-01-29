package net.w3e.wlib.json;

import java.util.Collection;

import com.google.gson.JsonSyntaxException;

import net.w3e.wlib.log.LogUtil;

public interface WJsonHelper {
	default void nonNull(String msg, Object obj) {
		if (obj == null) throw new JsonSyntaxException(new NullPointerException(msg));
	}

	default void lessThan(String msg, int value) {
		this.lessThan(msg, value, 0);
	}
	default void lessThan(String msg, int value, int min) {
		if (value <= min) {
			throw new JsonSyntaxException(LogUtil.LESS_THAN.createMsg(msg, value, min + 1));
		}
	}

	default void lessThan(String msg, float value) {
		this.lessThan(msg, value, 0);
	}
	default void lessThan(String msg, float value, float min) {
		if (value < min) {
			throw new JsonSyntaxException(LogUtil.LESS_THAN.createMsg(msg, value, min + 0.01f));
		}
	}

	default <A> void isEmpty(String msg, Collection<A> array) {
		this.nonNull(msg, array);
		if (array.isEmpty()) {
			throw new JsonSyntaxException(LogUtil.IS_EMPTY.createMsg(msg));
		}
	}

	default <A> void isEmpty(String msg, @SuppressWarnings("unchecked") A... array) {
		this.nonNull(msg, array);
		if (array.length == 0) {
			throw new JsonSyntaxException(LogUtil.IS_EMPTY.createMsg(msg));
		}
	}
}
