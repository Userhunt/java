package net.w3e.wlib.mat;

public class WIntegerRectangle {
	public Integer x1;
	public Integer x2;
	public Integer y1;
	public Integer y2;

	@Override
	public String toString() {
		return String.format("{x:[%s,%s],y:[%s,%s]}", x1, x2, y1, y2);
	}
}
