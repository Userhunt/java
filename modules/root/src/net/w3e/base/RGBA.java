package net.w3e.base;

import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class RGBA {

	public static final RGBA.RGBAPack bgra = new RGBA.RGBAPack(RGBA.RGBAFlag.B, RGBA.RGBAFlag.G, RGBA.RGBAFlag.R, RGBA.RGBAFlag.A);
	public static final RGBA.RGBAPack bgr = new RGBA.RGBAPack(RGBA.RGBAFlag.B, RGBA.RGBAFlag.G, RGBA.RGBAFlag.R);
	public static final RGBA.RGBAPack rgba = new RGBA.RGBAPack(RGBA.RGBAFlag.R, RGBA.RGBAFlag.G, RGBA.RGBAFlag.B, RGBA.RGBAFlag.A);

	public static final RGBA WHITE = new RGBA(255, 255, 255);
	public static final RGBA LIGHT_GRAY = new RGBA(192, 192, 192);
	public static final RGBA GRAY = new RGBA(128, 128, 128);
	public static final RGBA DARK_GRAY = new RGBA(64, 64, 64);
	public static final RGBA BLACK = new RGBA(0, 0, 0);
	public static final RGBA RED = new RGBA(255, 0, 0);
	public static final RGBA PINK = new RGBA(255, 175, 175);
	public static final RGBA ORANGE = new RGBA(255, 200, 0);
	public static final RGBA YELLOW = new RGBA(255, 255, 0);
	public static final RGBA GREEN = new RGBA(0, 255, 0);
	public static final RGBA MAGENTA = new RGBA(255, 0, 255);
	public static final RGBA CYAN = new RGBA(0, 255, 255);
	public static final RGBA BLUE = new RGBA(0, 0, 255);
	public static final RGBA LIME = new RGBA(182, 255, 0);
	public static final RGBA DARK_RED = new RGBA(127, 0, 0);

	public final double r;
	public final double g;
	public final double b;
	public final double a;
	private RGBACache cache;

	public RGBA(int r, int g, int b) {
		this(calc(r), calc(g), calc(b), 1);
	}

	public RGBA(int r, int g, int b, int a) {
		this(calc(r), calc(g), calc(b), calc(a));
	}

	public RGBA(int r, int g, int b, float a) {
		this(calc(r), calc(g), calc(b), a);
	}

	public RGBA(int r, int g, int b, double a) {
		this(calc(r), calc(g), calc(b), a);
	}

	public RGBA(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public RGBA(double r, double g, double b, double a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public static final double calc(int v) {
		return ((double)v)/255d;
	}

	public static final int calc(double v) {
		return (int)(v*255 + 0.5);
	}

	public final int rInt() {
		return calc(r);
	}

	public final int gInt() {
		return calc(g);
	}

	public final int bInt() {
		return calc(b);
	}

	public final int aInt() {
		return calc(a);
	}

	public final float r() {
		return (float)r;
	}

	public final float g() {
		return (float)g;
	}

	public final float b() {
		return (float)b;
	}

	public final float a() {
		return (float)a;
	}

	public final RGBA r(int r) {
		return new RGBA(calc(r), g, b, a);
	}

	public final RGBA g(int g) {
		return new RGBA(r, calc(g), b, a);
	}

	public final RGBA b(int b) {
		return new RGBA(r, g, calc(b), a);
	}

	public final RGBA a(int a) {
		return new RGBA(r, g, b, calc(a));
	}

	public final RGBA r(float r) {
		return new RGBA(r, g, b, a);
	}

	public final RGBA g(float g) {
		return new RGBA(r, g, b, a);
	}

	public final RGBA b(float b) {
		return new RGBA(r, g, b, a);
	}

	public final RGBA a(float a) {
		return new RGBA(r, g, b, a);
	}

	public final RGBA r(double r) {
		return new RGBA(r, g, b, a);
	}

	public final RGBA g(double g) {
		return new RGBA(r, g, b, a);
	}

	public final RGBA b(double b) {
		return new RGBA(r, g, b, a);
	}

	public final RGBA a(double a) {
		return new RGBA(r, g, b, a);
	}

	/**
	 * gui
	 * @return
	 */
	public final int packBGRA() {
		return RGBA.bgra.pack(this);
	}

	/**
	 * text
	 * @return
	 */
	public final int packBGR() {
		return RGBA.bgr.pack(this);
	}

	/**
	 * Model
	 * @return
	 */
	public final int packRGBA() {
		return RGBA.rgba.pack(this);
	}

	public static final RGBA unpackRGBA(int rgba) {
		return RGBA.rgba.unpack(rgba);
	}

	private record RGBACache(RGBAPack type, int value) {}

	public static class RGBAPack {

		private final RGBAFlag[] flags;

		public RGBAPack(RGBAFlag... flags) {
			int size = flags.length;
			if (size >= 4) {
				size = 4;
			}
			this.flags = new RGBAFlag[size];
			for (int i = 0; i < size; i++) {
				this.flags[i] = flags[i];
			}
		}

		public final int pack(RGBA color) {
			if (color == null || this.flags.length == 0) {
				return 0;
			}
			if (color.cache != null && color.cache.type.equals(this)) {
				return color.cache.value;
			}
			if (flags.length == 0) {
				return 0;
			}
			int[] array = new int[flags.length];
			for (int i = 0; i < flags.length; i++) {
				array[i] = flags[i].pack(color);
			}

			int i = 0;
			int out = 0;
			for (int c : array) {
				out = out | (c << i);
				i += 8;
			}

			color.cache = new RGBACache(this, out);

			return out;
		}

		public final RGBA unpack(int color) {
			int[] colors = new int[]{0, 0, 0, 255};
			int i = 0;
			for (RGBAFlag flag : this.flags) {
				colors[flag.index] = flag.unpack(i, color);
				i++;
			}
			return new RGBA(colors[0], colors[1], colors[2], colors[3]);
		}

		@Override
		public final boolean equals(Object object) {
			if (object == null) {
				return false;
			} else if (object == this) {
				return true;
			} else if (!(object instanceof RGBAPack pack)) {
				return false;
			} else {
				return Arrays.equals(this.flags, pack.flags);
			}
		}
	}

	public static enum RGBAFlag {
		R((byte)0, RGBA::rInt, rgba -> rgba.r),
		G((byte)1, RGBA::gInt, rgba -> rgba.g),
		B((byte)2, RGBA::bInt, rgba -> rgba.b),
		A((byte)3, RGBA::aInt, rgba -> rgba.a);

		@FunctionalInterface
		public static interface RGBAFlagGetterInt {
			int get(RGBA rgba);
		}

		@FunctionalInterface
		public static interface RGBAFlagGetterDouble {
			double get(RGBA rgba);
		}

		public final byte index;
		public final RGBAFlagGetterInt getterInt;
		public final RGBAFlagGetterDouble getterDouble;

		private RGBAFlag(byte index, RGBAFlagGetterInt getterInt, RGBAFlagGetterDouble getterDouble) {
			this.index = index;
			this.getterInt = getterInt;
			this.getterDouble = getterDouble;
		}

		public final int pack(RGBA rgba) {
			return this.getterInt.get(rgba);
		}

		public final int unpack(int rgba) {
			return unpack(this.index, rgba);
		}

		public final int unpack(int index, int rgba) {
			return (rgba >> (index * 8)) & 255;
		}
	}

	public final int pack(RGBAFlag... flags) {
		return new RGBAPack(flags).pack(this);
	}

	public final String packHEX() {
		return "#" + Integer.toHexString(packBGRA());
	}

	public static final RGBA unpackHEX(String hex) {
		if (hex.startsWith("#") && !hex.isEmpty()) {
			hex = hex.substring(1);
		}
		if (hex.length() == 6) {
			hex = "ff" + hex;
		}
		return RGBA.bgra.unpack((int)Long.parseLong(hex.toLowerCase(), 16));
	}

	public final RGBA multiplyAlpha(int a) {
		return new RGBA(r, g, b, this.a * calc(a));
	}

	public final RGBA multiplyTotal(RGBA... color) {
		int colors[] = new int[color.length];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = color[i].packRGBA();
		}

		return multiplyTotal(colors);
	}

	public final RGBA multiplyTotal(int... color) {
		int a = RGBA.rgba.pack(this);
		for (int rgba : color) {
			a = FastColor.ARGB32.multiply(a, rgba);
		}

		return RGBA.rgba.unpack(a);
	}

	public static final RGBA fromJson(JsonElement json, boolean rgba) {
		if (json.isJsonObject()) {
			return fromJson((JsonObject)json);
		} else {
			if (json instanceof JsonPrimitive primitive && primitive.isString()) {
				String color = primitive.getAsString();
				if (color.startsWith("#")) {
					try {
						return unpackHEX(color);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			try {
				return rgba ? RGBA.rgba.unpack(json.getAsInt()) : RGBA.bgra.unpack(json.getAsInt());
			} catch (Exception e) {
				return fromJson(JsonParser.parseString(json.getAsString()), rgba);
			}
		}
	}

	public static final RGBA fromJson(JsonObject json) {
		int r = fromJson(json.get("r"), 0);
		int g = fromJson(json.get("g"), 0);
		int b = fromJson(json.get("b"), 0);
		int a = fromJson(json.get("a"), 255);
		return new RGBA(r, g, b, a);
	}

	private static final int fromJson(JsonElement json, int i) {
		try {
			return json.getAsInt();
		} catch (Exception e) {
			return i;
		}
	}

	@Override
	public final int hashCode() {
		return packBGRA();
	}

	@Override
	public final boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (this == object) {
			return true;
		} else if (!(object instanceof RGBA)) {
			return false;
		} else {
			return packBGRA() == ((RGBA)object).packBGRA();
		}
	}

	@Override
	public final String toString() {
		return String.format("{\"r\":%s,\"g\":%s,\"b\":%s,\"a\":%s}", rInt(), gInt(), bInt(), aInt());
	}

	public static String[] hexToString(RGBA color) {
		return hexToString(color.packHEX());
	}

	public static String[] hexToString(String hex) {
		hex = hex.substring(1);
		String r = "00";
		String g = "00";
		String b = "00";
		String a = "00";
		int length = hex.length();
		if (length % 2 == 1) {
			hex = "0" + hex;
		}
		if (length == 2) {
			b = hex;
		} else if (length == 4) {
			b = hex.substring(2, 4);
			g = hex.substring(0, 2);
		} else if (length == 6) {
			b = hex.substring(4, 6);
			g = hex.substring(2, 4);
			r = hex.substring(0, 2);
		} else {
			b = hex.substring(6, 8);
			g = hex.substring(4, 6);
			r = hex.substring(2, 4);
			a = hex.substring(0, 2);
		}
		return new String[]{r, g, b, a};
	}

	/**
	 * @see net.minecraft.util.FastColor
	 */
	private class FastColor {
		public static class ARGB32 {
			public static int alpha(int p_13656_) {
				return p_13656_ >>> 24;
			}

			public static int red(int p_13666_) {
				return p_13666_ >> 16 & 255;
			}

			public static int green(int p_13668_) {
				return p_13668_ >> 8 & 255;
			}

			public static int blue(int p_13670_) {
				return p_13670_ & 255;
			}

			public static int color(int p_13661_, int p_13662_, int p_13663_, int p_13664_) {
				return p_13661_ << 24 | p_13662_ << 16 | p_13663_ << 8 | p_13664_;
			}

			public static int multiply(int p_13658_, int p_13659_) {
				return color(alpha(p_13658_) * alpha(p_13659_) / 255, red(p_13658_) * red(p_13659_) / 255, green(p_13658_) * green(p_13659_) / 255, blue(p_13658_) * blue(p_13659_) / 255);
			}
		}
	}
}
