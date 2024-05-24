package net.api.assets;

public record AssetHolder<T>(boolean file, String key, int id, T value, Asset<T> asset) {
	
	public final String print() {
		return String.format("AssetHolder[type=%s, key=%s, id=%s]", this.file ? "file" : "jar", this.key, this.id);
	}
}
