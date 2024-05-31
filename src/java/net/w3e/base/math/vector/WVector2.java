package net.w3e.base.math.vector;

public class WVector2 extends IWVector<WVector2> {

	public static final WVector2 EMPTY = new WVector2(0, 0);

	private final int x, z;

	public WVector2(int x, int z) {
		this.x = x;
		this.z = z;
	}

	@Override
	public final int getX() {
		return this.x;
	}

	@Override
	public final int getY() {
		return 0;
	}

	@Override
	public final int getZ() {
		return this.z;
	}

	@Override
	protected final IWVector<WVector2> create(int x, int y, int z) {
		return new WVector2(x, z);
	}
}
