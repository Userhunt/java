package net.w3e.base.dungeon.layers.interfaces;

public interface IDungeonLayerProgress<T extends Enum<T>> {
	int ordinal();
	T[] getValues();

	@SuppressWarnings("unchecked")
	default T next(float progress) {
		T self = (T)this;
		if (progress < 100) {
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

	default int progress(IDungeonLayerProgress<T> prevProgress, float i) {
		float partScale = 100f / this.getValues().length;
		return (int)Math.floor(prevProgress.ordinal() * partScale + i * partScale / 100 + 0.001f);
	}
}
