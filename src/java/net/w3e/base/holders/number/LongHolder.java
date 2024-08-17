package net.w3e.base.holders.number;

public class LongHolder extends NumberHolder<Long, LongHolder> {

	private long value;

	public LongHolder() {}

	public LongHolder(long value) {
		this.value = value;
	}

	public LongHolder(NumberHolder<?, ?> holder) {
		this(holder.getAsLong());
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

	@Override
	public final LongHolder add() {
		this.value += 1;
		return this.add(1);
	}

	public final LongHolder add(long value) {
		this.value += value;
		return this;
	}

	@Override
	public final LongHolder remove() {
		return this.remove(-1);
	}

	public final LongHolder remove(long value) {
		this.value += value;
		return this;
	}

	@Override
	public LongHolder copy() {
		return new LongHolder(this);
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
