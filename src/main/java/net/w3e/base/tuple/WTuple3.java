package net.w3e.base.tuple;

import java.util.Objects;

/**
 * 12.04.23
 */
public class WTuple3<A, B, C> extends WTuple2<A, B> {

	public C c;

	public WTuple3() { }

	public WTuple3(A a, B b, C c) {
		super(a, b);
		this.c = c;
	}

	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			WTuple3<?, ?, ?> target = ((WTuple3<?, ?, ?>)object);
			return Objects.equals(c, target.c);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), c.hashCode());
	}

	@Override
	protected String insString() {
		return String.format(",%s", c);
	}
}
