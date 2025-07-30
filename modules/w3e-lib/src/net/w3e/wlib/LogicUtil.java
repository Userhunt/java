package net.w3e.wlib;

import java.util.function.BiFunction;

public class LogicUtil {

	public static final Boolean and(Boolean... array) {
		return calc(true, (bl1, bl2) -> bl1 && bl2, array);
	}

	public static final boolean and(boolean... array) {
		return calc(true, (bl1, bl2) -> bl1 && bl2, array);
	}

	public static final Boolean nand(Boolean... array) {
		return not(and(array));
	}

	public static final boolean nand(boolean... array) {
		return not(and(array));
	}

	public static final Boolean or(Boolean... array) {
		return calc(false, (bl1, bl2) -> bl1 || bl2, array);
	}

	public static final boolean or(boolean... array) {
		return calc(false, (bl1, bl2) -> bl1 || bl2, array);
	}

	public static final Boolean nor(Boolean... array) {
		return not(or(array));
	}

	public static final Boolean nor(boolean... array) {
		return not(or(array));
	}

	/*public static final Boolean xor(Boolean... array) {
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
	}*/

	/*public static final Boolean xnor(Boolean... array) {
		return not(xor(array));
	}*/

	public static final Boolean not(Boolean bl) {
		if (bl == null) {
			return null;
		} else {
			return !bl;
		}
	}

	public static final Boolean not(boolean bl) {
		return !bl;
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

	public static interface LogicUtilCalc {
		boolean apply(boolean bl1, boolean bl2);
	}

	public static final boolean calc(boolean base, LogicUtilCalc function, boolean... array) {
		if (array.length == 0) {
			return base;
		}
		for (boolean bl : array) {
			base = function.apply(base, bl);
		}
		return base;
	}

	public static final boolean valueOrDefault(Boolean value, boolean def) {
		return value != null ? value : def;
	}

	public static int count(boolean... array) {
		int i = 0;
		for (boolean bl : array) {
			if (bl) {
				i++;
			}
		}
		return i;
	}
}
