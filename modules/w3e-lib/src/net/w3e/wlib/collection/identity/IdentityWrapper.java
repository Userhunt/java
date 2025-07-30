package net.w3e.wlib.collection.identity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IdentityWrapper<K> {

	private final Object key;

	@SuppressWarnings("unchecked")
	public K get() {
		return (K)this.key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdentityWrapper wrapper) {
			return this.key == wrapper.get();
		}
		return this.key == obj;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this.key);
	}
}
