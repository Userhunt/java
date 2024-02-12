package net.w3e.base.tuple.number;

import net.w3e.base.tuple.WPrimitiveTuple;

public class WLongTuple extends WPrimitiveTuple<Long> {

	private long value;

	public WLongTuple() { }

	public WLongTuple(WLongTuple t) {
		set(t.value);
	}

	public WLongTuple(long value) {
		set(value);
	}

	public long get() {
		return value;
	}

	public void set(long value) {
		this.value = value;
	}

	public final void increase() {
		set(get() + 1);
	}

	public final void decrease() {
		set(get() - 1);
	}

	public final void add(long value) {
		set(get() + value);
	}

	public final void remove(long value) {
		set(get() - value);
	}

	@Override
	protected Long getValue() {
		return get();
	}
}
