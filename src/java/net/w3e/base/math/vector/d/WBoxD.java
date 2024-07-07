package net.w3e.base.math.vector.d;

public class WBoxD {

	private double minX, minY, minZ;
	private double maxX, maxY, maxZ;

	public WBoxD(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	public final WBoxD expand(double value) {
		return this.expand(value, value, value);
	}

	public final WBoxD expand(double x, double y, double z) {
		return this.modify(x, y, z);
	}

	public final WBoxD inflate(double value) {
		return this.inflate(value, value, value);
	}

	public final WBoxD inflate(double x, double y, double z) {
		return this.modify(x * -1, y * -1, z * -1);
	}

	public final WBoxD offset(double x, double y, double z) {
		this.minX += x;
		this.minY += y;
		this.minZ += z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
		return this;
	}

	public final WBoxD move(double x, double y, double z) {
		WVector3D center = this.center();
		return offset(x - center.getXD(), y - center.getYD(), z - center.getZD());
	}

	public boolean contains(double x, double y, double z) {
		return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
	}

	public boolean contains(IWVectorND<?> vector) {
		return this.contains(vector.getXD(), vector.getYD(), vector.getZD());
	}

	public final WVector3D size() {
		return new WVector3D(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
	}

	public final WVector3D min() {
		return new WVector3D(this.minX, this.minY, this.minZ);
	}

	public final WVector3D max() {
		return new WVector3D(this.maxX, this.maxY, this.maxZ);
	}

	public final WVector3D center() {
		return new WVector3D((this.minX + this.maxX) / 2, (this.minY + this.maxY) / 2, (this.minZ + this.maxZ) / 2);
	}

	public final WBoxD copy() {
		return new WBoxD(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	public static final WBoxD MAX() {
		double min = -10000;
		double max = 10000;
		return new WBoxD(min, min, min, max, max, max);
	}

	public static final WBoxD of(double x, double y, double z) {
		return new WBoxD(0, 0, 0, x, y, z);
	}

	private final WBoxD modify(double x, double y, double z) {
		return this.modifyX(x).modifyY(y).modifyZ(z);
	}

	private final WBoxD modifyX(double value) {
		double min = this.minX;
		double max = this.maxX;
		this.minX -= value;
		this.maxX += value;
		if (this.minX > this.maxX) {
			double v = (min + max) / 2;
			this.minX = v;
			if (min + max % 2 == 0) {
				this.maxX = v;
			} else {
				this.maxX = v + 1;
			}
		}
		return this;
	}

	private final WBoxD modifyY(double value) {
		double min = this.minY;
		double max = this.maxY;
		this.minY -= value;
		this.maxY += value;
		if (this.minY > this.maxY) {
			double v = (min + max) / 2;
			this.minY = v;
			if (min + max % 2 == 0) {
				this.maxY = v;
			} else {
				this.maxY = v + 1;
			}
		}
		return this;
	}

	private final WBoxD modifyZ(double value) {
		double min = this.minZ;
		double max = this.maxZ;
		this.minZ -= value;
		this.maxZ += value;
		if (this.minZ > this.maxZ) {
			double v = (min + max) / 2;
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
