package net.w3e.base.noise;

import java.util.Random;

import net.w3e.base.math.BMatUtil;

public class SimplexNoiseSampler {
	protected static final int[][] GRADIENTS = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
	private static final double SQRT_3 = Math.sqrt(3.0);
	private static final double SKEW_FACTOR_2D;
	private static final double UNSKEW_FACTOR_2D;
	private final int[] permutation = new int[512];
	public final double originX;
	public final double originY;
	public final double originZ;

	public SimplexNoiseSampler(Random random) {
		this.originX = random.nextDouble() * 256.0;
		this.originY = random.nextDouble() * 256.0;
		this.originZ = random.nextDouble() * 256.0;

		int i;
		for(i = 0; i < 256; this.permutation[i] = i++) {}

		for(i = 0; i < 256; ++i) {
			int j = random.nextInt(256 - i);
			int k = this.permutation[i];
			this.permutation[i] = this.permutation[j + i];
			this.permutation[j + i] = k;
		}
	}

	private int map(int input) {
		return this.permutation[input & 255];
	}

	protected static double dot(int[] gradient, double x, double y, double z) {
		return (double)gradient[0] * x + (double)gradient[1] * y + (double)gradient[2] * z;
	}

	private double grad(int hash, double x, double y, double z, double distance) {
		double d = distance - x * x - y * y - z * z;
		double e;
		if (d < 0.0) {
			e = 0.0;
		} else {
			d *= d;
			e = d * d * dot(GRADIENTS[hash], x, y, z);
		}

		return e;
	}

    public double sample(double x, double y) {
        double d = (x + y) * SKEW_FACTOR_2D;
        int i = BMatUtil.round(x + d);
        int j = BMatUtil.round(y + d);
        double e = (double)(i + j) * UNSKEW_FACTOR_2D;
        double f = (double)i - e;
        double g = (double)j - e;
        double h = x - f;
        double k = y - g;
        byte l;
        byte m;
        if (h > k) {
            l = 1;
            m = 0;
        } else {
            l = 0;
            m = 1;
        }

        double n = h - (double)l + UNSKEW_FACTOR_2D;
        double o = k - (double)m + UNSKEW_FACTOR_2D;
        double p = h - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
        double q = k - 1.0 + 2.0 * UNSKEW_FACTOR_2D;
        int r = i & 255;
        int s = j & 255;
        int t = this.map(r + this.map(s)) % 12;
        int u = this.map(r + l + this.map(s + m)) % 12;
        int v = this.map(r + 1 + this.map(s + 1)) % 12;
        double w = this.grad(t, h, k, 0.0, 0.5);
        double z = this.grad(u, n, o, 0.0, 0.5);
        double aa = this.grad(v, p, q, 0.0, 0.5);
        return 70.0 * (w + z + aa);
    }

    public double sample(double x, double y, double z) {
        double d = 0.3333333333333333;
        double e = (x + y + z) * d;
        int i = BMatUtil.round(x + e);
        int j = BMatUtil.round(y + e);
        int k = BMatUtil.round(z + e);
        double f = 0.16666666666666666;
        double g = (double)(i + j + k) * f;
        double h = (double)i - g;
        double l = (double)j - g;
        double m = (double)k - g;
        double n = x - h;
        double o = y - l;
        double p = z - m;
        byte q;
        byte r;
        byte s;
        byte t;
        byte u;
        byte v;
        if (n >= o) {
            if (o >= p) {
                q = 1;
                r = 0;
                s = 0;
                t = 1;
                u = 1;
                v = 0;
            } else if (n >= p) {
                q = 1;
                r = 0;
                s = 0;
                t = 1;
                u = 0;
                v = 1;
            } else {
                q = 0;
                r = 0;
                s = 1;
                t = 1;
                u = 0;
                v = 1;
            }
        } else if (o < p) {
            q = 0;
            r = 0;
            s = 1;
            t = 0;
            u = 1;
            v = 1;
        } else if (n < p) {
            q = 0;
            r = 1;
            s = 0;
            t = 0;
            u = 1;
            v = 1;
        } else {
            q = 0;
            r = 1;
            s = 0;
            t = 1;
            u = 1;
            v = 0;
        }

        double w = n - (double)q + f;
        double aa = o - (double)r + f;
        double ab = p - (double)s + f;
        double ac = n - (double)t + d;
        double ad = o - (double)u + d;
        double ae = p - (double)v + d;
        double af = n - 1.0 + 0.5;
        double ag = o - 1.0 + 0.5;
        double ah = p - 1.0 + 0.5;
        int ai = i & 255;
        int aj = j & 255;
        int ak = k & 255;
        int al = this.map(ai + this.map(aj + this.map(ak))) % 12;
        int am = this.map(ai + q + this.map(aj + r + this.map(ak + s))) % 12;
        int an = this.map(ai + t + this.map(aj + u + this.map(ak + v))) % 12;
        int ao = this.map(ai + 1 + this.map(aj + 1 + this.map(ak + 1))) % 12;
        double ap = this.grad(al, n, o, p, 0.6);
        double aq = this.grad(am, w, aa, ab, 0.6);
        double ar = this.grad(an, ac, ad, ae, 0.6);
        double as = this.grad(ao, af, ag, ah, 0.6);
        return 32.0 * (ap + aq + ar + as);
    }

	public static double perlinFade(double value) {
        return value * value * value * (value * (value * 6.0 - 15.0) + 10.0);
    }

	public static double perlinFadeDerivative(double value) {
        return 30.0 * value * value * (value - 1.0) * (value - 1.0);
    }

	public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

	public static double lerp2(double deltaX, double deltaY, double x0y0, double x1y0, double x0y1, double x1y1) {
        return lerp(deltaY, lerp(deltaX, x0y0, x1y0), lerp(deltaX, x0y1, x1y1));
    }

	public static double lerp3(double deltaX, double deltaY, double deltaZ, double x0y0z0, double x1y0z0, double x0y1z0, double x1y1z0, double x0y0z1, double x1y0z1, double x0y1z1, double x1y1z1) {
        return lerp(deltaZ, lerp2(deltaX, deltaY, x0y0z0, x1y0z0, x0y1z0, x1y1z0), lerp2(deltaX, deltaY, x0y0z1, x1y0z1, x0y1z1, x1y1z1));
    }

    static {
        SKEW_FACTOR_2D = 0.5 * (SQRT_3 - 1.0);
        UNSKEW_FACTOR_2D = (3.0 - SQRT_3) / 6.0;
    }
}
