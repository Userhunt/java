package net.w3e.base.suppliers;

import java.util.function.Supplier;

public class ValueSupplier<T> implements Supplier<T> {

	private final T value;

	public ValueSupplier(T value) {
		this.value = value;
	}

	@Override
	public final T get() {
		return this.value;
	}
}
