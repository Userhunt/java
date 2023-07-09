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

	public final void info(Object object) {
		MessageUtil.info(logger.create(), BStringUtil.toString(object));
	}

	public final void warn(String string) {
		MessageUtil.warn(logger.create(), string);
	}

	public final void warn(Object object) {
		MessageUtil.warn(logger.create(), BStringUtil.toString(object));
	}

	public final void debug(String string) {
		MessageUtil.debug(logger.create(), string);
	}

	public final void debug(Object object) {
		MessageUtil.debug(logger.create(), BStringUtil.toString(object));
	}

	public final void error(String string) {
		MessageUtil.error(logger.create(), string);
	}

	public final void error(Object object) {
		MessageUtil.error(logger.create(), BStringUtil.toString(object));
	}
}

