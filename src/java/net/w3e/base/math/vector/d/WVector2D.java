package net.w3e.base.math.vector.d;

import net.w3e.base.math.vector.IWVector;

public class WVector2D extends IWVectorND<WVector2D> {

	public static final WVector2D EMPTY = new WVector2D(0, 0);

	private final double x, z;

	public WVector2D(double x, double z) {
		this.x = x;
		this.z = z;
	}

	public WVector2D(IWVector<?> vector) {
		this(vector.getXD(), vector.getZD());
	}

	@Override
	public final double getXD() {
		return this.x;
	}

	@Override
	public final double getYD() {
		return 0;
	}

	@Override
	public final double getZD() {
		return this.z;
	}

	@Override
	protected final WVector2D create(double x, double y, double z) {
		return new WVector2D(x, z);
	}
}
