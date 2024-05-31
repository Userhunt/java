package net.w3e.base.math.vector;

public class WBox {

	private int minX, minY, minZ;
	private int maxX, maxY, maxZ;

	public WBox(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	public final WBox expand(int value) {
		return this.expand(value, value, value);
	}

	public final WBox expand(int x, int y, int z) {
		return this.modify(x, y, z);
	}

	public final WBox inflate(int value) {
		return this.inflate(value, value, value);
	}

	public final WBox inflate(int x, int y, int z) {
		return this.modify(x * -1, y * -1, z * -1);
	}

	public final WBox offset(int x, int y, int z) {
		this.minX += x;
		this.minY += y;
		this.minZ += z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
		return this;
	}

	public final WBox move(int x, int y, int z) {
		WVector3 center = center();
		return offset(x - center.getX(), y - center.getY(), z - center.getZ());
	}

	public boolean contains(int x, int y, int z) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
	}

	public boolean contains(IWVector<?> vector) {
		return this.contains(vector.getX(), vector.getY(), vector.getZ());
	}

	public final WVector3 size() {
		return new WVector3(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
	}

	public final WVector3 min() {
		return new WVector3(this.minX, this.minY, this.minZ);
	}

	public final WVector3 max() {
		return new WVector3(this.maxX, this.maxY, this.maxZ);
	}

	public final WVector3 center() {
		return new WVector3((this.minX + this.maxX) / 2, (this.minY + this.maxY) / 2, (this.minZ + this.maxZ) / 2);
	}

	public final WBox copy() {
		return new WBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public static final WBox MAX() {
		int min = -10000;
		int max = 10000;
		return new WBox(min, min, min, max, max, max);
	}

	public static final WBox of(int x, int y, int z) {
		return new WBox(0, 0, 0, x, y, z);
	}

	private final WBox modify(int x, int y, int z) {
		return this.modifyX(x).modifyY(y).modifyZ(z);
	}

	private final WBox modifyX(int value) {
		int min = this.minX;
		int max = this.maxX;
		this.minX -= value;
		this.maxX += value;
		if (this.minX > this.maxX) {
			int v = (min + max) / 2;
			this.minX = v;
			if (min + max % 2 == 0) {
				this.maxX = v;
			} else {
				this.maxX = v + 1;
			}
		}
		return this;
	}

	private final WBox modifyY(int value) {
		int min = this.minY;
		int max = this.maxY;
		this.minY -= value;
		this.maxY += value;
		if (this.minY > this.maxY) {
			int v = (min + max) / 2;
			this.minY = v;
			if (min + max % 2 == 0) {
				this.maxY = v;
			} else {
				this.maxY = v + 1;
			}
		}
		return this;
	}

	private final WBox modifyZ(int value) {
		int min = this.minZ;
		int max = this.maxZ;
		this.minZ -= value;
		this.maxZ += value;
		if (this.minZ > this.maxZ) {
			int v = (min + max) / 2;
			this.minZ = v;
			if (min + max % 2 == 0) {
				this.maxZ = v;
			} else {
				this.maxZ = v + 1;
			}
		}
		return this;
	}
}
