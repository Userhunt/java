package net.w3e.base.holders.number;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

import net.w3e.base.collection.RandomCollection.FloatSupplier;
import net.w3e.base.holders.AbstractObjectHolder;

public abstract class NumberHolder<T extends Number, V extends NumberHolder<T, V>> extends AbstractObjectHolder<T> implements IntSupplier, LongSupplier, FloatSupplier, DoubleSupplier {

	public abstract V add();
	public abstract V remove();

	@Override
	public abstract V copy();

	public byte getAsByte() {
		return get().byteValue();
	}

	public short getAsShort() {
		return get().shortValue();
	}

	public int getAsInt() {
		return get().intValue();
	}

	public long getAsLong() {
		return get().longValue();
	}

	public float getAsFloat() {
		return get().floatValue();
	}

	public double getAsDouble() {
		return get().doubleValue();
	}
}
