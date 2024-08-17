package net.w3e.base.holders;

public class ObjectHolder<T> extends AbstractObjectHolder<T> {

	private T t;

	public ObjectHolder() {}

	public ObjectHolder(T t) {
		this.t = t;
	}

	public ObjectHolder(AbstractObjectHolder<T> holder) {
		this(holder.get());
	}

	@Override
	public final T get() {
		return t;
	}

	@Override
	public final void set(T t) {
		this.t = t;
	}

	@Override
	public final ObjectHolder<T> copy() {
		return new ObjectHolder<>(this);
	}
}
