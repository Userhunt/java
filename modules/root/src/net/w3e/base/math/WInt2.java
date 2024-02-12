package net.w3e.base.math;

public class WInt2 {
	private int x;
	private int y;
	private boolean modify;

	public WInt2() {
		this(100, 100);
	}

	public WInt2(int x, int y) {
		this(x, y, true);
	}

	public WInt2(int x, int y, boolean modify) {
		this.x = x;
		this.y = y;
		this.modify = modify;
	}

	public final int x() {
		return this.x;
	}

	public final int y() {
		return this.y;
	}

	public WInt2 lock() {
		this.modify = false;
		return this;
	}

	public WInt2 from(WInt2 other) {
		if (this.modify) {
			this.x = other.x;
			this.y = other.y;
		}
		return this;
	}

	public WInt2 copy() {
		return new WInt2(this.x, this.y);
	}

	public WInt2 translate(int x, int y) {
		if (this.modify) {
			this.x += x;
			this.y += y;
		}
		return this;
	}

	public WInt2 scale(double scale) {
		if (scale < 0 || scale == 1 || !this.modify) {
			return this;
		}
		this.x *= scale;
		this.y *= scale;
		return this;
	}

	@Override
	public String toString() {
		return String.format("{x:%s,y:%s}", x, y);
	}
}
