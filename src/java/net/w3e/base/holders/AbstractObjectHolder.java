package net.w3e.base.holders;

import java.util.Objects;

public abstract class AbstractObjectHolder<T> {

	public abstract T get();

	public abstract void set(T t);

	public abstract AbstractObjectHolder<T> copy();

	@Override
	public final boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (this == object) {
			return true;
		} else if (!(object instanceof AbstractObjectHolder)) {
			return false;
		} else {
			AbstractObjectHolder<?> target = ((AbstractObjectHolder<?>)object);
			return Objects.equals(this.get(), target.get());
		}
	}

	@Override
	public final int hashCode() {
		T t;
		return (t = get()) == null ? 0 : t.hashCode();
	}

	public final String toString() {
		return String.format("{class:\"%s\",hash:%s,value:[%s]" + "} ", this.getClass().getSimpleName(), Integer.toHexString(this.hashCode()), this.get());
	}
}
