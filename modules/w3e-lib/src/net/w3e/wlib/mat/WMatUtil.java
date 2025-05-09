package net.w3e.wlib.mat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Direction.Axis;
import net.skds.lib2.mat.FastMath;

public class WMatUtil {

	/* ======================== POW ======================== */
	public static int pow(int value, int powValue) {
		if (powValue <= 0) {
			return 1;
		} else if (powValue == 1) {
			return value;
		} else {
			return value * pow(value, powValue - 1);
		}
	}

	public static float pow(float value, float powValue) {
		if (powValue <= 1) {
			return value;
		} else {
			return value * pow(value, powValue - 1);
		}
	}

	public static float pow(float value, int powValue) {
		if (powValue <= 1) {
			return value;
		} else {
			return value * pow(value, powValue - 1);
		}
	}

	@Deprecated // убрать отсюда
	public static String randomName() {
		return "_" + FastMath.RANDOM.nextInt(1000);
	}

	/* ======================== isIn ======================== */
	public static boolean isInRange(int point, int center, int range) {
		return point >= center - range && point <= center + range;
	}
	/* ======================== range ======================== */

	/*public static void main(String[] args) {
		while (true) {
			System.out.println(mapRange(-0.5, -1d, 1d, 0, 50));
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/

	public static int mapRange(int value, int oldMin, int oldMax, int newMin, int newMax) {
		return FastMath.round(mapRange((double)value, oldMin, oldMax, newMin, newMax));
	}

	public static long mapRange(long value, long oldMin, long oldMax, long newMin, long newMax) {
		return FastMath.roundLong(mapRange((double)value, oldMin, oldMax, newMin, newMax));
	}

	public static float mapRange(float value, float oldMin, float oldMax, float newMin, float newMax) {
		return (float)mapRange((double)value, oldMin, oldMax, newMin, newMax);
	}

	public static double mapRange(double value, double oldMin, double oldMax, double newMin, double newMax) {
		return (value - oldMin) * (newMax - newMin) / (oldMax - oldMin) + newMin;
	}

	public static float round(float value, int places) {
		return BigDecimal.valueOf(value).setScale(Math.max(places, 0), RoundingMode.HALF_UP).floatValue();
	}

	public static double round(double value, int places) {
 		return BigDecimal.valueOf(value).setScale(Math.max(places, 0), RoundingMode.HALF_UP).doubleValue();
 	}
 
	/* ======================== hashCode ======================== */
	public static long hashCode(int x, int y, int z) {
		long l = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
		l = l * l * 42317861L + l * 11L;
		return l >> 16;
	}

	/* ======================== sphere ======================== */
	/**
	 * nextDouble
	 */
	public static Vec3 randomPointSphereSurface(Random random, double radius, Vec3 pos) {
		double x = random.nextDouble();
		double y = random.nextDouble();
		double z = random.nextDouble();
		if (x == 0 && y == 0 && z == 0) {
			return pos;
		} else {
			x = x * 2 - 1;
			y = y * 2 - 1;
			z = z * 2 - 1;
			double d = FastMath.invSqrt(x * x + y * y + z * z) * radius;
			x *= d;
			y *= d;
			z *= d;
			return pos.add(x, y, z);
		}
	}

	/**
	 * nextDouble
	 */
	public static Vec3 randomPointSphereInside(Random random, double radius, Vec3 pos) {
		double x = random.nextDouble();
		double y = random.nextDouble();
		double z = random.nextDouble();
		if (x == 0 && y == 0 && z == 0) {
			return pos;
		} else {
			x = x * 2 - 1;
			y = y * 2 - 1;
			z = z * 2 - 1;
			double d = FastMath.invSqrt(x * x + y * y + z * z) * random.nextDouble(radius);
			x *= d;
			y *= d;
			z *= d;
			return pos.add(x, y, z);
		}
	}

	/**
	 * nextDouble
	 */
	public static Vec3 randomPointCircleSurface(Random random, double radius, Vec3 pos) {
		double x = random.nextDouble();
		double z = random.nextDouble();
		if (x == 0 && z == 0) {
			return pos;
		} else {
			x = random.nextDouble() * 2.0 - 1.0;
			z = random.nextDouble() * 2.0 - 1.0;
			double d = FastMath.invSqrt(x * x + z * z) * radius;
			x *= d;
			z *= d;
			return pos.add(x, 0, z);
		}
	}

	/**
	 * nextDouble
	 */
	public static Vec3 randomPointCircleInside(Random random, double radius, Vec3 pos) {
		double x = random.nextDouble();
		double z = random.nextDouble();
		if (x == 0 && z == 0) {
			return pos;
		} else {
			x = random.nextDouble() * 2.0 - 1.0;
			z = random.nextDouble() * 2.0 - 1.0;
			double d = FastMath.invSqrt(x * x + z * z) * random.nextDouble(radius);
			x *= d;
			z *= d;
			return pos.add(x, 0, z);
		}
	}

	public static Vec3 reflect(Vec3 vec, Axis... axis) {
		int x = 1;
		int y = 1;
		int z = 1;

		for (Axis a : axis) {
			switch(a) {
				case X: x = -1; break;
				case Y: y = -1; break;
				case Z: z = -1; break;
			}
		}

		return vec.scale(x, y, z);
	}
}
