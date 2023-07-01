package net.w3e.base.tuple;

/**
 * 12.04.23
 */
public abstract class WPrimitiveTuple<T> {

	protected abstract T getValue();

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public boolean equals(Object object) {
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
