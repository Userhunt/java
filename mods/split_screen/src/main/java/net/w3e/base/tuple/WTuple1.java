package net.w3e.base.tuple;

import java.util.Objects;

/**
 * 12.04.23
 */
public class WTuple1<T> {

	private T t;

	public WTuple1() { }

	public WTuple1(T t) {
		set(t);
	}

	public T get() {
		return t;
	}

	public void set(T t) {
		this.t = t;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (this == object) {
			return true;
		} else if (!(object instanceof WTuple1)) {
			return false;
		} else {
			WTuple1<?> target = ((WTuple1<?>)object);
			return Objects.equals(t, target.t);
		}
	}

	@Override
	public int hashCode() {
		return t == null ? 0 : t.hashCode();
	}

	public String toString() {
		return "{" + t + "} " + super.toString();
	}
}
