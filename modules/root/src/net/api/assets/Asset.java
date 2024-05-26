package net.api.assets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import net.w3e.base.jar.JarUtil;

public abstract class Asset<T> {

	public final AssetCollector source;
	private final List<AssetHolder<T>> values = new ArrayList<>();
	private boolean loaded;

	public Asset(AssetCollector source) {
		this.source = source;
	}

	public final void load() {
		if (this.loaded) {
			return;
		}
		if (this.source.loader().load(this)) {
			this.loaded = true;
			new AssetJarLoader();
			new AssetFileLoader();
		}
	}

	private class AssetJarLoader {
		private AssetJarLoader() {
			Path path = Asset.this.source.getJarPath();
			if (path != null) {
				loadSingle(0, path);
				if (!Asset.this.source.singleFile()) {
					Path root = JarUtil.getDebugFolder(path.toString());
					int length = 0;
					if (root != null) {
						length = root.toString().length() + 1;
					}
					for (Path p : JarUtil.getJarFolder(path.toString())) {
						loadSingle(length, p);
					}
				}
			}
		}

		private final void loadSingle(int length, Path file) {
			String path = file.toString().substring(length);
			if (Asset.this.testJar(file, FilenameUtils.getExtension(path))) {
				try {
					Asset.this.loadJar(path, file.toString());
				} catch (Exception e) {
					AssetLoader.LOGGER.error(String.format("file=%s, root=%s", file, Asset.this.source.getJarPath()));
					e.printStackTrace();
				}
			}
		}
	}

	private class AssetFileLoader {

		private AssetFileLoader() {
			File file = Asset.this.source.getRunPath();

			if (file != null && file.exists()) {
				int length = file.getAbsolutePath().length();
				if (Asset.this.source.singleFile() == file.isFile()) {
					if (Asset.this.source.singleFile()) {
						loadSingle(length - Asset.this.source.filePath().length(), file);
					} else {
						loadSingle(length, file);
						loadTree(length + 1, file);
					}
				}
			}
		}

		private final void loadSingle(int length, File file) {
			String path = file.getAbsolutePath();
			if (Asset.this.testFile(file, FilenameUtils.getExtension(path))) {
				try {
					Asset.this.loadFile(path.substring(length), file);
				} catch (Exception e) {
					AssetLoader.LOGGER.error(String.format("file=%s, root=%s", file, Asset.this.source.getRunPath()));
					e.printStackTrace();
				}
			}
		}

		private final void loadTree(int length, File file) {
			for (File f : file.listFiles()) {
				if (f.isFile()) {
					loadSingle(length, f);
				} else {
					loadTree(length, f);
				}
			}
		}
	}

	protected abstract boolean testExtension(String extension);

	protected boolean testFile(File file, String extension) {
		return this.testExtension(extension);
	}

	protected abstract void loadFile(String key, File file) throws IOException;

	protected boolean testJar(Path file, String extension) {
		return this.testExtension(extension);
	}

	public abstract void loadJar(String key, String file) throws IOException;

	protected final InputStream getJarInputStream(String file) {
		return JarUtil.getResourceAsStream(file);
	}

	public final void unload() {
		if (this.loaded || this.source.loader().unload(this)) {
			this.loaded = false;
			Iterator<AssetHolder<T>> iterator = this.values.iterator();
			while(iterator.hasNext()) {
				this.unload(iterator.next());
				iterator.remove();
			}
		}
	}

	protected abstract void unload(AssetHolder<T> assetHolder);

	public final void reload() {
		this.unload();
		this.load();
	}

	public final boolean isLoaded() {
		return this.loaded || this.source.loader().isLoaded(this);
	}

	protected void add(boolean file, String key, T value) {
		this.values.add(new AssetHolder<T>(file, key, this.size(), value, this));
	}

	public final int size() {
		return this.values.size();
	}

	public final boolean isEmpty() {
		return this.size() == 0;
	}

	public final AssetHolder<T> get() {
		return this.get(0);
	}

	public final AssetHolder<T> get(int i) {
		if (!this.isLoaded()) {
			this.load();
		}
		return i < 0 || this.size() <= i ? this.getEmpty() : this.copy(this.values.get(i));
	}

	private final AssetHolder<T> getEmpty() {
		return new AssetHolder<T>(false, null, -1, this.getEmptyValue(), this);
	}

	protected abstract T getEmptyValue();

	private final AssetHolder<T> copy(AssetHolder<T> original) {
		return this.needCopy() ? new AssetHolder<T>(original.file(), original.key(), original.id(), this.copyValue(original.value()), this) : original;
	}

	protected boolean needCopy() {
		return true;
	}

	protected abstract T copyValue(T value);

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		Iterator<AssetHolder<T>> iterator = this.values.iterator();
		while(iterator.hasNext()) {
			builder.append(iterator.next().print());
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}
