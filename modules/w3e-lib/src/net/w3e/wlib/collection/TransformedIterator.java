package net.w3e.wlib.collection;

import java.util.Iterator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class TransformedIterator<T, V> implements Iterator<T> {

	protected final Iterator<V> iterator;

	@Override
	public final boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public abstract T next();

	@Override
	public final void remove() {
		this.iterator.remove();
	}
}