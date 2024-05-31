package net.w3e.base.math.vector;

public class WVector3 extends IWVector<WVector3> {

	public static final WVector3 EMPTY = new WVector3(0, 0, 0);

	private final int x, y, z;

	public WVector3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public final int getX() {
		return this.x;
	}

	@Override
	public final int getY() {
		return this.y;
	}

	@Override
	public final int getZ() {
		return this.z;
	}

	@Override
	protected final IWVector<WVector3> create(int x, int y, int z) {
		return new WVector3(x, y, z);
	}
}
