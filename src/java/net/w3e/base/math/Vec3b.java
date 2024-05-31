package net.w3e.base.math;

import java.util.function.BiFunction;

import net.w3e.base.LogicUtil;

public class Vec3b {

	public static final Vec3b ZERO = new Vec3b(false, false, false);

	public static final Vec3b X = new Vec3b(true, false, false);
	public static final Vec3b Y = new Vec3b(false, true, false);
	public static final Vec3b Z = new Vec3b(false, false, true);

	public static final Vec3b XY = new Vec3b(true, true, false);
	public static final Vec3b XZ = new Vec3b(true, false, true);
	public static final Vec3b YZ = new Vec3b(false, true, true);

	public static final Vec3b XYZ = new Vec3b(true, true, true);

	public final boolean x;
	public final boolean y;
	public final boolean z;

	public Vec3b(boolean x, boolean y, boolean z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3b(Vec3b vec3b) {
		this.x = vec3b.x;
		this.y = vec3b.x;
		this.z = vec3b.x;
	}

	public boolean any() {
		return this.x || this.y || this.z;
	}

	public final Vec3b and(Vec3b vec3b) {
		return and(vec3b.x, vec3b.y, vec3b.z);
	}

	public final Vec3b and(Boolean x, Boolean y, Boolean z) {
		return logic(x, y, z, LogicUtil::and);
	}

	public final Vec3b nand(Vec3b vec3b) {
		return nand(vec3b.x, vec3b.y, vec3b.z);
	}

	public final Vec3b nand(Boolean x, Boolean y, Boolean z) {
		return logic(x, y, z, LogicUtil::nand);
	}

	public final Vec3b or(Vec3b vec3b) {
		return or(vec3b.x, vec3b.y, vec3b.z);
	}

	public final Vec3b or(Boolean x, Boolean y, Boolean z) {
		return logic(x, y, z, LogicUtil::or);
	}

	public final Vec3b nor(Vec3b vec3b) {
		return or(vec3b.x, vec3b.y, vec3b.z);
	}

	public final Vec3b nor(Boolean x, Boolean y, Boolean z) {
		return logic(x, y, z, LogicUtil::nor);
	}

	public final Vec3b xor(Vec3b vec3b) {
		return xor(vec3b.x, vec3b.y, vec3b.z);
	}

	public final Vec3b xor(Boolean x, Boolean y, Boolean z) {
		return logic(x, y, z, LogicUtil::xor);
	}

	public final Vec3b xnor(Vec3b vec3b) {
		return xnor(vec3b.x, vec3b.y, vec3b.z);
	}

	public final Vec3b xnor(Boolean x, Boolean y, Boolean z) {
		return logic(x, y, z, LogicUtil::xnor);
	}

	public final Vec3b logic(Boolean x, Boolean y, Boolean z, BiFunction<Boolean, Boolean, Boolean> function) {
		boolean x1 = function.apply(this.x, x);
		boolean y1 = function.apply(this.y, y);
		boolean z1 = function.apply(this.z, z);

		return new Vec3b(x1, y1, z1);
	}

	public final Vec3b inverse() {
		return inverse(true, true, true);
	}

	public final Vec3b inverse(boolean x, boolean y, boolean z) {
		return new Vec3b(x ? !this.x : this.x, y ? !this.y : this.y, z ? !this.z : this.z);
	}

	public final boolean isFalse() {
		return !(this.x || this.y || this.z);
	}

	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Vec3b)) {
			return false;
		} else {
			Vec3b v = (Vec3b)o;
			return v.x == this.x && v.y == this.y && v.z == this.z;
		}
	}

	public final int hashCode() {
		return ((Boolean.hashCode(this.x) * 31) + Boolean.hashCode(this.y)) * 31 + Boolean.hashCode(this.z);
	}

	private final String toString(boolean value) {
		return value ? "1" : "0";
	}

	public final String toString() {
		return String.format("(%s%s%s)", this.toString(this.x), this.toString(this.y), this.toString(this.z));
	}
}