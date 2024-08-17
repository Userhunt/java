package net.w3e.base.math.vector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.w3e.base.math.vector.i.WVector3I;

@AllArgsConstructor
public enum WDirection {
	UP(new WVector3I(0, 1, 0), 1),
	DOWN(new WVector3I(0, -1, 0), 0),
	NORTH(new WVector3I(0, 0, -1), 3, 4, 5),
	SOUTH(new WVector3I(0, 0, 1), 2, 5, 4),
	WEST(new WVector3I(-1, 0, 0), 5, 3, 2),
	EAST(new WVector3I(1, 0, 0), 4, 2, 3);

	private static final WDirection[] VALUES = values();

	@Getter
	private final WVector3I relative;

	private final int offset, left, right;

	private WDirection(WVector3I relative, int offset) {
		this(relative, offset, -1, -1);
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
