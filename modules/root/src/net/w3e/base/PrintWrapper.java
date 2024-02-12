package net.w3e.base;

import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.jetbrains.annotations.Nullable;

import net.w3e.base.message.BMessageLoggerHelper;

/**
 * 15.04.23
 */
public class PrintWrapper extends java.io.PrintStream {

	public static final Logger LOGGER = LogManager.getRootLogger();
	public static final Logger SETUP = LogManager.getLogger("SETUP");

	public static final BMessageLoggerHelper MSG_UTIL = new BMessageLoggerHelper(LOGGER);

	public static void install() {
		System.setErr(new PrintWrapper(System.err));
		System.setOut(new PrintWrapper(System.out));
		PrintWrapper.SETUP.warn("writter replaced");
		System.out.println("test replace from " + StackLocatorUtil.getStackTraceElement(2));
	}

	public PrintWrapper(OutputStream out) {
		super(out);
	}

	public final void println(boolean x) {
		this.logLine(String.valueOf(x));
	}

	public final void println(char x) {
		this.logLine(String.valueOf(x));
	}

	public final void println(int x) {
		this.logLine(String.valueOf(x));
	}

	public final void println(long x) {
		this.logLine(String.valueOf(x));
	}

	public final void println(float x) {
		this.logLine(String.valueOf(x));
	}

	public final void println(double x) {
		this.logLine(String.valueOf(x));
	}

	public final void println(@Nullable String x) {
		this.logLine(x);
	}

	public final void println(Object x) {
		this.logLine(String.valueOf(x));
	}

	private final void logLine(@Nullable String x) {
		StackTraceElement stack = StackLocatorUtil.getStackTraceElement(3);
		String cl = stack.getClassName();
		cl = cl.substring(cl.lastIndexOf(".") + 1);
		String res = "[" + cl + "{" + stack.getMethodName() + ":" + stack.getLineNumber() + "}]: " + x;
		//"[" + stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + "]: "
		this.info(res);
	}

	protected void info(String string) {
		LOGGER.info(string);
	}

}