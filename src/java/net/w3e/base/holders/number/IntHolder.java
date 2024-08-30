package net.w3e.base.holders.number;

public class IntHolder extends NumberHolder<Integer, IntHolder> {

	private int value;

	public IntHolder() {}

	public IntHolder(int value) {
		this.value = value;
	}

	public IntHolder(NumberHolder<?, ?> holder) {
		this(holder.getAsInt());
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

	@Override
	public final IntHolder add() {
		return this.add(1);
	}

	public final IntHolder add(int value) {
		this.value += value;
		return this;
	}

	@Override
	public final IntHolder remove() {
		return this.remove(-1);
	}

	public final IntHolder remove(int value) {
		this.value += value;
		return this;
	}

	@Override
	public IntHolder copy() {
		return new IntHolder(this);
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
