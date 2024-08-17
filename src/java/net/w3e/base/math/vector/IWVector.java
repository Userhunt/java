package net.w3e.base.math.vector;

import net.w3e.base.math.vector.i.IWVectorNI;

public abstract class IWVector<T extends IWVector<T>> implements Comparable<T> {

	public abstract int getXI();
	public abstract int getYI();
	public abstract int getZI();
	
	public abstract double getXD();
	public abstract double getYD();
	public abstract double getZD();

	protected abstract T create(double x, double y, double z);
	
	public final T add(WDirection direction) {
		return this.add(direction.getRelative());
	}
	public final T add(IWVectorNI<?> vector) {
		return this.add(vector.getXI(), vector.getYI(), vector.getZI());
	}
	public final T add(IWVector<?> vector) {
		return this.add(vector.getXD(), vector.getYD(), vector.getZD());
	}
	public final T add(double x, double y, double z) {
		return this.create(this.getXD() + x, this.getYD() + y, this.getZD() + z);
	}

	public final T substract(WDirection direction) {
		return this.substract(direction.getRelative());
	}
	public final T substract(IWVectorNI<?> vector) {
		return this.substract(vector.getXI(), vector.getYI(), vector.getZI());
	}
	public final T substract(IWVector<?> vector) {
		return this.substract(vector.getXD(), vector.getYD(), vector.getZD());
	}
	public final T substract(double x, double y, double z) {
		return this.create(this.getXD() - x, this.getYD() - y, this.getZD() - z);
	}

	public final T inverse() {
		return this.scale(-1);
	}
	public final T scale(double i) {
		return this.scale(i, i, i);
	}
	public final T scale(double x, double y, double z) {
		return this.create(this.getXD() * x, this.getYD() * y, this.getZD() * z);
	}

	public final T withX(double i) {
		return this.create(i, this.getYD(), this.getZD());
	}

	public final T withY(double i) {
		return this.create(this.getXD(), i, this.getZD());
	}

	public final T withZ(double i) {
		return this.create(this.getXD(), this.getYD(), i);
	}

	public abstract String toStringArray();

	@Override
	public final boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof IWVector<?> vector)) {
			return false;
		}
		if (this.getXD() != vector.getXD()) {
			return false;
		}
		if (this.getYD() != vector.getYD()) {
			return false;
		}
		return this.getZD() == vector.getZD();
	}

	@Override
	public final int hashCode() {
		return (this.getYI() + this.getZI() * 31) * 31 + this.getXI();
	}
}
