package net.w3e.wlib.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import lombok.AllArgsConstructor;
import net.skds.lib2.utils.AutoString;

@AllArgsConstructor
public class LogMessage {

	private final String message;

	public final void info(Object... args) {
		LogUtil.LOGGER.info(parse(args));
	}

	public final void info(Logger log, Object... args) {
		log.info(parse(args));
	}

	public final void warn(Object... args) {
		LogUtil.LOGGER.warn(parse(args));
	}

	public final void warn(Logger log, Object... args) {
		log.warn(parse(args));
	}

	public final void error(Object... args) {
		LogUtil.LOGGER.error(parse(args));
	}

	public final void error(Logger log, Object... args) {
		log.error(parse(args));
	}

	public final void debug(Object... args) {
		LogUtil.LOGGER.debug(parse(args));
	}

	public final void debug(Logger log, Object... args) {
		log.warn(parse(args));
	}

	private String parse(Object... args) {
		Object[] realArg = new Object[args.length];
		int i = 0;
		for (Object arg : args) {
			realArg[i] = AutoString.autoString(arg);
			i++;
		}
		return String.format("%s%s", LogUtil.parseTrace(StackLocatorUtil.getStackTraceElement(3)), createMsg(args));
	}

	public final String createMsg(Object... args) {
		Object[] realArg = new Object[args.length];
		int i = 0;
		for (Object arg : args) {
			realArg[i] = AutoString.autoString(arg);
			i++;
		}
		return String.format(this.message, realArg);
	}
}
