package net.w3e.base.message;

import org.apache.logging.log4j.Logger;

import net.w3e.base.BStringUtil;

public class BMessageLoggerHelper {

	protected final BMessageLogger logger;

	public BMessageLoggerHelper(Logger logger) {
		this.logger = new BMessageLogger(logger);
	}

	public final void info() {
		System.out.println();
	}

	public final void info(Object object, Object... args) {
		MessageUtil.info(logger.create(), BStringUtil.toString(object), args);
	}

	public final void warn(String string, Object... args) {
		MessageUtil.warn(logger.create(), string, args);
	}

	public final void warn(Object object, Object... args) {
		MessageUtil.warn(logger.create(), BStringUtil.toString(object), args);
	}

	public final void debug(String string, Object... args) {
		MessageUtil.debug(logger.create(), string, args);
	}

	public final void debug(Object object, Object... args) {
		MessageUtil.debug(logger.create(), BStringUtil.toString(object), args);
	}

	public final void error(String string, Object... args) {
		MessageUtil.error(logger.create(), string, args);
	}

	public final void error(Object object, Object... args) {
		MessageUtil.error(logger.create(), BStringUtil.toString(object), args);
	}
}

