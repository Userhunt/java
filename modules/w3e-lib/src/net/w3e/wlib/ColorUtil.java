package net.w3e.wlib;

import java.awt.Color;
import java.util.Arrays;

public class ColorUtil {

	public static final ColorPack BGRA = new ColorPack(ColorPackFlag.B, ColorPackFlag.G, ColorPackFlag.R, ColorPackFlag.A);
	public static final ColorPack BGR = new ColorPack(ColorPackFlag.B, ColorPackFlag.G, ColorPackFlag.R);
	public static final ColorPack RGBA = new ColorPack(ColorPackFlag.R, ColorPackFlag.G, ColorPackFlag.B, ColorPackFlag.A);

	/**
	 * Minecraft gui
	 */
	public static int packBGRA(Color color) {
		return BGRA.pack(color);
	}

	/**
	 * Minecraft text
	 */
	public static int packBGR(Color color) {
		return BGR.pack(color);
	}

	/**
	 * Minecraft Model
	 */
	public static int packRGBA(Color color) {
		return RGBA.pack(color);
	}

	public static Color unpackHEX(String hex) {
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		}

		if (hex.length() == 6) {
			hex = "ff" + hex;
		}

		return BGRA.unpack((int)Long.parseLong(hex.toLowerCase(), 16));
	}

	public static String packHEX(Color color) {
		return "#" + Integer.toHexString(BGRA.pack(color));
	 }

	public static String[] hexToString(Color color) {
		return hexToString(packHEX(color));
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
  

	public static double toDouble(int v) {
		return ((double)v)/255d;
	}

	public static int toInt(double v) {
		return (int)(v*255 + 0.5);
	}

	public static class ColorPack {

		private final ColorPackFlag[] flags;

		public ColorPack(ColorPackFlag... flags) {
			int size = flags.length;
			if (size >= 4) {
				size = 4;
			}
			this.flags = new ColorPackFlag[size];
			System.arraycopy(flags, 0, this.flags, 0, size);
		}

		public final int pack(Color color) {
			if (color == null || this.flags.length == 0) {
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

			return out;
		}

		public final Color unpack(int color) {
			int[] colors = new int[]{0, 0, 0, 255};
			int i = 0;
			for (ColorPackFlag flag : this.flags) {
				colors[flag.index] = flag.unpack(i, color);
				i++;
			}
			return new Color(colors[0], colors[1], colors[2], colors[3]);
		}

		@Override
		public final boolean equals(Object object) {
			if (object == null) {
				return false;
			} else if (object == this) {
				return true;
			} else if (!(object instanceof ColorPack pack)) {
				return false;
			} else {
				return Arrays.equals(this.flags, pack.flags);
			}
		}
	}

	public static enum ColorPackFlag {
		R((byte)0, color -> color.getRed()),
		G((byte)1, color -> color.getGreen()),
		B((byte)2, color -> color.getBlue()),
		A((byte)3, color -> color.getAlpha());

		@FunctionalInterface
		public static interface ColorPackFlagGetter {
			int get(Color color);
		}

		public final byte index;
		private final ColorPackFlagGetter getter;

		private ColorPackFlag(byte index, ColorPackFlagGetter getter) {
			this.index = index;
			this.getter = getter;
		}

		public final int pack(Color color) {
			return this.getter.get(color);
		}

		public final int getInt(Color color) {
			return pack(color);
		}

		public final double getDouble(Color color) {
			return toDouble(pack(color));
		}

		public final int unpack(int rgba) {
			return unpack(this.index, rgba);
		}

		public final int unpack(int index, int rgba) {
			return (rgba >> (index * 8)) & 255;
		}
	}

}
