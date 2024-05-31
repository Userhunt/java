package net.w3e.base;

public class PackUtil {

	public static final String toString(byte b) {
		return b + ": " + String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(b))).replaceAll(" ", "0");
	}

	public static final String toString(short s) {
		return s + ": " + String.format("%16s", Integer.toBinaryString(Short.toUnsignedInt(s))).replaceAll(" ", "0");
	}

	public static final String toString(int i) {
		return i + ": " + String.format("%32s", Integer.toBinaryString(i)).replaceAll(" ", "0");
	}

	public static final String toString(long l) {
		return l + ": " + String.format("%64s", Long.toBinaryString(l)).replaceAll(" ", "0");
	}

	public static final boolean test(byte num, int i) {
		int mask = 1 << (--i);
		return (num & mask) != 0;
	}

	public static final boolean test(short num, int i) {
		int mask = 1 << (--i);
		return (num & mask) != 0;
	}

	public static final boolean test(int num, int i) {
		int mask = 1 << (--i);
		return (num & mask) != 0;
	}

	public static final boolean test(long num, int i) {
		long mask = 1 << (--i);
		return (num & mask) != 0;
	}

	public static final byte set(byte num, int i, boolean value) {
		i--;
		return value ? (num |= 1 << i) : (num &= ~(1 << i));
	}

	public static final short set(short num, int i, boolean value) {
		i--;
		return value ? (num |= 1 << i) : (num &= ~(1 << i));
	}

	public static final int set(int num, int i, boolean value) {
		i--;
		return value ? (num |= 1 << i) : (num &= ~(1 << i));
	}

	public static final long set(long num, int i, boolean value) {
		i--;
		return value ? (num |= 1 << i) : (num &= ~(1 << i));
	}

	public static final long pack(int i, int j) {
		return (((long)i) << 32) | (j & 0xffffffffL);
	}

	public static final int[] unpack(long l) {
		return new int[]{(int)(l >> 32), (int)l};
	}

	public static class PackByteFlag {
		private final int pos;

		public final byte enable;

		public PackByteFlag(int pos) {
			this.pos = pos;
			this.enable = set((byte)0);
		}

		public final boolean is(byte flags) {
			return PackUtil.test(flags, this.pos);
		}

		public final byte set(byte flags) {
			return set(flags, true);
		}

		private byte set(byte flags, boolean mode) {
			return PackUtil.set(flags, this.pos, mode);
		}
	}
}
