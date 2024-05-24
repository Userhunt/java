package net.home.random_generator.noise;

import java.util.Random;

public class Perlin2D {
	private final byte[] permutationTable;

	public Perlin2D() {
		this(0);
	}

	public Perlin2D(int seed) {
		this(new Random(seed));
	}

	public Perlin2D(Random random) {
		permutationTable = new byte[1024];
		random.nextBytes(permutationTable);
	}

	private final float[] getPseudoRandomGradientVector(long x, long y) {
		int v = (int)(((x * 1836311903) ^ (y * 2971215073L) + 4807526976L) & 1023);
		v = permutationTable[v] & 3;

		return switch (v) {
			case 0 ->  new float[]{ 1, 0};
			case 1 ->  new float[]{-1, 0};
			case 2 ->  new float[]{ 0, 1};
			default -> new float[]{ 0,-1};
		};
	}

	private final float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

	private final float dot(float[] a, float[] b) {
		return a[0] * b[0] + a[1] * b[1];
	}

	public final float noise(float fx, float fy) {
		int left = (int)Math.floor(fx);
		int top  = (int)Math.floor(fy);
		float pointInQuadX = fx - left;
		float pointInQuadY = fy - top;

		float[] topLeftGradient     = getPseudoRandomGradientVector(left,   top  );
		float[] topRightGradient    = getPseudoRandomGradientVector(left+1, top  );
		float[] bottomLeftGradient  = getPseudoRandomGradientVector(left,   top+1);
		float[] bottomRightGradient = getPseudoRandomGradientVector(left+1, top+1);

		float[] distanceToTopLeft     = new float[]{ pointInQuadX,   pointInQuadY   };
		float[] distanceToTopRight    = new float[]{ pointInQuadX-1, pointInQuadY   };
		float[] distanceToBottomLeft  = new float[]{ pointInQuadX,   pointInQuadY-1 };
		float[] distanceToBottomRight = new float[]{ pointInQuadX-1, pointInQuadY-1 };

		float tx1 = dot(distanceToTopLeft,     topLeftGradient);
		float tx2 = dot(distanceToTopRight,    topRightGradient);
		float bx1 = dot(distanceToBottomLeft,  bottomLeftGradient);
		float bx2 = dot(distanceToBottomRight, bottomRightGradient);

		pointInQuadX = (float)SimplexNoiseSampler.perlinFade(pointInQuadX);
		pointInQuadY = (float)SimplexNoiseSampler.perlinFade(pointInQuadY);

		float tx = lerp(tx1, tx2, pointInQuadX);
		float bx = lerp(bx1, bx2, pointInQuadX);
		float tb = lerp(tx, bx, pointInQuadY);

		return tb;
	}

	public final float noise(float fx, float fy, int octaves) {
		return this.noise(fx, fy, octaves, 0.5f);
	}

	public final float noise(float fx, float fy, int octaves, float persistence) {
		float amplitude = 1;
		float max = 0;
		float result = 0;

		while (octaves-- > 0) {
			max += amplitude;
			result += noise(fx, fy) * amplitude;
			amplitude *= persistence;
			fx *= 2;
			fy *= 2;
		}

		return result/max;
	}
}