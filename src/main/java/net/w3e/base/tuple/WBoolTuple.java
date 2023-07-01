package net.w3e.base.tuple;

/**
 * 12.04.23
 */
public class WBoolTuple extends WPrimitiveTuple<Boolean> {

	private boolean value = false;

	public WBoolTuple() { }

	public WBoolTuple(WBoolTuple t) {
		set(t.value);
	}

	public WBoolTuple(boolean value) {
		set(value);
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}

	@Override
	protected Boolean getValue() {
		return value;
	}
}