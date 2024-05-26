package net.w3e.base.math;

public class MathData {
	private final String round;
	private double min = Double.MAX_VALUE;
	private double max = Double.MIN_VALUE;
	private double zero = Double.MAX_VALUE;
	private double zeroValue = Double.MAX_VALUE;

	public MathData() {
		this(3);
	}

	public MathData(int round) {
		this.round = "%." + round + "f";
	}

	public final MathData calculate(double value) {
		this.min = Math.min(this.min, value);
		this.max = Math.max(this.max, value);
		double abs = Math.abs(value);
		if (this.zero > abs) {
			this.zero = value;
			this.zeroValue = value;
		}
		return this;
	}

	public final String generateString() {
		return String.format("{min:%s,max:%s,zero:%s,zeroValue:%s}",  
			String.format(this.round, this.min),
			String.format(this.round, this.max),
			String.format(this.round, this.zero),
			String.format(this.round, this.zeroValue)
		);
	}
}
