package net.w3e.base.json.system;

import org.apache.logging.log4j.Logger;

import net.w3e.base.json.BJsonUtil;
import net.w3e.base.message.MessageError;

public interface BaseJsonHelper {
	Logger logger();

	default void throwMessage() {
		BJsonUtil.throwMessage(logger(), "", 3);
	}
	default void throwMessage(String last) {
		BJsonUtil.throwMessage(logger(), last, 3);
	};
	static void throwMessage(Logger logger, String last) {
		BJsonUtil.throwMessage(logger, last, 3);
	}

	default void throwMessage(Object last) {
		BJsonUtil.throwMessage(logger(), String.valueOf(last), 3);
	}

	default void throwMessage(Exception last) {
		BJsonUtil.throwMessage(logger(), last);
	}
	static void throwMessage(Logger logger, Exception last) {
		BJsonUtil.throwMessage(logger, last);
	}

	default void throwMessage(MessageError error, Object[] arg, String last) {
		throwMessage(logger(), error.createMsg(arg) + " " + last);
	}
}
