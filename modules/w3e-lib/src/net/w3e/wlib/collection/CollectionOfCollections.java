package net.w3e.wlib.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.w3e.wlib.collection.identity.IdentityLinkedHashMap;

public class CollectionOfCollections<E> implements Collection<E> {

	private final Collection<E> collection;
	private final CollectionOfCollections<E> next;

	protected CollectionOfCollections(Collection<E> set, CollectionOfCollections<E> next) {
		this.collection = set;
		this.next = next;
	}

	public final List<E> toList() {
		List<E> list = new ArrayList<>();
		for (E e : this) {
			list.add(e);
		}
		return list;
	}

	@Override
	public final int size() {
		return this.collection.size() + (this.next != null ? this.next.size() : 0);
	}

	@Override
	public final boolean isEmpty() {
		return this.collection.isEmpty() || (this.next != null ? this.next.isEmpty() : false);
	}

	@Override
	public final boolean contains(Object o) {
		return this.collection.contains(o) || (this.next != null ? this.next.contains(o) : false);
	}

	@Override
	public final Iterator<E> iterator() {
		return new CollectionOfCollectionsIterator<>(this);
	}

	@Override
	public final Object[] toArray() {
		return this.toList().toArray();
	}

	@Override
	public final <T> T[] toArray(T[] a) {
		return this.toList().toArray(a);
	}

	@Override
	public final <T> T[] toArray(IntFunction<T[]> generator) {
		return this.toList().toArray(generator);
	}

	@Override
	public final boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean containsAll(Collection<?> c) {
		return this.collection.containsAll(c) || (this.next != null ? this.next.containsAll(c) : false);
	}

	@Override
	public final boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void clear() {
		this.collection.clear();
		if (this.next != null) {
			this.next.clear();
		}
	}

	@Override
	public final boolean removeIf(Predicate<? super E> filter) {
		boolean remove = this.collection.removeIf(filter);
		if (this.next != null) {
			remove = this.next.removeIf(filter) || remove;
		}
		return remove;
	}

	@Override
	public final String toString() {
		return this.toList().toString();
	}

	private static class CollectionOfCollectionsIterator<E> implements Iterator<E> {

		private CollectionOfCollections<E> entry;
		private Iterator<E> iterator;

		public CollectionOfCollectionsIterator(CollectionOfCollections<E> entry) {
			this.entry = entry;
		}

		@Override
		public final boolean hasNext() {
			if (this.iterator == null) {
				if (this.entry == null) {
					return false;
				} else {
					this.iterator = entry.collection.iterator();
				}
			}
			if (!this.iterator.hasNext()) {
				this.iterator = null;
				if (this.entry == null || this.entry.next == null) {
					return false;
				} else {
					while(this.entry.next != null) {
						this.entry = this.entry.next;
						if (!this.entry.collection.isEmpty()) {
							break;
						}
					}
					return this.hasNext();
				}
			}
			return true;
		}

		@Override
		public final E next() {
			if (this.iterator == null) {
				throw new NoSuchElementException();
			} else {
				return this.iterator.next();
			}
		}
	}

	@SafeVarargs
	public static final <T> Collection<T> newCollection(Collection<T>... collections) {
		return newCollection(CollectionOfCollections::new, () -> Collections.emptyList(), collections);
	}

	@SafeVarargs
	@SuppressWarnings("unchecked")
	private static final <T, V extends Collection<T>> V newCollection(BiFunction<V, CollectionOfCollections<T>, CollectionOfCollections<T>> factory, Supplier<V> empty, V... collections) {
		Set<V> list = Collections.newSetFromMap(new IdentityLinkedHashMap<>());
		List<V> keys = new ArrayList<>(Arrays.asList(collections));
		Collections.reverse(keys);
		list.addAll(keys);

		Iterator<V> iterator = list.iterator();
		CollectionOfCollections<T> first = null;
		while(iterator.hasNext()) {
			first = factory.apply(iterator.next(), first);
		}
		if (first == null) {
			return empty.get();
		} else {
			return (V)first;
		}
	}

	@SafeVarargs
	public static final <T> Set<T> newSet(Set<T>... collections) {
		return newCollection(CollectionsOfSet::new, () -> Set.of(), collections);
	}

	@SafeVarargs
	public static final <T> List<T> newList(List<T>... collections) {
		return newCollection(CollectionsOfList::new, () -> List.of(), collections);
	}

	private static class CollectionsOfSet<E> extends CollectionOfCollections<E> implements Set<E> {

		protected CollectionsOfSet(Collection<E> set, CollectionOfCollections<E> next) {
			super(set, next);
		}
	}

	private static class CollectionsOfList<E> extends CollectionOfCollections<E> implements List<E> {

		protected CollectionsOfList(Collection<E> set, CollectionOfCollections<E> next) {
			super(set, next);
		}

		@Override
		public final boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final E get(int index) {
			return toList().get(index);
		}

		@Override
		public final E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final void add(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final E remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final int indexOf(Object o) {
			return this.toList().indexOf(o);
		}

		@Override
		public final int lastIndexOf(Object o) {
			return this.toList().lastIndexOf(o);
		}

		@Override
		public final ListIterator<E> listIterator() {
			throw new UnsupportedOperationException();
		}

		@Override
		public final ListIterator<E> listIterator(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public final List<E> subList(int fromIndex, int toIndex) {
			throw new UnsupportedOperationException();
		}
	}
}
