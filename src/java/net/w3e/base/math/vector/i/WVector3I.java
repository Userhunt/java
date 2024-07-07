package net.w3e.base.math.vector.i;

import net.w3e.base.math.vector.IWVector;

public class WVector3I extends IWVectorNI<WVector3I> {

	public static final WVector3I EMPTY = new WVector3I(0, 0, 0);

	private final int x, y, z;

	public WVector3I(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WVector3I(IWVector<?> vector) {
		this(vector.getXI(), vector.getYI(), vector.getZI());
	}

	@Override
	public final int getXI() {
		return this.x;
	}

	@Override
	public final int getYI() {
		return this.y;
	}

	@Override
	public final int getZI() {
		return this.z;
	}

	@Override
	protected final WVector3I create(int x, int y, int z) {
		return new WVector3I(x, y, z);
	}
}
