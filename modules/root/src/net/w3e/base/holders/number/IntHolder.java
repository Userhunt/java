package net.w3e.base.holders.number;

public class IntHolder extends NumberHolder<Integer> {

	private int value;

	public IntHolder() {}

	public IntHolder(int value) {
		this.value = value;
	}

	@Override
	public final Integer get() {
		return this.value;
	}

	@Override
	public final void set(Integer value) {
		this.value = value;
	}

	public final void setValue(int value) {
		this.value = value;
	}

	public final void add() {
		this.value += 1;
	}

	public final void add(int value) {
		this.value += value;
	}

	public final void remove() {
		this.value -= 1;
	}

	public final void remove(int value) {
		this.value += value;
	}

	@Override
	public final byte getAsByte() {
		return (byte)this.value;
	}

	@Override
	public final short getAsShort() {
		return (short)this.value;
	}

	@Override
	public final int getAsInt() {
		return this.value;
	}

	@Override
	public final long getAsLong() {
		return this.value;
	}

	@Override
	public final float getAsFloat() {
		return this.value;
	}

	@Override
	public final double getAsDouble() {
		return this.value;
	}
}
