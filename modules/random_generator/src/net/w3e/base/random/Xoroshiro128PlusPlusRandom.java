/*
* Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
*/
package net.w3e.base.random;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;

import net.w3e.base.math.BMatUtil;

/**
 * Xoroshiro128++ based pseudo random number generator.
 * 
 * @implNote The actual implementation can be found on {@link Xoroshiro128PlusPlusRandomImpl}.
 */
public class Xoroshiro128PlusPlusRandom extends IRandom {
	private Xoroshiro128PlusPlusRandomImpl implementation;
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public Xoroshiro128PlusPlusRandom(long seed) {
		this(new Xoroshiro128PlusPlusRandomImpl(XoroshiroSeed.createXoroshiroSeed(seed)));
	}

	public Xoroshiro128PlusPlusRandom(XoroshiroSeed seed) {
		this(new Xoroshiro128PlusPlusRandomImpl(seed));
	}

	public Xoroshiro128PlusPlusRandom(long seedLo, long seedHi) {
		this(new Xoroshiro128PlusPlusRandomImpl(seedLo, seedHi));
	}

	private Xoroshiro128PlusPlusRandom(Xoroshiro128PlusPlusRandomImpl implementation) {
		this.implementation = implementation;
	}

	@Override
	public final IRandom split() {
		return new Xoroshiro128PlusPlusRandom(this.implementation.next(), this.implementation.next());
	}

	@Override
	public final RandomSplitter nextSplitter() {
		return new Splitter(this.implementation.next(), this.implementation.next());
	}

	@Override
	public final void setSeed(long seed) {
		this.implementation = new Xoroshiro128PlusPlusRandomImpl(XoroshiroSeed.createXoroshiroSeed(seed));
		this.gaussianGenerator.reset();
	}

	@Override
	public final int nextInt() {
		return (int)this.implementation.next();
	}

	@Override
	public final int nextInt(int bound) {
		if (bound <= 0) {
			throw new IllegalArgumentException("Bound must be positive");
		}
		long l = Integer.toUnsignedLong(this.nextInt());
		long m = l * (long)bound;
		long n = m & 0xFFFFFFFFL;
		if (n < (long)bound) {
			int i = Integer.remainderUnsigned(~bound + 1, bound);
			while (n < (long)i) {
				l = Integer.toUnsignedLong(this.nextInt());
				m = l * (long)bound;
				n = m & 0xFFFFFFFFL;
			}
		}
		long o = m >> 32;
		return (int)o;
	}

	@Override
	public final long nextLong() {
		return this.implementation.next();
	}

	@Override
	public final boolean nextBoolean() {
		return (this.implementation.next() & 1L) != 0L;
	}

	@Override
	public final float nextFloat() {
		return (float)this.next(24) * FLOAT_MULTIPLIER;
	}

	@Override
	public final double nextDouble() {
		return (double)this.next(53) * DOUBLE_MULTIPLIER;
	}

	@Override
	public final double nextGaussian() {
		return this.gaussianGenerator.next();
	}

	@Override
	public final void skip(int count) {
		for (int i = 0; i < count; ++i) {
			this.implementation.next();
		}
	}

	/**
	 * {@return {@code bits} upper bits of random value}
	 * 
	 * @implNote In Xoroshiro128++, the lower bits have to be discarded in order
	 * to ensure proper randomness. For example, to obtain a double, the upper 53
	 * bits should be used instead of the lower 53 bits.
	 */
	private long next(int bits) {
		return this.implementation.next() >>> 64 - bits;
	}

	public static class Splitter extends RandomSplitter {
		private final long seedLo;
		private final long seedHi;

		public Splitter(long seedLo, long seedHi) {
			this.seedLo = seedLo;
			this.seedHi = seedHi;
		}

		@Override
		public IRandom split(int x, int y, int z) {
			long l = BMatUtil.hashCode(x, y, z);
			long m = l ^ this.seedLo;
			return new Xoroshiro128PlusPlusRandom(m, this.seedHi);
		}

		@Override
		public IRandom split(String seed) {
			XoroshiroSeed xoroshiroSeed = XoroshiroSeed.createXoroshiroSeed(seed);
			return new Xoroshiro128PlusPlusRandom(xoroshiroSeed.split(this.seedLo, this.seedHi));
		}
	}

	public static class Xoroshiro128PlusPlusRandomImpl {
		private long seedLo;
		private long seedHi;

		public Xoroshiro128PlusPlusRandomImpl(XoroshiroSeed seed) {
			this(seed.seedLo(), seed.seedHi());
		}

		public Xoroshiro128PlusPlusRandomImpl(long seedLo, long seedHi) {
			this.seedLo = seedLo;
			this.seedHi = seedHi;
			if ((this.seedLo | this.seedHi) == 0L) {
				this.seedLo = -7046029254386353131L;
				this.seedHi = 7640891576956012809L;
			}
		}

		public final long next() {
			long l = this.seedLo;
			long m = this.seedHi;
			long n = Long.rotateLeft(l + m, 17) + l;
			this.seedLo = Long.rotateLeft(l, 49) ^ (m ^= l) ^ m << 21;
			this.seedHi = Long.rotateLeft(m, 28);
			return n;
		}
	}

	public record XoroshiroSeed(long seedLo, long seedHi) {

		@SuppressWarnings("deprecation")
		private static final HashFunction MD5_HASH = Hashing.md5();

		public static final XoroshiroSeed createXoroshiroSeed(long seed) {
			return createUnmixedXoroshiroSeed(seed).mix();
		}

		public static final XoroshiroSeed createUnmixedXoroshiroSeed(long seed) {
			long l = seed ^ 0x6A09E667F3BCC909L;
			long m = l + -7046029254386353131L;
			return new XoroshiroSeed(l, m);
		}

		public static final XoroshiroSeed createXoroshiroSeed(String seed) {
			byte[] bs = MD5_HASH.hashString(seed, Charsets.UTF_8).asBytes();
			long l = Longs.fromBytes(bs[0], bs[1], bs[2], bs[3], bs[4], bs[5], bs[6], bs[7]);
			long m = Longs.fromBytes(bs[8], bs[9], bs[10], bs[11], bs[12], bs[13], bs[14], bs[15]);
			return new XoroshiroSeed(l, m);
		}

        public final XoroshiroSeed split(long seedLo, long seedHi) {
            return new XoroshiroSeed(this.seedLo ^ seedLo, this.seedHi ^ seedHi);
        }

        public final XoroshiroSeed split(XoroshiroSeed seed) {
            return this.split(seed.seedLo, seed.seedHi);
        }

        public final XoroshiroSeed mix() {
            return new XoroshiroSeed(mixStafford13(this.seedLo), mixStafford13(this.seedHi));
        }

		public static long mixStafford13(long seed) {
			seed = (seed ^ seed >>> 30) * -4658895280553007687L;
			seed = (seed ^ seed >>> 27) * -7723592293110705685L;
			return seed ^ seed >>> 31;
		}
    }
}

