package net.w3e.base.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.w3e.base.json.BJsonHelper;
import net.w3e.base.json.BJsonUtil;

public class ApiJsonHelper extends BJsonUtil implements BJsonHelper {

	private final Logger logger;

	public ApiJsonHelper(String logger) {
		this(LogManager.getLogger(logger));
	}

	public ApiJsonHelper(Logger logger) {
		this.logger = logger;
	}

	@Override
	public Logger logger() {
		return this.logger;
	}
}
