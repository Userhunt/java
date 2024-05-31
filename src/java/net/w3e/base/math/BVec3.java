package net.w3e.base.math;

public abstract class BVec3<T extends BVec3<T>> {

	public static final BSVec3 XN = new BSVec3(-1.0F, 0.0F, 0.0F);
	public static final BSVec3 XP = new BSVec3(1.0F, 0.0F, 0.0F);
	public static final BSVec3 YN = new BSVec3(0.0F, -1.0F, 0.0F);
	public static final BSVec3 YP = new BSVec3(0.0F, 1.0F, 0.0F);
	public static final BSVec3 ZN = new BSVec3(0.0F, 0.0F, -1.0F);
	public static final BSVec3 ZP = new BSVec3(0.0F, 0.0F, 1.0F);

	public static final BSVec3 ZERO = new BSVec3(0.0f, 0.0f, 0.0f);
	public static final BSVec3 NORMAL = new BSVec3(1.0f, 1.0f, 1.0f);

	public final double x;
	public final double y;
	public final double z;

	public BVec3(double xIn, double yIn, double zIn) {
		this.x = xIn;
		this.y = yIn;
		this.z = zIn;
	}

	protected abstract T instance(double xIn, double yIn, double zIn);

	public final T round() {
		return this.instance(BMatUtil.round(x), BMatUtil.round(y), BMatUtil.round(z));
	}

	public final T lerp(BVec3<?> vectorIn, float pctIn) {
		float f = 1.0F - pctIn;
		double nx = this.x * f + vectorIn.x * pctIn;
		double ny = this.y * f + vectorIn.y * pctIn;
		double nz = this.z * f + vectorIn.z * pctIn;
		return this.instance(nx, ny, nz);
	}

	public final T subtractReverse(BVec3<?> vec) {
		return this.instance(vec.x - this.x, vec.y - this.y, vec.z - this.z);
	}

	public final T normalize() {
		return this.scale(1d / Math.sqrt(Math.fma(x, x, Math.fma(y, y, z * z))));
		//double d0 = (double) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		//return d0 < 1.0E-4D ? ZERO : new UVec3(this.x / d0, this.y / d0, this.z / d0);
	}

	public final double dotProduct(BVec3<?> vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}

	public final T crossProduct(BVec3<?> vec) {
		return this.instance(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
	}

	public final T subtract(BVec3<?> vec) {
		return this.subtract(vec.x, vec.y, vec.z);
	}

	public final T subtract(double x, double y, double z) {
		return this.add(-x, -y, -z);
	}

	public final T add(BVec3<?> vec) {
		return this.add(vec.x, vec.y, vec.z);
	}

	public final T add(double x, double y, double z) {
		return this.instance(this.x + x, this.y + y, this.z + z);
	}

	public final double distanceTo(BVec3<?> vec) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	public final double squareDistanceTo(BVec3<?> vec) {
		return this.squareDistanceTo(vec.x, vec.y, vec.z);
	}

	public final double squareDistanceTo(double xIn, double yIn, double zIn) {
		double d0 = xIn - this.x;
		double d1 = yIn - this.y;
		double d2 = zIn - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public final T scale(double factor) {
		return this.mul(factor, factor, factor);
	}

	public final T inverse() {
		return this.scale(-1.0D);
	}

	public final T mul(BVec3<?> vec) {
		return this.mul(vec.x, vec.y, vec.z);
	}

	public final T mul(double factorX, double factorY, double factorZ) {
		return this.instance(this.x * factorX, this.y * factorY, this.z * factorZ);
	}

	public final double length() {
		return (double) Math.sqrt(this.lengthSquared());
	}

	public final double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof BVec3)) {
			return false;
		} else {
			BVec3<?> Vec3d = (BVec3<?>) o;
			if (Double.compare(Vec3d.x, this.x) != 0) {
				return false;
			} else if (Double.compare(Vec3d.y, this.y) != 0) {
				return false;
			} else {
				return Double.compare(Vec3d.z, this.z) == 0;
			}
		}
	}

	public final int hashCode() {
		long j = Double.doubleToLongBits(this.x);
		int i = (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.y);
		i = 31 * i + (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.z);
		return 31 * i + (int) (j ^ j >>> 32);
	}

	public final String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public final T rotatePitch(float pitch) {
		double f = Math.cos(pitch);
		double f1 = Math.sin(pitch);
		double d0 = this.x;
		double d1 = this.y * f + this.z * f1;
		double d2 = this.z * f - this.y * f1;
		return this.instance(d0, d1, d2);
	}

	public final T rotateYaw(float yaw) {
		double f = Math.cos(yaw);
		double f1 = Math.sin(yaw);
		double d0 = this.x * f + this.z * f1;
		double d1 = this.y;
		double d2 = this.z * f - this.x * f1;
		return this.instance(d0, d1, d2);
	}

	public final T rotateRoll(float roll) {
		double f = Math.cos(roll);
		double f1 = Math.sin(roll);
		double d0 = this.x * f + this.y * f1;
		double d1 = this.y * f - this.x * f1;
		double d2 = this.z;
		return this.instance(d0, d1, d2);
	}

	public final float getX() {
		return (float)this.x;
	}

	public final float getY() {
		return (float)this.y;
	}

	public final float getZ() {
		return (float)this.z;
	}

	public final int getIntX() {
		return Math.round(this.getX());
	}

	public final int getIntY() {
		return Math.round(this.getY());
	}

	public final int getIntZ() {
		return Math.round(this.getZ());
	}

	public final double projOn(BVec3<?> a) {
		return this.projOnNormalized(a.normalize());
	}

	public final double projOnNormalized(BVec3<?> a) {
		return this.dotProduct(a);
	}

	public static final class BSVec3 extends BVec3<BSVec3> {

		public BSVec3(double xIn, double yIn, double zIn) {
			super(xIn, yIn, zIn);
		}
	
		@Override
		protected final BSVec3 instance(double xIn, double yIn, double zIn) {
			return new BSVec3(xIn, yIn, zIn);
		}
	}
}