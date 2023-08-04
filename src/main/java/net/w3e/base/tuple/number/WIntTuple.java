package net.w3e.base.tuple.number;

import net.w3e.base.tuple.WPrimitiveTuple;

public class WIntTuple extends WPrimitiveTuple<Integer> {

	private int value;

	public WIntTuple() { }

	public WIntTuple(WIntTuple t) {
		set(t.value);
	}

	public WIntTuple(int value) {
		set(value);
	}

	public final int get() {
		return this.value;
	}

	public final void set(int value) {
		this.value = value;
	}

	public final void increase() {
		set(get() + 1);
	}

	public final void decrease() {
		set(get() - 1);
	}

	public final void add(int value) {
		set(get() + value);
	}

	public final void remove(int value) {
		set(get() - value);
	}

	@Override
	protected Integer getValue() {
		return get();
	}
}
