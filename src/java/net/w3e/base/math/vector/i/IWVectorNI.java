package net.w3e.base.math.vector.i;

import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.vector.IWVector;

public abstract class IWVectorNI<T extends IWVectorNI<T>> extends IWVector<T> {

	@Override
	public final double getXD() {
		return this.getXI();
	}

	@Override
	public final double getYD() {
		return this.getYI();
	}

	@Override
	public final double getZD() {
		return this.getZI();
	}

	@Override
	protected final T create(double x, double y, double z) {
		return this.create(BMatUtil.round(x), BMatUtil.round(y), BMatUtil.round(z));
	}

	protected abstract T create(int x, int y, int z);

	public final WVector3I pos2Chunk() {
		return pos2Chunk(this);
	}

	public final WVector3I chunk2Pos() {
		return chunk2Pos(this);
	}

	public static final WVector3I pos2Chunk(IWVectorNI<?> pos) {
		if (pos == null) {
			return WVector3I.EMPTY;
		}
		return new WVector3I(pos.getXI() >> 4, pos.getYI() >> 4, pos.getZI() >> 4);
	}

	public static final WVector3I chunk2Pos(IWVectorNI<?> chunk) {
		if (chunk == null) {
			return WVector3I.EMPTY;
		}
		return new WVector3I(chunk.getXI() << 4, chunk.getYI() << 4, chunk.getZI() << 4);
	}

	@Override
	public final String toString() {
		return String.format("{class:\"%s\",hash:%s,x:%s,y:%s,z:%s}", this.getClass().getSimpleName(), this.hashCode(), this.getXI(), this.getYI(), this.getZI());
	}

	@Override
	public final String toStringArray() {
		return String.format("[%s,%s,%s]", this.getXI(), this.getYI(), this.getZI());
	}

	@Override
	public final int compareTo(T vector) {
		if (this.getYI() == vector.getYI()) {
			if (this.getXI() == vector.getXI()) {
				return this.getZI() - vector.getZI();
			}
			return this.getXI() - vector.getXI();
		}
		return this.getYI() - vector.getYI();
	}
}
