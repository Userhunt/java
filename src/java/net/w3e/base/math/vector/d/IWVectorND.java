package net.w3e.base.math.vector.d;

import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.IWVector;

public abstract class IWVectorND<T extends IWVectorND<T>> extends IWVector<T> {

	@Override
	public final int getXI() {
		return BMatUtil.round(this.getXD());
	}

	@Override
	public final int getYI() {
		return BMatUtil.round(this.getYD());
	}

	@Override
	public final int getZI() {
		return BMatUtil.round(this.getZD());
	}

	@Override
	public final String toString() {
		return String.format("{class:\"%s\",hash:%s,x:%s,y:%s,z:%s}", this.getClass().getSimpleName(), this.hashCode(), this.getXD(), this.getYD(), this.getZD());
	}

	@Override
	public final String toStringArray() {
		return String.format("[%s,%s,%s]", this.getXD(), this.getYD(), this.getZD());
	}

	@Override
	public final int compareTo(T vector) {
		if (this.getYD() == vector.getYD()) {
			if (this.getXD() == vector.getXD()) {
				return BMatUtil.round((this.getZD() - vector.getZD()) * 10);
			}
			return BMatUtil.round((this.getXD() - vector.getXD()) * 10);
		}
		return BMatUtil.round((this.getYD() - vector.getYD()) * 10);
	}
}
