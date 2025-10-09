package net.w3e.wlib.dungeon.direction;

public enum ConnectionState {
	HARD,
	SOFT,
	BOTH,
	NONE
	;
	public boolean isHard() {
		return this == BOTH || this == HARD;
	}
	public boolean isSoft() {
		return this == BOTH || this == SOFT;
	}
	public boolean isBoth() {
		return this == BOTH;
	}
	public boolean isAny() {
		return this == BOTH || this == HARD || this == SOFT;
	}
	public boolean isNone() {
		return this == NONE;
	}

	public ConnectionState add(ConnectionState state) {
		if (this == state) {
			return this == NONE ? null : this;
		}
		if (this == NONE) {
			return state;
		}
		if (state == NONE) {
			return this;
		}
		return BOTH;
	}

	public ConnectionState remove(ConnectionState state) {
		if (this == state || this == NONE || state == BOTH) {
			return null;
		}
		if (state == NONE) {
			return this;
		}
		if (this == BOTH) {
			if (state == HARD) {
				return SOFT;
			} else if (state == SOFT) {
				return HARD;
			}
		}
		return this;
	}

}
