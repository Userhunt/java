package net.w3e.base.message;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

/**
 * 12.04.23
 */
public class BMessageLogger {

	public final Logger LOGGER;
	public final StackTraceElement stackTraceElement;

	public BMessageLogger(Logger logger) {
		this.LOGGER = logger;
		this.stackTraceElement = null;
	}

	public BMessageLogger(Logger logger, StackTraceElement stackTraceElement) {
		this.LOGGER = logger;
		this.stackTraceElement = stackTraceElement;
	}

	public BMessageLogger create() {
		return new BMessageLogger(LOGGER, StackLocatorUtil.getStackTraceElement(3));
	}

	public final String parseTrace() {
		return "[" + stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + "]: ";
	}

	public final void info(String msg) {
		LOGGER.info(parse(msg));
	}

	public final void warn(String msg) {
		LOGGER.warn(parse(msg));
	}

	public final void error(String msg) {
		LOGGER.error(parse(msg));
	}

	public final String parse(String msg) {
		return String.format("%s%s", parseTrace(), msg);
	}

	public final void debug(String msg) {
		LOGGER.debug(msg);
	}
}
