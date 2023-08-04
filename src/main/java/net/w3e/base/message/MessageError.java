package net.w3e.base.message;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import net.w3e.base.BStringUtil;

public class MessageError {

	private final String text;

	public MessageError(String text) {
		this.text = text;
	}

	public void warn(Logger log, Object... arg) {
		createMsg(new BMessageLogger(log, StackLocatorUtil.getStackTraceElement(2)), arg, false);
	}

	public void warn(Logger log, StackTraceElement stackTraceElement, Object... arg) {
		createMsg(new BMessageLogger(log, stackTraceElement), arg, false);
	}

	public void error(Logger log, Object... arg) {
		createMsg(new BMessageLogger(log, StackLocatorUtil.getStackTraceElement(2)), arg, true);
	}

	public void error(Logger log, StackTraceElement stackTraceElement, Object... arg) {
		createMsg(new BMessageLogger(log, stackTraceElement), arg, true);
	}

	private void createMsg(BMessageLogger logger, Object[] arg, boolean error) {
		if (error) {
			MessageUtil.error(logger, createMsg(arg));
		} else {
			MessageUtil.warn(logger, createMsg(arg));
		}
	}

	public String createMsg(Object... arg) {
		Object[] realArg = new Object[arg.length];
		int i = 0;
		for (Object object : arg) {
			realArg[i] = BStringUtil.toString(object);
			i++;
		}
		return String.format(text, arg);
	}
}
