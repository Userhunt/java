package net.w3e.base.generator;

import java.util.function.DoubleSupplier;

import net.w3e.base.Fix;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.math.BMatUtil;

public record GenRange(double min, double max, double... args) {

	public static final GenRange EMPTY = new GenRange(0, 0);
	public static final GenRange LEVEL_RANGE = new GenRange(0, Double.MAX_VALUE);

	public static final GenRange levelRange(double min) {
		return levelRange(min, Double.MAX_VALUE);
	}

	public static final GenRange levelRange(double min, double max) {
		return new GenRange(Math.max(0, min), max);
	}

	public static final GenRange weight(double value) {
		return weight(value, value, 0);
	}

	public static final GenRange weight(double min, double max) {
		return weight(min, max, (max - min) / 5);
	}

	public static final GenRange weight(double min, double max, double step) {
		return new GenRange(Math.max(0, min), max, step);
	}

	public static final GenRange chance(double value) {
		return chance(value, value, 0);
	}

	public static final GenRange chance(double min, double max) {
		return chance(min, max, (max - min) / 5);
	}

	public static final GenRange chance(double min, double max, double step) {
		min = BMatUtil.clamp(min, 0, 100);
		max = BMatUtil.clamp(max, 0, 100);
		return new GenRange(min, max, step);
	}

	public static final GenRange valueRange(double value) {
		return valueRange(value, value, 0);
	}

	public static final GenRange valueRange(double min, double max) {
		return valueRange(min, max, (max - min) / 5);
	}

	public static final GenRange valueRange(double min, double max, double step) {
		return valueRange(min, max, step, step / 2);
	}

	public static final GenRange valueRange(double min, double max, double step, double spread) {
		return valueRange(min, max, step, spread, 0);
	}

	public static final GenRange valueRange(double min, double max, double step, double spread, double spreadLvl) {
		return new GenRange(min, max, step, spread, spreadLvl, 2);
	}

	public static final GenRange valueRange(double min, double max, double step, double spread, double spreadLvl, int round) {
		return new GenRange(min, max, step, spread, spreadLvl, round);
	}

	public static final double clamp(double value, double min, double max) {
		return BMatUtil.clamp(value, Math.min(min, max), Math.max(min, max));
	}


	public final double range() {
		return this.max - this.min;
	}

	public final int steps() {
		double arg = arg(1, 1);
		if (arg == 0) {
			return 0;
		}
		double value = this.range() / arg;
		int round = BMatUtil.round(value);
		if (value > round) {
			round += 1;
		}
		return round;
	}

	public final double valueStep(double lvl) {
		return this.valueStep(lvl, 0);
	}

	public final double valueStep(double lvl, double minLvl) {
		double value = this.min + this.arg(1, 1) * (lvl - minLvl);
		return clamp(value, this.min, this.max);
	}

	public final double[] valueStepData(double lvl, double minLvl, DoubleSupplier nextDouble) {
		double[] result = new double[2];
		result[0] = this.valueStep(lvl, minLvl);

		double spread = this.arg(2, -1);
		double spreadLvl = this.arg(3, -1);
		if (spread > 0 || spreadLvl != 0) {
			spread = Math.max(spread, 0);
			spreadLvl *= lvl;
			if (spread != -spreadLvl) {
				double delta = nextDouble.getAsDouble() * (spread + spreadLvl);
				delta = Fix.round(delta, this.argInt(4, 2));					
				result[1] = delta;
			}
		}

		return result;
	}

	public final double random(DoubleSupplier random) {
		return random.getAsDouble() * this.range() + this.min();
	}


	public final double arg(int i, double def) {
		if (this.args.length >= i) {
			return this.args[i - 1];
		} else {
			return def;
		}
	}

	public final int argInt(int i, int def) {
		if (this.args.length >= i) {
			return BMatUtil.round(this.args[i - 1]);
		} else {
			return def;
		}
	}

	public final boolean isIn(double value) {
		return value >= this.min && value <= this.max;
	}

	@Override
	public final String toString() {
		return String.format("{min:%s,max:%s,args:%s}", this.min, this.max, BJsonUtil.toString(this.args));
	}
}
