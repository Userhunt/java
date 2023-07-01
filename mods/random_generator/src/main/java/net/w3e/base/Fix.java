package net.w3e.base;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Fix {

	/*
	BMatUtil.round -> Math.round
	min()
	max()
	 */

	public static double round(double value, int places) {
		return BigDecimal.valueOf(value).setScale(Math.max(places, 0), RoundingMode.HALF_UP).doubleValue();
	}
}
