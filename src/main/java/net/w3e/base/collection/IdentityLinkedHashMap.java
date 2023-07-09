package net.w3e.base.collection;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.Iterators;

public class IdentityLinkedHashMap<K, T> extends AbstractMap<K,T> {
	private static final Equivalence<Object> equivalence = Equivalence.identity();
	private final IdentityLinkedHashSet set = new IdentityLinkedHashSet();

	public IdentityLinkedHashMap() {}

	public IdentityLinkedHashMap(Map<? extends K, ? extends T> m) {
		putAll(m);
		for (Entry<? extends K, ? extends T> entry : m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Set<Entry<K, T>> entrySet() {
		return set;
	}

	@Override
	public T put(K k, T t) {
		return set.innerMap.put(equivalence.wrap(k), t);
	}

	@Override
	public boolean containsKey(Object arg0) {
		return set.contains(arg0);
	}

	@Override
	public T remove(Object arg0) {
		return set.innerMap.remove(equivalence.wrap(arg0));
	}

	@Override
	public T get(Object arg0) {
		return set.innerMap.get(equivalence.wrap(arg0));
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new IdentityLinkedHashMap<>(this);
	}

	public class MyEntry implements Entry<K, T> {
		private final Entry<Equivalence.Wrapper<K>, T> entry;
		public MyEntry(Entry<Wrapper<K>, T> entry) {this.entry = entry;}

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

	public class IdentityLinkedHashSet extends AbstractSet<Entry<K,T>> {
		private final Map<Wrapper<K>, T> innerMap = new LinkedHashMap<>();

		@Override
		public Iterator<Entry<K, T>> iterator() {
			return Iterators.transform(innerMap.entrySet().iterator(), entry -> new MyEntry(entry));
		}

		@Override
		public boolean add(Entry<K, T> entry) {
			Wrapper<K> wrap = equivalence.wrap(entry.getKey());
			innerMap.put(wrap, entry.getValue());
			return true;
		}

		@Override
		public int size() {
			return innerMap.size();
		}

		@Override
		public boolean contains(Object arg0) {
			return innerMap.containsKey(equivalence.wrap(arg0));
		}
	}
}
