package net.w3e.wlib;

import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.jetbrains.annotations.Nullable;

public class PrintWrapper extends java.io.PrintStream {

	public static final Logger LOGGER = LogManager.getRootLogger();
	public static final Logger SETUP = LogManager.getLogger("SETUP");

	public static void install() {
		System.out.println("old writter");
		System.setErr(new PrintWrapper(System.err));
		System.setOut(new PrintWrapper(System.out));
		PrintWrapper.SETUP.warn("writter replaced");
		System.out.println("test replace from " + StackLocatorUtil.getStackTraceElement(2));
	}

	public PrintWrapper(OutputStream out) {
		super(out);
	}

	@Override
	public final void println(boolean x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(char x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(int x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(long x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(float x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(double x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(@Nullable String x) {
		this.logLine(x);
	}

	@Override
	public final void println(Object x) {
		this.logLine(String.valueOf(x));
	}

	private final void logLine(@Nullable String x) {
		StackTraceElement stack = StackLocatorUtil.getStackTraceElement(3);
		String cl = stack.getClassName();
		cl = cl.substring(cl.lastIndexOf(".") + 1);
		String res = "[" + cl + "{" + stack.getMethodName() + ":" + stack.getLineNumber() + "}]: " + x;
		if (res.startsWith("[Throwable$WrappedPrintStream{println:763}]: 	at")) {
			res = "  " + x.substring(1);
		}
		//"[" + stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + "]: "
		this.info(res);
	}

	protected void info(String string) {
		LOGGER.info(string);
	}
}