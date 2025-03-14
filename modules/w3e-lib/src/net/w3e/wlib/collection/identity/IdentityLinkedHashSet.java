package net.w3e.wlib.collection.identity;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.w3e.wlib.collection.TransformedIterator;

public class IdentityLinkedHashSet<K> extends AbstractSet<K> {
	private final Set<IdentityWrapper<K>> set = new LinkedHashSet<>();

	@Override
	public Iterator<K> iterator() {
		return new IdentityIterator();
	}

	@Override
	public boolean add(K entry) {
		IdentityWrapper<K> wrap = new IdentityWrapper<>(entry);
		return this.set.add(wrap);
	}

	@Override
	public int size() {
		return this.set.size();
	}

	@Override
	public boolean contains(Object arg0) {
		return this.set.contains(new IdentityWrapper<>(arg0));
	}

	private class IdentityIterator extends TransformedIterator<K, IdentityWrapper<K>> {

		private IdentityIterator() {
			super(set.iterator());
		}

		@Override
		public K next() {
			return this.iterator.next().get();
		}
	}
}
