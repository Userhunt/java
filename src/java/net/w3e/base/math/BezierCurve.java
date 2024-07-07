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
	 * (1-t)^2*P0 + 2t(t-1)P1 + t^2*P2
	 * @param <T>
	 * @param t
	 * @param start
	 * @param point1
	 * @param end
	 * @return
	 */
	public static final <T extends IWVector<T>> WVector3D curve3(float t, T start, T point1, T end) {
		float a = 1f - t;
		a *= a;
		return new WVector3D(
			start.scale(a)
			.add(point1.scale(2 * t * (t - 1)))
			.add(end.scale(t * t))
		);
	}

	/**
	 * (1-t)^3*P0 + 3t(t-1)^2*P1 + 3t^2*(t-1)*P2 + t^3*P3
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
		a *= a * a;
		float b = t - 1;
		b *= b;
		return new WVector3D(
			start.scale(a)
			.add(point1.scale(3 *  t * b))
			.add(point2.scale(3 * t * t * (t - 1)))
			.add(end.scale(t * t * t))
		);
	}
}
