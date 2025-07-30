package net.w3e.wlib.collection.identity;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import net.w3e.wlib.collection.TransformedIterator;

public class IdentityLinkedHashMap<K, T> extends AbstractMap<K,T> {
	private final IdentitySet set = new IdentitySet();

	public IdentityLinkedHashMap() {}

	public IdentityLinkedHashMap(Map<? extends K, ? extends T> m) {
		putAll(m);
	}

	@Override
	public final Set<Entry<K, T>> entrySet() {
		return set;
	}

	@Override
	public final T put(K k, T t) {
		return set.innerMap.put(new IdentityWrapper<>(k), t);
	}

	@Override
	public final boolean containsKey(Object arg0) {
		return set.innerMap.containsKey(new IdentityWrapper<>(arg0));
	}

	@Override
	public final T remove(Object arg0) {
		return set.innerMap.remove(new IdentityWrapper<>(arg0));
	}

	@Override
	public final T get(Object arg0) {
		return set.innerMap.get(new IdentityWrapper<>(arg0));
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new IdentityLinkedHashMap<>(this);
	}

	private class IdentitySet extends AbstractSet<Entry<K,T>> {
		private final Map<IdentityWrapper<K>, T> innerMap = new LinkedHashMap<>();

		@Override
		public Iterator<Entry<K, T>> iterator() {
			return new IdentityIterator();
		}

		@Override
		public boolean add(Entry<K, T> entry) {
			IdentityWrapper<K> wrap = new IdentityWrapper<>(entry.getKey());
			if (innerMap.containsKey(wrap)) {
				return false;
			} else {
				innerMap.put(wrap, entry.getValue());
				return true;
			}
		}

		@Override
		public int size() {
			return innerMap.size();
		}

		@Override
		public boolean contains(Object arg0) {
			return innerMap.containsKey(new IdentityWrapper<>(arg0));
		}
	}

	private class IdentityIterator extends TransformedIterator<Map.Entry<K, T>, Entry<IdentityWrapper<K>, T>> {

		private IdentityIterator() {
			super(set.innerMap.entrySet().iterator());
		}

		@Override
		public Entry<K, T> next() {
			return new IdentityEntry(this.iterator.next());
		}

		@AllArgsConstructor
		private class IdentityEntry implements Entry<K, T> {
			private final Entry<IdentityWrapper<K>, T> entry;

			@Override
			public K getKey() {
				return entry.getKey().get();
			}

			@Override
			public T getValue() {
				return entry.getValue();
			}

			@Override
			public T setValue(T value) {
				return entry.setValue(value);
			}
		}
	}
}
