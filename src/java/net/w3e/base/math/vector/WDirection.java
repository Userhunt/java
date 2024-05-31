package net.w3e.base.math.vector;

public enum WDirection {
	UP(new WVector3(0, 1, 0), 1),
	DOWN(new WVector3(0, -1, 0), 0),
	NORTH(new WVector3(0, 0, -1), 3, 4, 5),
	SOUTH(new WVector3(0, 0, 1), 2, 5, 4),
	WEST(new WVector3(-1, 0, 0), 5, 2, 3),
	EAST(new WVector3(1, 0, 0), 4, 3, 2);

	private static final WDirection[] VALUES = values();

	public final WVector3 relative;

	private final int offset, left, right;

	private WDirection(WVector3 relative, int offset) {
		this(relative, offset, -1, -1);
	}

	private WDirection(WVector3 relative, int offset, int left, int right) {
		this.relative = relative;
		this.offset = offset;
		this.left = left;
		this. right = right;
	}

	public final WDirection opposite() {
		return this.get(this.offset);
	}

	public final WDirection left() {
		return this.get(this.left);
	}

	public final WDirection right() {
		return this.get(this.right);
	}

	public final WDirection get(int id) {
		if (id == -1 || id > VALUES.length - 1) {
			return this;
		} else {
			return VALUES[id];
		}
	}
}
