package net.w3e.base.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

public class BMatUtil {

	public static final Random RANDOM = new Random();

	public static final double RA = Math.PI / 180.0d;

	/**
	 * Constant by which to multiply an angular value in degrees to obtain an
	 * angular value in radians.
	 */
	public static final double DEGREES_TO_RADIANS = 0.017453292519943295;

	/**
	 * Constant by which to multiply an angular value in radians to obtain an
	 * angular value in degrees.
	 */
	public static final double RADIANS_TO_DEGREES = 57.29577951308232;

	public static final float toRadians(float value) {
		return (float)(value * DEGREES_TO_RADIANS);
	}

	public static final float toDegrees(float value) {
		return (float)(value * RADIANS_TO_DEGREES);
	}

	/* ======================== POW ======================== */
	public static final int pow(int value, int powValue) {
		if (powValue <= 1) {
			return value;
		} else {
			return value * pow(value, powValue - 1);
		}
	}

	public static final float pow(float value, float powValue) {
		if (powValue <= 1) {
			return value;
		} else {
			return value * pow(value, powValue - 1);
		}
	}

	public static final float pow(float value, int powValue) {
		if (powValue <= 1) {
			return value;
		} else {
			return value * pow(value, powValue - 1);
		}
	}



	public static final String randomName() {
		return "_" + (int)(Math.random() * 1000);
	}

	/* ======================== ROUND ======================== */
	//(int)(v >= 0 ? (v + .5) : (v - .5))
	public static final int round(float v) {
		return Math.round(v);
	}

	public static final int round(double v) {
		return (int)roundLong(v);
	}

	public static final long roundLong(double v) {
		return Math.round(v);
	}

	public static double round(double value, int places) {
		return BigDecimal.valueOf(value).setScale(Math.max(places, 0), RoundingMode.HALF_UP).doubleValue();
	}

	public static float round(float value, int places) {
		return BigDecimal.valueOf(value).setScale(Math.max(places, 0), RoundingMode.HALF_UP).floatValue();
	}

	/* ======================== AVERAGE ======================== */
	public static final double average(List<Integer> list) {
		if (list.isEmpty()) {
			return 0;
		}
		return list.stream().mapToInt(a -> a).average().getAsDouble();
	}

	public static final int averageInt(List<Integer> list) {
		return averageInt(list, true);
	}

	public static final int averageInt(List<Integer> list, boolean up) {
		double d = average(list);
		if (up) {
			d += .49999999999999;
		}
		return BMatUtil.round(d);
	}

	/* ======================== isIn ======================== */
	public static final boolean isIn(int point, int center, int range) {
		return point >= center - range && point <= center + range;
	}
	public static final int distance(int point, int center, int range) {
		if (isIn(point, center, range)) {
			return distance(point, center);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public static final int distance(int point, int center) {
		if (point == center) {
			return 0;
		} else if (point > center) {
			return point - center;
		} else {
			return center - point;
		}
	}

	public static final int distance(int x1, int x2, int y1, int y2) {
		int dx = x1 - x2;
		int dy = y1 - y2;
		return dx * dx + dy * dy;
	}

	/* ======================== clamp ======================== */
	public static int clamp(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}

	public static float clamp(float value, float min, float max) {
		return value < min ? min : Math.min(value, max);
	}

	public static double clamp(double value, double min, double max) {
		return value < min ? min : Math.min(value, max);
	}

	/* ======================== minMax ======================== */

	public static final byte min(byte a, byte b) {
		return (byte)Math.min(a, b);
	}

	public static final byte max(byte a, byte b) {
		return (byte)Math.max(a, b);
	}

	public static final int min(int a, int b) {
		return Math.min(a, b);
	}

	public static final int max(int a, int b) {
		return Math.max(a, b);
	}

	public static final long min(long a, long b) {
		return Math.min(a, b);
	}

	public static final long max(long a, long b) {
		return Math.max(a, b);
	}

	public static final float min(float a, float b) {
		return Math.min(a, b);
	}

	public static final float max(float a, float b) {
		return Math.max(a, b);
	}

	public static final double min(double a, double b) {
		return Math.min(a, b);
	}

	public static final double max(double a, double b) {
		return Math.max(a, b);
	}
}
