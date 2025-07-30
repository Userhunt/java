package net.w3e.wlib.mat;

import net.skds.lib2.mat.FastMath;

public class WIntRectangle {
	public int x1;
	public int x2;
	public int y1;
	public int y2;

	public WIntRectangle() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public WIntRectangle(int x1, int x2, int y1, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public WIntRectangle from(WIntRectangle rectangle) {
		this.x1 = rectangle.x1;
		this.x2 = rectangle.x2;
		this.y1 = rectangle.y1;
		this.y2 = rectangle.y2;
		return this;
	}

	public WIntRectangle copy() {
		return new WIntRectangle(x1, x2, y1, y2);
	}

	public boolean isIn(double x, double y) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	public boolean isContact(WIntRectangle contact) {
		return isContact(contact.x1, contact.y1, contact.x2, contact.y2);
	}

	public boolean isContact(int x1, int y1, int x2, int y2) {
		// x1 вообще не попал в диапозон
		if (x1 > this.x2) {
			return false;
		}
		// x2 вообще не попал в диапозон
		if (x2 < this.x1) {
			return false;
		}
		// y1 вообще не попал в диапозон
		if (y1 > this.y2) {
			return false;
		}
		// y2 вообще не попал в диапозон
		if (y2 < this.y1) {
			return false;
		}

		return true;
	}

	public WIntRectangle cutTo(WIntRectangle cut) {
		return cutTo(cut.x1, cut.x2, cut.y1, cut.y2);
	}

	public WIntRectangle cutTo(int x1, int x2, int y1, int y2) {
		if (this.x1 < x1) {
			this.x1 = x1;
		}
		if (this.x2 > x2) {
			this.x2 = x2;
		}
		if (this.y1 < y1) {
			this.y1 = y1;
		}
		if (this.y2 > y2) {
			this.y2 = y2;
		}
		return this;
	}

	public WIntRectangle translate(int x, int y) {
		x1 += x;
		x2 += x;
		y1 += y;
		y2 += y;
		return this;
	}

	public WIntRectangle scale(double scale) {
		if (scale < 0 || scale == 1) {
			return this;
		}

		int w = x2 - x1;
		w = FastMath.round((w - w * scale) / 2);
		this.x1 += w;
		this.x2 -= w;

		int h = y2 - y1;
		h = FastMath.round((h - h * scale) / 2);
		this.y1 += h;
		this.y2 -= h;

		return this;
	}

	public WIntRectangle connect(WIntRectangle rectangle) {
		return connect(this, rectangle, false);
	}

	public WIntRectangle mul(WIntRectangle rectangle) {
		return mul(this, rectangle, false);
	}

	public WIntRectangle expand(int xy) {
		return expand(xy, false);
	}

	public WIntRectangle expand(int xy, boolean create) {
		return expand(xy, xy, false);
	}

	public WIntRectangle expand(int x, int y) {
		return expand(x, y, false);
	}

	public WIntRectangle expand(int x, int y, boolean create) {
		WIntRectangle rectangle = this;
		if (create) {
			rectangle = copy();
		}

		rectangle.x1 -= x / 2;
		rectangle.x2 += x;
		rectangle.y1 -= y / 2;
		rectangle.y2 += y;

		return rectangle;
	}

	public final void moveTo(WIntRectangle rectangle) {
		this.moveTo(rectangle.x1, rectangle.x2 - rectangle.x1, rectangle.y1, rectangle.y2 - rectangle.y1);
	}

	public final void moveTo(int x, int w, int y, int h) {
		int t1 = movTo(this.x1, this.x2, x, w);
		int t2 = movTo(this.y1, this.y2, y, h);
		if (t1 != 0 || t2 != 0) {
			translate(t1, t2);
		}
	}

	private final int movTo(int g1, int g2, int j1, int j2) {
		int s1 = g2 - g1;
		int s2 = j2 - j1;
		if (s1 > s2) {
			int d1 = (j1 + j2) / 2;
			int d2 = (g1 + g2) / 2;
			return d1 - d2;
		} else {
			if (g1 >= j1 && g2 <= j2) {
				return 0;
			} else {
				if (j2 < g2) {
					return g2 - j2;
				} else {
					return j1 - g1;
				}
			}
		}
	}

	public final void substractTo(WIntRectangle area, WIntRectangle max) {
		this.x1 = FastMath.clamp(area.x1 - this.x1, max.x1, max.x2);
		this.x2 = FastMath.clamp(area.x2 - this.x2, max.x1, max.x2);
		this.y1 = FastMath.clamp(area.y1 - this.y1, max.y1, max.y2);
		this.y2 = FastMath.clamp(area.y2 - this.y2, max.y1, max.y2);
	}

	@Override
	public String toString() {
		return String.format("{x:[%s,%s],y:[%s,%s]}", x1, x2, y1, y2);
	}

	public static WIntRectangle connect(WIntRectangle w1, WIntRectangle w2) {
		return connect(w1, w2, true);
	}

	public static WIntRectangle connect(WIntRectangle w1, WIntRectangle w2, boolean create) {
		WIntRectangle rectangle;
		if (create) {
			rectangle = new WIntRectangle();
		} else {
			rectangle = w1;
		}

		rectangle.x1 = Math.min(w1.x1, w2.x1);
		rectangle.y1 = Math.min(w1.y1, w2.y1);
		rectangle.x2 = Math.max(w1.x2, w2.x2);
		rectangle.y2 = Math.max(w1.y2, w2.y2);

		return rectangle;
	}

	public static WIntRectangle mul(WIntRectangle w1, WIntRectangle w2) {
		return mul(w1, w2, true);
	}

	public static WIntRectangle mul(WIntRectangle w1, WIntRectangle w2, boolean create) {
		WIntRectangle rectangle;
		if (create) {
			rectangle = new WIntRectangle();
		} else {
			rectangle = w1;
		}

		rectangle.x1 = Math.max(w1.x1, w2.x1);
		rectangle.y1 = Math.max(w1.y1, w2.y1);
		rectangle.x2 = Math.min(w1.x2, w2.x2);
		rectangle.y2 = Math.min(w1.y2, w2.y2);

		return rectangle;
	}
}
