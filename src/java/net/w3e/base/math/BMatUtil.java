package net.w3e.base.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.DoubleSupplier;

public class BMatUtil {

	public static final Random RANDOM = new Random();

	public static final UUID createInsecureUUID() {
		return createInsecureUUID(RANDOM);
	}

	public static final UUID createInsecureUUID(Random random) {
		long l = random.nextLong() & 0xFFFFFFFFFFFF0FFFL | 0x4000L;
		long m = random.nextLong() & 0x3FFFFFFFFFFFFFFFL | Long.MIN_VALUE;
		return new UUID(l, m);
	}

	/* ======================== PI ======================== */

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

	public static final <T extends BVec3<T>> T toRadians(T vec) {
		return vec.scale(DEGREES_TO_RADIANS);
	}

	public static final <T extends BVec3<T>> T toDegrees(T vec) {
		return vec.scale(RADIANS_TO_DEGREES);
	}

	/* ======================== POW ======================== */
	public static final int pow(int value, int powValue) {
		if (powValue <= 0) {
			return 1;
		} else if (powValue == 1) {
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

	/* ======================== range ======================== */
	public static final byte toRange(byte value, byte min, byte max) {
		value -= min;
		value /= max - min;
		return value;
	}

	public static final short toRange(short value, short min, short max) {
		value -= min;
		value /= max - min;
		return value;
	}

	public static final int toRange(int value, int min, int max) {
		value -= min;
		value /= max - min;
		return value;
	}

	public static final long toRange(long value, long min, long max) {
		value -= min;
		value /= max - min;
		return value;
	}

	public static final float toRange(float value, float min, float max) {
		value -= min;
		value /= max - min;
		return value;
	}

	public static final double toRange(double value, double min, double max) {
		value -= min;
		value /= max - min;
		return value;
	}

	/* ======================== clamp ======================== */
	public static byte clamp(byte val, byte min, byte max) {
		return (byte)Math.max(min, Math.min(max, val));
	}

	public static short clamp(short val, short min, short max) {
		return (short)Math.max(min, Math.min(max, val));
	}

	public static int clamp(int value, int min, int max) {
		return Math.clamp(value, min, max);
	}

	public static long clamp(long value, long min, long max) {
		return Math.clamp(value, min, max);
	}

	public static float clamp(float value, float min, float max) {
		return Math.clamp(value, min, max);
	}

	public static double clamp(double value, double min, double max) {
		return Math.clamp(value, min, max);
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

	/* ======================== fastMath ======================== */
	public static final float sin(float a) {
		int b = (int) (a * sinTable.length / 360);
		b %= sinTable.length;
		if (b < 0) {
			b += sinTable.length;
		}
		return sinTable[b];
	}

	public static final float cos(final float x) {
		return sin(x + 90);
	}

	private static final float[] sinTable = new float[1024 * 16];

	static {
		for (int i = 0; i < sinTable.length; i++) {
			sinTable[i] = (float) StrictMath.sin(2 * Math.PI * i / sinTable.length);
		}
	}

	/* ======================== lerp ======================== */
	public static final double lerp(double delta, double start, double end) {
		return start + delta * (end - start);
	}

	public static final double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
		return lerp(deltaY, lerp(deltaX, x0y0, x1y0), lerp(deltaX, x0y1, x1y1));
	}

	public static final double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0, double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
		return lerp(deltaZ, lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
	}

	public static final double clampedLerp(double start, double end, double delta) {
        if (delta < 0.0) {
            return start;
        }
        if (delta > 1.0) {
            return end;
        }
        return BMatUtil.lerp(delta, start, end);
    }

	/* ======================== hashCode ======================== */
	public static final long hashCode(int x, int y, int z) {
        long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
        l = l * l * 42317861L + l * 11L;
        return l >> 16;
    }

	/* ======================== sphere ======================== */
	/**
	 * nextDouble
	 * @param <T>
	 * @param random
	 * @param radius
	 * @param pos
	 * @return
	 */
	public static final <T extends BVec3<T>> T randomPointSphereSurface(DoubleSupplier random, double radius, T pos) {
		double x = random.getAsDouble();
		double y = random.getAsDouble();
		double z = random.getAsDouble();
		if (x == 0 && y == 0 && z == 0) {
			return pos;
		} else {
			x = x * 2 - 1;
			y = y * 2 - 1;
			z = z * 2 - 1;
			double d = 1d/Math.sqrt(x * x + y * y + z * z) * radius;
			x *= d;
			y *= d;
			z *= d;
			return (T)pos.add(x, y, z);
		}
	}

	/**
	 * nextDouble
	 * @param <T>
	 * @param random
	 * @param radius
	 * @param pos
	 * @return
	 */
	public static final <T extends BVec3<T>> T randomPointSphereInside(DoubleSupplier random, double radius, T pos) {
		double x, y, z;
		while(true) {
			x = random.getAsDouble() * 2.0 - 1.0;
			y = random.getAsDouble() * 2.0 - 1.0;
			z = random.getAsDouble() * 2.0 - 1.0;
			if (x * x + y * y + z * z < 1.0) {
				break;
			}
		}
		x *= radius;
		y *= radius;
		z *= radius;
		return pos.add(x, y, z);
	}

	/**
	 * nextDouble
	 * @param <T>
	 * @param random
	 * @param radius
	 * @param pos
	 * @return
	 */
	public static final <T extends BVec3<T>> T randomPointCircleSurface(DoubleSupplier random, double radius, T pos) {
		double x = random.getAsDouble();
		double z = random.getAsDouble();
		if (x == 0 && z == 0) {
			return pos;
		} else {
			x = random.getAsDouble() * 2.0 - 1.0;
			z = random.getAsDouble() * 2.0 - 1.0;
			double d = 1d/Math.sqrt(x * x + z * z) * radius;
			x *= d;
			z *= d;
			return pos.add(x, 0, z);
		}
	}

	/**
	 * nextDouble
	 * @param <T>
	 * @param random
	 * @param radius
	 * @param pos
	 * @return
	 */
	public static final <T extends BVec3<T>> T randomPointCircleInside(DoubleSupplier random, double radius, T pos) {
		double x, z;
		while(true) {
			x = random.getAsDouble() * 2.0 - 1.0;
			z = random.getAsDouble() * 2.0 - 1.0;
			if (x * x + z * z < 1.0) {
				break;
			}
		}
		x *= radius;
		z *= radius;
		return pos.add(x, 0, z);
	}
}
