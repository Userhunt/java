package net.w3e.base.tuple.number;

import net.w3e.base.tuple.WPrimitiveTuple;

/**
 * 12.04.23
 */
public class WFloatTuple extends WPrimitiveTuple<Float> {

	private float value;

	public WFloatTuple() { }

	public WFloatTuple(WFloatTuple t) {
		set(t.value);
	}

	public WFloatTuple(float value) {
		set(value);
	}

	public float get() {
		return value;
	}

	public void set(float value) {
		this.value = value;
	}

	public final void increase() {
		set(get() + 1);
	}

	public final void decrease() {
		set(get() - 1);
	}

	public final void add(float value) {
		set(get() + value);
	}

	public final void remove(float value) {
		set(get() - value);
	}

	@Override
	protected Float getValue() {
		return get();
	}
}
