package net.w3e.base;

import java.util.function.BiFunction;

/**
 * 15.04.23
 */
public class LogicUtil {

	public static final Boolean and(Boolean... array) {
		return calc(true, (bl1, bl2) -> bl1 && bl2, array);
	}

	public static final Boolean nand(Boolean... array) {
		return not(and(array));
	}

	public static final Boolean or(Boolean... array) {
		return calc(false, (bl1, bl2) -> bl1 || bl2, array);
	}

	public static final Boolean nor(Boolean... array) {
		return not(or(array));
	}

	public static final Boolean xor(Boolean... array) {
		if (array.length == 0) {
			return null;
		} else {
			for (Boolean bl : array) {
				if (bl != null) {
					return calc(bl, (bl1, bl2) -> bl1 == bl2, array);
				}
			}
			return null;
		}
	}

	public static final Boolean xnor(Boolean... array) {
		return not(xor(array));
	}

	public static final Boolean not(Boolean bl) {
		if (bl == null) {
			return null;
		} else {
			return !bl;
		}
	}

	public static final Boolean calc(boolean base, BiFunction<Boolean, Boolean, Boolean> function, Boolean... array) {
		boolean any = false;
		for (Boolean bl : array) {
			if (bl != null) {
				any = true;
				base = function.apply(base, bl);
			}
		}
		if (any) {
			return base;
		} else {
			return null;
		}
	}

	public static final boolean valueOrDefault(Boolean value, boolean def) {
		return value != null ? value : def;
	}
}
