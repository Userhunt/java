package net.w3e.base.tuple;

import java.util.Objects;

public class WTuple2<A, B> {

	public A a;
	public B b;

	public WTuple2() {}

	public WTuple2(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (this == object) {
			return true;
		} else if (!(object instanceof WTuple2)) {
			return false;
		} else {
			WTuple2<?, ?> target = ((WTuple2<?, ?>)object);
			return Objects.equals(a, target.a) && Objects.equals(b, target.b);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(a.hashCode(), b.hashCode());
	}

	public String toString() {
		return String.format("{class:%s,hash:%s,value:[%s,%s%s]", getClass().getSimpleName(), hashCode(), a, b, insString());
	}

	protected String insString() {
		return "";
	}
}
