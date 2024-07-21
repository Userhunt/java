package net.w3e.base.math;

import net.w3e.base.math.vector.IWVector;
import net.w3e.base.math.vector.d.WVector3D;

//https://ru.wikipedia.org/wiki/Кривая_Безье
public class BezierCurve {
	
	/**
	 * (1-t)P0 + tP1
	 * @param <T>
	 * @param t
	 * @param start
	 * @param end
	 * @return
	 */
	public static final <T extends IWVector<T>> WVector3D curve2(float t, T start, T end) {
		return new WVector3D((end.substract(start).scale(t)).add(start));
	}

	/**
	 * (1-t)^2*P0 + 2t(1 - t)P1 + t^2*P2
	 * @param <T>
	 * @param t
	 * @param start
	 * @param point1
	 * @param end
	 * @return
	 */
	public static final <T extends IWVector<T>> WVector3D curve3(float t, T start, T point1, T end) {
		float a = 1f - t;
		return new WVector3D(
			start.scale(a * a)
			.add(point1.scale(2 * t * a))
			.add(end.scale(t * t))
		);
	}

	/**
	 * (1-t)^3*P0 + 3t(1 - t)^2*P1 + 3t^2*(1 - t)*P2 + t^3*P3
	 * 
	 * @param <T>
	 * @param t
	 * @param start
	 * @param point1
	 * @param end
	 * @return
	 */
	public static final <T extends IWVector<T>> WVector3D curve4(float t, T start, T point1, T point2, T end) {
		float a = 1f - t;
		return new WVector3D(
			start.scale(a * a * a)
			.add(point1.scale(3 *  t * a * a))
			.add(point2.scale(3 * t * t * a))
			.add(end.scale(t * t * t))
		);
	}
}
