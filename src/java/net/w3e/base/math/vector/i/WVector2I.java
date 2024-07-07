package net.w3e.base.math.vector.i;

import net.w3e.base.math.vector.IWVector;

public class WVector2I extends IWVectorNI<WVector2I> {

	public static final WVector2I EMPTY = new WVector2I(0, 0);

	private final int x, z;

	public WVector2I(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public WVector2I(IWVector<?> vector) {
		this(vector.getXI(), vector.getZI());
	}

	@Override
	public final int getXI() {
		return this.x;
	}

	@Override
	public final int getYI() {
		return 0;
	}

	@Override
	public final int getZI() {
		return this.z;
	}

	@Override
	protected final WVector2I create(int x, int y, int z) {
		return new WVector2I(x, z);
	}
}
