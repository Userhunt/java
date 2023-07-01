package net.w3e.base.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

import org.apache.logging.log4j.util.TriConsumer;

/**
 * 12.04.23
 */
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

	private final boolean test(E e) {
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
	public void add(int index, E element) {
		if (test(element)) {
			return;
		}
		super.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
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
	public boolean addAll(int index, Collection<? extends E> c) {
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
	public E set(int index, E element) {
		if (test(element)) {
			return null;
		}
		return super.set(index, element);
	}

	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		List<E> list = new ArrayList<>(this);
		list.replaceAll(operator);
		clear();
		addAll(list);
	}

	public void forEach(BiConsumer<Integer, E> action) {
		ArraySet<E> copy = new ArraySet<>(this);
		int size = copy.size();
		for (int i = 0; i < size; i++) {
			action.accept(i, copy.get(i));
		}
	}

	public void forEach(TriConsumer<ArraySet<E>, Integer, E> action) {
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
	@Deprecated
	public List<E> subList(int fromIndex, int toIndex) {
		return super.subList(fromIndex, toIndex);
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
}
