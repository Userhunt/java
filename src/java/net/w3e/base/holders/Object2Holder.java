package net.w3e.base.holders;

public class Object2Holder<A, B> {

	private A a;
	private B b;

	public Object2Holder() {}

	public Object2Holder(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public final A getA() {
		return this.a;
	}

	public final void setA(A a) {
		this.a = a;
	}

	public final B getB() {
		return this.b;
	}

	public final void setB(B b) {
		this.b = b;
	}
}
