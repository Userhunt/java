package net.w3e.base;

import java.util.function.Supplier;

public class RegisterSupplier<T> implements Supplier<T> {

	private final Supplier<T> sup;
	private T value = null;

	public RegisterSupplier(Supplier<T> sup) {
		this.sup = sup;
	}

	@Override
	public T get() {
		if (this.value == null) {
			this.value = sup.get();
		}
		return this.value;
	}
}
