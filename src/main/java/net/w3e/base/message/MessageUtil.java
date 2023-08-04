package net.w3e.base.message;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import net.w3e.base.BStringUtil;

public class MessageUtil {

	public static final Logger LOGGER = LogManager.getFormatterLogger("w3e/UnsetLogger");

	public static final MessageError EMPTY = new MessageError("%s");
	public static final MessageError ILLEGAL = new MessageError("Illegal state - %s");
	public static final MessageError UNSET = new MessageError("Undifined error - %s");

	public static final MessageError KEY_EXISTS = new MessageError("Key \"%s\" is already exists in %s");

	/**
	 * Key "%s" not found in %s
	 */
	public static final MessageError KEY_NOT_FOUND = new MessageError("Key \"%s\" not found in %s");
	public static final MessageError KEY_NOT_FOUND_NO_COLLECTION = new MessageError("Key \"%s\" not found");
	public static final MessageError KEY_NOT_FOUND_WITH_TYPE = new MessageError("Key \"%s\" of \"%s\" not found in %s");
	public static final MessageError ARRAY_INDEX_OF_BOUNDS = new MessageError("Array index out of range: %s/%s");
	public static final MessageError EMPTY_ARRAY = new MessageError("Collection %s is empty");
	/**
	 * Collection %s already contains "%s"
	 */
	public static final MessageError KEY_DUPLICATE = new MessageError("Collection %s already contains \"%s\"");

	public static final MessageError IS_EMPTY_OR_NULL = new MessageError("%s is empty or null");
	public static final MessageError IS_EMPTY = new MessageError("%s is empty");
	/*
	 * %s is null
	 */
	public static final MessageError NULL = new MessageError("%s is null");
	public static final MessageError NULL_IN = new MessageError("%s is null, %s");
	public static final MessageError CLASS_CAST = new MessageError("%s is not %s");
	public static final MessageError NOT_EQUAL = new MessageError("%s, type %s in not equals %s, type %s");

	public static final MessageError FILE_NOT_FOUND_JAR = new MessageError("File not found in jar - %s");

	public static final MessageError REDIFINE_EMPTY = new MessageError("Datapack tried to redefine \"empty\" for %s, igonring. %s");
	public static final MessageError REDIFINE_ARG = new MessageError("Datapack tried to redefine \"%s\" for %s, igonring. %s");
	public static final MessageError COULD_NOT_PARSE_1 = new MessageError("Couldn't parse %s {}");
	public static final MessageError COULD_NOT_PARSE_2 = new MessageError("Couldn't parse %s {}, %s");
	public static final MessageError COULD_NOT_PARSE_3 = new MessageError("Couldn't parse %s {}, %s, %s");

	public static final MessageError LESS_THAN = new MessageError("%s, %s is less than %s");
	public static final MessageError MORE_THAN = new MessageError("%s, %s is more than %s");
	public static final MessageError MIN_MAX = new MessageError("Min (%s) is more then max (%s)");

	public static final MessageError EXPECTED = new MessageError("Expected \"%s\" to be a \"%s\", was \"%s\"");
	public static final MessageError EXPECTED_SET = new MessageError("Expected \"%s\" to be a \"%s\", was \"%s\", set to \"%s\"");

	public static String parseObject(Object object) {
		if (object != null) {
			return object.toString();
		} else {
			return "null";
		}
	}

	public static String parseTrace(StackTraceElement stackTraceElement) {
		return "[" + stackTraceElement.getClassName() + ":" + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber() + "]: ";
	}

	public static void error(Logger log, Object... object) {
		error(new BMessageLogger(log, StackLocatorUtil.getStackTraceElement(2)), object);
	}

	public static void error(BMessageLogger logger, String msg) {
		logger.error(msg);
	}

	public static void error(BMessageLogger logger, Object... object) {
		logger.error(BStringUtil.toString(object));
	}

	public static void warn(BMessageLogger logger, String string) {
		logger.warn(string);
	}

	public static void info(BMessageLogger logger, String string) {
		logger.info(string);
	}

	public static void debug(BMessageLogger logger, String string) {
		logger.debug(string);
	}
}

