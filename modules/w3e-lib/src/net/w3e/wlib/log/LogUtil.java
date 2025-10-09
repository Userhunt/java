package net.w3e.wlib.log;

import net.skds.lib2.utils.logger.SKDSLogger;

public class LogUtil {

	public static final SKDSLogger LOGGER = new SKDSLogger();

	public static final LogMessage EMPTY = new LogMessage("%s");
	public static final LogMessage ILLEGAL = new LogMessage("Illegal state - %s");
	public static final LogMessage UNSET = new LogMessage("Undifined error - %s");

	public static final LogMessage KEY_EXISTS = new LogMessage("Key \"%s\" is already exists in %s");

	/**
	 * Key "%s" not found in %s
	 */
	public static final LogMessage KEY_NOT_FOUND = new LogMessage("Key \"%s\" not found in %s");
	public static final LogMessage KEY_NOT_FOUND_NO_COLLECTION = new LogMessage("Key \"%s\" not found");
	public static final LogMessage KEY_NOT_FOUND_WITH_TYPE = new LogMessage("Key \"%s\" of \"%s\" not found in %s");
	public static final LogMessage ARRAY_INDEX_OF_BOUNDS = new LogMessage("Array index out of range: %s/%s");
	public static final LogMessage EMPTY_ARRAY = new LogMessage("Collection %s is empty");
	/**
	 * Collection %s already contains "%s"
	 */
	public static final LogMessage KEY_DUPLICATE = new LogMessage("Collection %s already contains \"%s\"");

	/**
	 * %s is empty or null
	 */
	public static final LogMessage IS_EMPTY_OR_NULL = new LogMessage("%s is empty or null");
	public static final LogMessage IS_EMPTY = new LogMessage("%s is empty");
	/*
	 * %s is null
	 */
	public static final LogMessage NULL = new LogMessage("%s is null");
	public static final LogMessage NULL_IN = new LogMessage("%s is null, %s");
	public static final LogMessage CLASS_CAST = new LogMessage("%s is not %s");
	/*
	 * %s, type %s in not equals %s, type %s
	 */
	public static final LogMessage NOT_EQUAL = new LogMessage("%s, type %s in not equals %s, type %s");

	public static final LogMessage FILE_NOT_FOUND_JAR = new LogMessage("File not found in jar - %s");

	public static final LogMessage REDIFINE_EMPTY = new LogMessage("Datapack tried to redefine \"empty\" for %s, igonring. %s");
	public static final LogMessage REDIFINE_ARG = new LogMessage("Datapack tried to redefine \"%s\" for %s, igonring. %s");
	public static final LogMessage COULD_NOT_PARSE_1 = new LogMessage("Couldn't parse %s {}");
	public static final LogMessage COULD_NOT_PARSE_2 = new LogMessage("Couldn't parse %s {}, %s");
	public static final LogMessage COULD_NOT_PARSE_3 = new LogMessage("Couldn't parse %s {}, %s, %s");

	public static final LogMessage LESS_THAN = new LogMessage("%s, %s is less than %s");
	public static final LogMessage MORE_THAN = new LogMessage("%s, %s is more than %s");
	public static final LogMessage MIN_MAX = new LogMessage("Min (%s) is more then max (%s)");

	public static final LogMessage EXPECTED = new LogMessage("Expected \"%s\" to be a \"%s\", was \"%s\"");
	public static final LogMessage EXPECTED_SET = new LogMessage("Expected \"%s\" to be a \"%s\", was \"%s\", set to \"%s\"");

	public static final LogMessage REQUIRED = new LogMessage("Required \"%s\"");

}
