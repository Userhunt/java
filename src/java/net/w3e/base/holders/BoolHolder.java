package net.w3e.base.holders;

public class BoolHolder extends AbstractObjectHolder<Boolean> {

	private boolean value;

	public BoolHolder() {}

	public BoolHolder(boolean value) {
		this.value = value;
	}

	public BoolHolder(BoolHolder value) {
		this(value.value);
	}

	@Override
	public final Boolean get() {
		return this.value;
	}

	@Override
	public final void set(Boolean value) {
		this.value = value;
	}

	public final boolean getBool() {
		return this.value;
	}

	public final void setBool(boolean value) {
		this.value = value;
	}

	public final void setTrue() {
		this.value = true;
	}

	public final void setFalse() {
		this.value = false;
	}

	@Override
	public BoolHolder copy() {
		return new BoolHolder(this);
	}
}
