package net.w3e.base.math.vector;

public abstract class IWVector<T extends IWVector<T>> implements Comparable<IWVector<T>> {

	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();

	protected abstract IWVector<T> create(int x, int y, int z);

	public final T add(WDirection direction) {
		return this.add(direction.relative);
	}

	@SuppressWarnings("unchecked")
	public final T add(IWVector<?> vector) {
		return (T)this.create(this.getX() + vector.getX(), this.getY() + vector.getY(), this.getZ() + vector.getZ());
	}

	public final T inverse() {
		return this.scale(-1);
	}

	@SuppressWarnings("unchecked")
	public final T scale(int i) {
		return (T)this.create(this.getX() * i, this.getY() * i, this.getZ() * i);
	}

	@SuppressWarnings("unchecked")
	public final T withX(int i) {
		return (T)this.create(i, this.getY(), this.getZ());
	}

	@SuppressWarnings("unchecked")
	public final T withY(int i) {
		return (T)this.create(this.getX(), i, this.getZ());
	}

	@SuppressWarnings("unchecked")
	public final T withZ(int i) {
		return (T)this.create(this.getX(), this.getY(), i);
	}

	public final WVector3 toChunk() {
		return toChunk(this);
	}

	public final WVector3 toPos() {
		return toPos(this);
	}

	public static final WVector3 toChunk(IWVector<?> pos) {
		if (pos == null) {
			return WVector3.EMPTY;
		}
		return new WVector3(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
	}

	public static final WVector3 toPos(IWVector<?> chunk) {
		if (chunk == null) {
			return WVector3.EMPTY;
		}
		return new WVector3(chunk.getX() << 4, chunk.getY() << 4, chunk.getZ() << 4);
	}

	@Override
	public final boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof IWVector<?> vector)) {
			return false;
		}
		if (this.getX() != vector.getX()) {
			return false;
		}
		if (this.getY() != vector.getY()) {
			return false;
		}
		return this.getZ() == vector.getZ();
	}

	@Override
	public final int hashCode() {
		return (this.getY() + this.getZ() * 31) * 31 + this.getX();
	}

	@Override
	public final String toString() {
		return String.format("{class:\"%s\",hash:%s,x:%s,y:%s,z:%s}", this.getClass().getSimpleName(), this.hashCode(), this.getX(), this.getY(), this.getZ());
	}

	public final String toStringArray() {
		return String.format("[%s,%s,%s]", this.getX(), this.getY(), this.getZ());
	}

	@Override
	public final int compareTo(IWVector<T> vector) {
		if (this.getX() == vector.getY()) {
			if (this.getZ() == vector.getZ()) {
				return this.getX() - vector.getX();
			}
			return this.getZ() - vector.getZ();
		}
		return this.getY() - vector.getY();
	}
}
