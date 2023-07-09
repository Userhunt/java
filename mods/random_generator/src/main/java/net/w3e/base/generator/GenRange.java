package net.w3e.base.generator;

import java.util.Arrays;
import java.util.function.DoubleSupplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.w3e.base.BStringUtil;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.math.BMatUtil;

public record GenRange(double min, double max, double... args) {

	public static final GenRange EMPTY = new GenRange(0, 0);
	public static final GenRange LEVEL_RANGE = levelRange(0);
	public static final GenRange EMPTY_CHANCE = chance(0, 50);
	public static final GenRange VALUE_1 = new ValueRangeBuilder(1).build();

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
		return new GenRange(Math.max(0, min), Math.max(0, max), step);
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

	public static final class ValueRangeBuilder {

		private double min;
		private double max;
		private double step;
		private double spread = 0;
		private double spreadLvl = 0;
		private double baseSpred = 0;
		private int round = 2;

		public ValueRangeBuilder() {
			this(0);
		}

		public ValueRangeBuilder(double value) {
			this(value, value, 0);
		}

		public ValueRangeBuilder(double min, double max) {
			this(min, max, (max - min) / 5);
		}

		public ValueRangeBuilder(double min, double max, double step) {
			this(min, max, step, step / 2);
		}

		public ValueRangeBuilder(double min, double max, double step, double spread) {
			this.min = min;
			this.max = max;
			this.step = step;
			this.spread = spread;
		}

		public final ValueRangeBuilder min(double min) {
			this.min = min;
			return this;
		}

		public final ValueRangeBuilder max(double max) {
			this.max = max;
			return this;
		}

		public final ValueRangeBuilder step(double step) {
			this.step = step;
			return this;
		}

		public final ValueRangeBuilder spread(double spread) {
			this.spread = spread;
			return this;
		}

		public final ValueRangeBuilder spreadLvl(double spreadLvl) {
			this.spreadLvl = spreadLvl;
			return this;
		}

		public final ValueRangeBuilder baseSpread(double baseSpread) {
			this.baseSpred = baseSpread;
			return this;
		}

		public final ValueRangeBuilder round(int round) {
			if (round >= 0) {
				this.round = round;
			}
			return this;
		}

		public ValueRangeBuilder read(GenRange range) {
			this.min = range.min;
			this.max = range.max;
			this.step = range.arg(1, this.step);
			this.spread = range.arg(2, this.spread);
			this.spreadLvl = range.arg(3, this.spreadLvl);
			this.baseSpred = range.arg(4, this.baseSpred);
			this.round = range.argInt(5, this.round);
			return this;
		} 

		public final GenRange build() {
			return new GenRange(this.min, this.max, this.step, this.spread, this.spreadLvl, this.baseSpred, this.round);
		}
	}

	public static final double clamp(double value, double min, double max) {
		return BMatUtil.clamp(value, Math.min(min, max), Math.max(min, max));
	}

	@Override
	public final double[] args() {
		return Arrays.copyOf(args, args.length);
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

	public final ValueData valueStepData(double lvl, double minLvl, DoubleSupplier nextDouble) {
		double[] result = new double[4];
		result[0] = this.valueStep(lvl, minLvl);

		double spread = this.arg(2, -1);
		double spreadLvl = this.arg(3, 0);
		result[1] = this.arg(4, 0);
		if (spread > 0 || spreadLvl != 0) {
			spread = Math.max(spread, 0);
			spreadLvl *= lvl;
			if (spread != -spreadLvl) {
				spread += spreadLvl;
				result[2] = spread;
				result[3] = (nextDouble.getAsDouble() - 0.5) * 2;
			}
		}

		return new ValueData(result);
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

	public JsonElement save() {
		if (this.args.length == 0) {
			if (this.min == this.max) {
				return new JsonPrimitive(this.min);
			} else {
				JsonArray array = new JsonArray();
				array.add(this.min);
				if (this.max != Double.MAX_VALUE) {
					array.add(this.max);
				}
				return array;
			}
		} else {
			JsonObject json = new JsonObject();
			if (this.min != 0.0) {
				json.addProperty("min", this.min);
			}
			if (this.max != Double.MAX_VALUE) {
				json.addProperty("max", this.max);
			}
			JsonArray array = new JsonArray();
			for (double arg : this.args) {
				array.add(arg);
			}
			json.add("args", array);
			return json;
		}
	}

	public static GenRange read(JsonElement json, GenRange def) {
		if (json == null) {
			return def;
		} else if (json instanceof JsonPrimitive) {
			double d = json.getAsDouble();
			return new GenRange(d, d);
		} else if (json instanceof JsonArray array) {
			double min = 0;
			double max = Double.MAX_VALUE;
			int size = array.size();
			if (size >= 1) {
				min = array.get(0).getAsDouble();
			}
			if (size >= 2) {
				max = array.get(1).getAsDouble();
			}
			return new GenRange(min, max);
		} else if (json instanceof JsonObject object) {
			double min = 0;
			if (object.has("min")) {
				min = object.get("min").getAsDouble();
			}
			double max = Double.MAX_VALUE;
			if (object.has("max")) {
				max = object.get("max").getAsDouble();
			}
			double[] args = new double[0];
			if (object.has("args")) {
				JsonArray argsObject = object.getAsJsonArray("args");
				args = BJsonUtil.readDoubleArray(argsObject, argsObject.size());
			}
			return new GenRange(min, max, args);
		} else {
			return def;
		}
	}

	public final boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (object == this) {
			return true;
		} else if (!(object instanceof GenRange range)) {
			return false;
		} else {
			return this.min == range.min && this.max == range.max && Arrays.equals(this.args, range.args);
		}
	}

	@Override
	public final String toString() {
		return String.format("{min:%s,max:%s,args:%s}", this.min, this.max, BStringUtil.toString(this.args));
	}

	public class ValueData {

		private final double base;
		private final double spread;
		private final double baseSpread;
		private final double random;
		private final int arg;
		private double scale = 1;

		private ValueData(double[] data) {
			this.base = data[0];
			this.baseSpread = data[1];
			this.spread = data[2];
			this.random = data[3];
			this.arg = GenRange.this.argInt(5, 2);
		}

		public final double value(boolean clampRandom) {
			double value = this.base;
			double d = this.baseSpread + this.random * this.spread;
			if (clampRandom) {
				value += d;
			}
			value = clamp(value * scale, GenRange.this.min, GenRange.this.max);
			if (!clampRandom) {
				value += d * scale;
			}
			return BMatUtil.round(value, arg);
		}

		protected final void applyScale(double scale) {
			this.scale *= scale;
		}

		@Override
		public final String toString() {
			double v1 = this.value(true);
			double v2 = this.value(false);
			String value = v1 == v2 ? String.valueOf(v1) : String.format("[%s,%s]", v1, v2);
			return String.format("{base:%s,offset:%s,spread:%s,random:%s,scale:%s,value:%s}", this.base, this.baseSpread, BMatUtil.round(this.spread, this.arg), BMatUtil.round(this.random, this.arg), this.scale, value);
		}
	}

}
