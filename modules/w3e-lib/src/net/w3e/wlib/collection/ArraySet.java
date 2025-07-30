package net.w3e.wlib.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class ArraySet<E> extends ArrayList<E> implements Set<E> {

	public ArraySet(int initialCapacity) {
		super(initialCapacity);
	}

	public ArraySet() {
		super();
	}

	public ArraySet(Collection<? extends E> c) {
		super(c);
	}

	protected final void place(E e) {
		super.add(e);
	}

	protected final boolean test(E e) {
		return e == null || contains(e);
	}

	@Override
	public boolean add(E e) {
		if (test(e)) {
			return false;
		}
		return super.add(e);
	}

	@Override
	public final void add(int index, E element) {
		if (test(element)) {
			return;
		}
		super.add(index, element);
	}

	@Override
	public final boolean addAll(Collection<? extends E> c) {
		List<E> collect = new ArraySet<>();
		for (E e : c) {
			if (test(e)) {
				continue;
			}
			collect.add(e);
		}
		return super.addAll(collect);
	}

	@Override
	public final boolean addAll(int index, Collection<? extends E> c) {
		List<E> collect = new ArraySet<>();
		for (E e : c) {
			if (test(e)) {
				continue;
			}
			collect.add(e);
		}
		return super.addAll(index, collect);
	}

	@Override
	public final E set(int index, E element) {
		if (test(element)) {
			return null;
		}
		return super.set(index, element);
	}

	@Override
	public final void replaceAll(UnaryOperator<E> operator) {
		List<E> list = new ArrayList<>(this);
		list.replaceAll(operator);
		clear();
		addAll(list);
	}

	public final void forEach(BiConsumer<Integer, E> action) {
		ArraySet<E> copy = new ArraySet<>(this);
		int size = copy.size();
		for (int i = 0; i < size; i++) {
			action.accept(i, copy.get(i));
		}
	}

	public final void forEach(TriConsumer<ArraySet<E>, Integer, E> action) {
		ArraySet<E> copy = new ArraySet<>(this);
		int size = copy.size();
		for (int i = 0; i < size; i++) {
			action.accept(this, i, copy.get(i));
		}
	}

	@Override
	public final int indexOf(Object o) {
		if (o == null) {
			return -1;
		} else {
			return index(o);
		}
	}

	public int index(Object o) {
		return super.indexOf(o);
	}

	@Override
	public final int lastIndexOf(Object o) {
		if (o == null) {
			return -1;
		} else {
			return lastIndex(o);
		}
	}

	public int lastIndex(Object o) {
		return super.indexOf(o);
	}

	@Override
	public final List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	public final void shuffle() {
		shuffle(new Random());
	}

	public final void shuffle(Random random) {
		List<E> list = new ArrayList<>(this);
		Collections.shuffle(list, random);
		this.clear();
		for (E e : list) {
			this.place(e);
		}
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public final void sort(Comparator<? super E> c) {
		Object[] a = this.toArray();
		Arrays.sort(a, (Comparator) c);
		this.clear();
		for (Object object : a) {
			this.place((E)object);
		}
	}

	public static class ArraySetStrict<E> extends ArraySet<E> {

		public ArraySetStrict(int initialCapacity) {
			super(initialCapacity);
		}

		public ArraySetStrict() {
			super();
		}

		public ArraySetStrict(Collection<? extends E> c) {
			super(c);
		}

		public int index(Object o) {
			int size = size();
			for (int i = 0; i < size; i++) {
				if (get(i) == o) {
					return i;
				}
			}
			return -1;
		}

		public int lastIndex(Object o) {
			int size = size();
			for (int i = size - 1; i >= 0; i--) {
				if (get(i) == o) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public boolean remove(Object o) {
			if (o == null) {
				return super.remove(o);
			}
			int i = 0;
			for (E e : this) {
				if (e == o) {
					remove(i);
					return true;
				}
				i++;
			}
			return false;
		}
	}

	/**
	 * An operation that accepts three input arguments and returns no result.
	 *
	 * @param <K> type of the first argument
	 * @param <V> type of the second argument
	 * @param <S> type of the third argument
	 * @since 2.7
	 */
	public interface TriConsumer<K, V, S> {

		/**
		 * Performs the operation given the specified arguments.
		 * @param k the first input argument
		 * @param v the second input argument
		 * @param s the third input argument
		 */
		void accept(K k, V v, S s);
	}
}
