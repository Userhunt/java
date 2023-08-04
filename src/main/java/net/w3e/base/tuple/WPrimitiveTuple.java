package net.w3e.base.tuple;

public abstract class WPrimitiveTuple<T> {

	protected abstract T getValue();

	@Override
	public final int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public final boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (this == object) {
			return true;
		} else if (object instanceof WPrimitiveTuple<?> tuple) {
			Object self = getValue();
			Object other = tuple.getValue();
			return self == other || self.equals(other);
		} else {
			return false;
		}
	}

	public String toString() {
		return String.format("{value:%s,hash:%s}", getValue(), hash());
	}

	protected final int hash() {
		return super.hashCode();
	}
}
