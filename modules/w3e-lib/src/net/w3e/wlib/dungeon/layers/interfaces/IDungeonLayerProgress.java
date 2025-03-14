package net.w3e.wlib.dungeon.layers.interfaces;

public interface IDungeonLayerProgress<T extends Enum<T>> {
	int ordinal();
	T[] getValues();

	@SuppressWarnings("unchecked")
	default T next(float progress) {
		T self = (T)this;
		if (progress < 1) {
			return self;
		}
		int ordinal = self.ordinal();
		T[] values = this.getValues();
		if (values.length > ordinal + 1) {
			return values[ordinal + 1];
		} else {
			return self;
		}
	}

	default float progress(IDungeonLayerProgress<T> prevProgress, float i) {
		float partScale = 1f / this.getValues().length;
		return prevProgress.ordinal() * partScale + i * partScale / 1f;
	}
}
