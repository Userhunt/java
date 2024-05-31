package net.w3e.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import net.w3e.base.message.BMessageLogger;

public class NotFoundManager {

	private static final Logger LOGGER = LogManager.getFormatterLogger("librity/NotFoundManager");

	private static final Map<Class<?>, NotFoundManagerIns> client = new HashMap<>();
	private static final Map<Class<?>, NotFoundManagerIns> server = new HashMap<>();

	public static void init(Class<?> cls, Logger logger, String formatedMsg, boolean server) {
		init(cls, new NotFoundManagerIns(logger, formatedMsg), server);
	}

	public static void init(Class<?> cls, NotFoundManagerIns manager, boolean server) {
		if (server) {
			NotFoundManager.server.put(cls, manager);
		} else {
			NotFoundManager.client.put(cls, manager);
		}
	}

	public static void init(Class<?> cls, Logger logger) {
		init(cls, logger, cls.getSimpleName() + " : \"%s\", not found, keys:%s");
	}

	public static void init(Class<?> cls, Logger logger, String formatedMsg) {
		NotFoundManagerIns manager = new NotFoundManagerIns(logger, formatedMsg);
		init(cls, manager);
	}

	public static void init(Class<?> cls, NotFoundManagerIns manager) {
		init(cls, manager, true);
		init(cls, manager, false);
	}

	public static void clear(boolean server) {
		if (server) {
			NotFoundManager.server.forEach((key, value) -> {
				value.clear();
			});
		} else {
			NotFoundManager.client.forEach((key, value) -> {
				value.clear();
			});
		}
	}

	public static void log(Class<?> cls, String key, String... args) {
		log(cls, key, true, args);
	}

	public static void log(Class<?> cls, String key, boolean server, String... args) {
		NotFoundManagerIns manager = null;
		if (server) {
			manager = NotFoundManager.server.get(cls);
		} else {
			manager = NotFoundManager.client.get(cls);
		}
		if (manager != null) {
			manager.log(key, args);
		} else {
			new BMessageLogger(LOGGER, StackLocatorUtil.getStackTraceElement(4)).error("manager " + cls + " is not found");
		}
	}

	public static class NotFoundManagerIns {

		public final String msg;
		public final Logger logger;
		private final Set<String> set = new HashSet<>();

		public NotFoundManagerIns(Logger logger, String formatedMsg) {
			this.logger = logger;
			this.msg = formatedMsg;
		}

		public void log(String key, String[] args) {
			if (set.contains(key)) {
				return;
			}
			try {
				logger.warn(String.format(msg, (Object[])args));
			} catch (Exception e) {
				logger.warn(msg);
				logger.warn(BStringUtil.toString(args));
			}
			set.add(key);
		}

		public void clear() {
			set.clear();
		}
	}
}
