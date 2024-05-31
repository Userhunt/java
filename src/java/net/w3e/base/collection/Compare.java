package net.w3e.base.collection;

public class Compare implements Comparable<Compare> {

	public final byte compare;

	public Compare(byte compare) {
		this.compare = compare;
	}

	@Override
	public final int compareTo(Compare o) {
		return compare - o.compare;
	}
}
