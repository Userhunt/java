package net.w3e.base.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.w3e.base.json.BJsonHelper;
import net.w3e.base.json.BJsonUtil;

@SuppressWarnings("deprecation")
public class ApiJsonHelper extends BJsonUtil implements BJsonHelper {

	private final Logger logger;

	public ApiJsonHelper(String logger) {
		this.logger = LogManager.getLogger(logger);
	}

	@Override
	public Logger logger() {
		return this.logger;
	}
}
