package net.w3e.base.tuple.number;

import net.w3e.base.tuple.WPrimitiveTuple;

/**
 * 12.04.23
 */
public class WDoubleTuple extends WPrimitiveTuple<Double> {

	private double value;

	public WDoubleTuple() { }

	public WDoubleTuple(WDoubleTuple t) {
		set(t.value);
	}

	public WDoubleTuple(double value) {
		set(value);
	}

	public double get() {
		return this.value;
	}

	public void set(double value) {
		this.value = value;
	}

	public final void increase() {
		set(get() + 1);
	}

	public final void decrease() {
		set(get() - 1);
	}

	public final void add(double value) {
		set(get() + value);
	}

	public final void remove(double value) {
		set(get() - value);
	}

	@Override
	protected Double getValue() {
		return get();
	}
}
