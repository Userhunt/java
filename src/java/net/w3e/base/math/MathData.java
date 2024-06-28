package net.w3e.base.math;

import java.awt.Color;

import lombok.Getter;

public class MathData {
	private final String round;
	@Getter
	private double min = Double.MAX_VALUE;
	@Getter
	private double max = Double.MIN_VALUE;
	@Getter
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
			this.zero = abs;
			this.zeroValue = value;
		}
		return this;
	}

	public final Color toColor(double value) {
		return this.toColor(value, min, max);
	}

	public final Color toColor(double value, double min, double max) {
		int color = BMatUtil.round(BMatUtil.toRange(value, min, max) * 255);
		return new Color(color, color, color);
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
