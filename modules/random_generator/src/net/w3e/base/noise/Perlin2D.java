package net.w3e.base.noise;

import java.util.Random;

import net.w3e.base.math.BMatUtil;

public class Perlin2D {
	private final byte[] permutationTable;

	public Perlin2D() {
		this(0);
	}

	public Perlin2D(long seed) {
		Random rand = new Random(seed);
		permutationTable = new byte[1024];
		rand.nextBytes(permutationTable);
	}

	private final float[] GetPseudoRandomGradientVector(int x, int y){
		int v = (int)(((x * 1836311903) ^ (y * 2971215073L) + 4807526976L) & 1023);
		v = permutationTable[v]&3;

		return switch (v) {
			case 0 ->  new float[]{  1, 0 };
			case 1 ->  new float[]{ -1, 0 };
			case 2 ->  new float[]{  0, 1 };
			default -> new float[]{  0,-1 };
		};
	}

	private static final float QunticCurve(float t){
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private static final float Lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

	private static final float Dot(float[] a, float[] b) {
		return a[0] * b[0] + a[1] * b[1];
	}

	public final float noise(float fx, float fy) {
		int left = BMatUtil.round(fx);
		int top  = BMatUtil.round(fy);
		float pointInQuadX = fx - left;
		float pointInQuadY = fy - top;

		float[] topLeftGradient     = GetPseudoRandomGradientVector(left,   top  );
		float[] topRightGradient    = GetPseudoRandomGradientVector(left+1, top  );
		float[] bottomLeftGradient  = GetPseudoRandomGradientVector(left,   top+1);
		float[] bottomRightGradient = GetPseudoRandomGradientVector(left+1, top+1);

		float[] distanceToTopLeft     = new float[]{ pointInQuadX,   pointInQuadY   };
		float[] distanceToTopRight    = new float[]{ pointInQuadX-1, pointInQuadY   };
		float[] distanceToBottomLeft  = new float[]{ pointInQuadX,   pointInQuadY-1 };
		float[] distanceToBottomRight = new float[]{ pointInQuadX-1, pointInQuadY-1 };

		float tx1 = Dot(distanceToTopLeft,     topLeftGradient);
		float tx2 = Dot(distanceToTopRight,    topRightGradient);
		float bx1 = Dot(distanceToBottomLeft,  bottomLeftGradient);
		float bx2 = Dot(distanceToBottomRight, bottomRightGradient);

		pointInQuadX = QunticCurve(pointInQuadX);
		pointInQuadY = QunticCurve(pointInQuadY);

		float tx = Lerp(tx1, tx2, pointInQuadX);
		float bx = Lerp(bx1, bx2, pointInQuadX);
		float tb = Lerp(tx, bx, pointInQuadY);

		return tb;
	}

	public float noise(float fx, float fy, int octaves) {
		return noise(fx, fy, octaves, .5f);
	}

	public float noise(float fx, float fy, int octaves, float persistence) {
		float amplitude = 1;
		float max = 0;
		float result = 0;

		while (octaves-- > 0)
		{
			max += amplitude;
			result += noise(fx, fy) * amplitude;
			amplitude *= persistence;
			fx *= 2;
			fy *= 2;
		}

		return result/max;
	}
}
