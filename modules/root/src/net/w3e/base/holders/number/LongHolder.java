package net.w3e.base.holders.number;

public class LongHolder extends NumberHolder<Long> {

	private long value;

	public LongHolder() {}

	public LongHolder(long value) {
		this.value = value;
	}

	@Override
	public final Long get() {
		return this.value;
	}

	@Override
	public final void set(Long value) {
		this.value = value;
	}

	public final void setValue(long value) {
		this.value = value;
	}

	public final void add() {
		this.value += 1;
	}

	public final void add(long value) {
		this.value += value;
	}

	public final void remove() {
		this.value -= 1;
	}

	public final void remove(long value) {
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
		return (int)this.value;
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
