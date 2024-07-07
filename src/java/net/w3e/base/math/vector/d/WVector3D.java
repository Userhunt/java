package net.w3e.base.math.vector.d;

import net.w3e.base.math.vector.IWVector;

public class WVector3D extends IWVectorND<WVector3D> {

	public static final WVector3D EMPTY = new WVector3D(0, 0, 0);

	private final double x, y, z;

	public WVector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WVector3D(IWVector<?> vector) {
		this(vector.getXD(), vector.getYD(), vector.getZD());
	}

	@Override
	public final double getXD() {
		return this.x;
	}

	@Override
	public final double getYD() {
		return this.y;
	}

	@Override
	public final double getZD() {
		return this.z;
	}

	@Override
	protected final WVector3D create(double x, double y, double z) {
		return new WVector3D(x, y, z);
	}
}
