package net.api.assets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;

import net.api.assets.type.PngAsset;
import net.api.assets.type.TxtAsset;
import net.w3e.base.PrintWrapper;
import net.w3e.base.message.BMessageLoggerHelper;
import net.w3e.base.message.MessageUtil;

public class AssetLoader {
	public static final AssetLoader INSTANCE = new AssetLoader(AssetCollector.folder("", null));

	public static final BMessageLoggerHelper LOGGER = new BMessageLoggerHelper(LogManager.getLogger("Asset Loader"));

	public final AssetCollector source;

	private AssetLoader(AssetCollector source) {
		this.source = source;
	}

	private final Map<String, Asset<?>> assets = new TreeMap<>();
	private final Map<String, Asset<?>> notLoaded = new TreeMap<>();

	public final AssetLoader registerAssetLoader(String key, Function<AssetLoader, AssetCollector> sourceFactory) {
		return register(key, loader -> new NestedAssetLoader(sourceFactory.apply(loader).asFolder())).loader;
	}

	public final <T extends Asset<?>> T register(String key, Function<AssetLoader, T> assetFactory) {
		if (key == null) {
			LOGGER.error(MessageUtil.IS_EMPTY_OR_NULL.createMsg("Key of asset"));
			return null;
		}
		if (assetFactory == null) {
			LOGGER.error(MessageUtil.IS_EMPTY_OR_NULL.createMsg("AssetFactory"));
			return null;
		}
		T asset = assetFactory.apply(this);
		if (asset == null) {
			LOGGER.error(MessageUtil.IS_EMPTY_OR_NULL.createMsg("Asset"));
			return null;
		}
		if (asset.source.loader() == null) {
			LOGGER.error(MessageUtil.IS_EMPTY_OR_NULL.createMsg("Loader of asset is null"));
			return null;
		}
		if (asset.source.loader() != this) {
			String name = AssetLoader.class.getSimpleName();
			LOGGER.error(MessageUtil.NOT_EQUAL.createMsg(asset.source.loader(), name, this, name));
			return null;
		}
		Asset<?> old = this.assets.put(key, asset);
		if (old == asset) {
			return asset;
		} else if (old != null && notLoaded.remove(key) == null) {
			old.unload();
		}
		this.notLoaded.put(key, null);
		if (!asset.isLoaded()) {
			this.notLoaded.put(key, asset);
		} else {
			this.notLoaded.remove(key);
		}

		return asset;
	}

	public final String getKey(Asset<?> asset) {
		Iterator<Entry<String, Asset<?>>> iterator = this.assets.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, Asset<?>> entry = iterator.next();
			Asset<?> value = entry.getValue();
			if (value == null) {
				iterator.remove();
				continue;
			}
			if (value == asset) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Asset<?> getValue(String string) {
		return this.assets.get(string);
	}

	public final boolean load(Asset<?> asset) {
		String key = this.getKey(asset);
		if (key != null) {
			return this.notLoaded.remove(key) != null;
		}
		return false;
	}

	public final boolean unload(Asset<?> asset) {
		String key = this.getKey(asset);
		if (key != null) {
			return this.notLoaded.put(key, asset) == null;
		}
		return false;
	}

	public final AssetLoader loadAll() {
		while(!this.notLoaded.isEmpty()) {
			Iterator<Entry<String, Asset<?>>> iterator = this.notLoaded.entrySet().iterator();
			Entry<String, Asset<?>> entry = iterator.next();
			Asset<?> value = entry.getValue();
			if (!this.isLoaded(value)) {
				value.load();
				continue;
			}
			iterator.remove();
		}
		return this;
	}

	public final AssetLoader unloadAll() {
		for (Entry<String, Asset<?>> entry : this.assets.entrySet()) {
			Asset<?> value = entry.getValue();
			if (value.isLoaded()) {
				this.notLoaded.put(entry.getKey(), value);
				value.unload();
			}
		}
		return this;
	}

	public final AssetLoader reloadAll() {
		this.assets.values().forEach(Asset::reload);
		return this;
	}

	public final boolean isLoaded(Asset<?> asset) {
		String key = this.getKey(asset);
		if (key != null) {
			return !this.notLoaded.containsKey(key);
		}
		return false;
	}

	private class NestedAssetLoader extends Asset<AssetLoader> {

		private final AssetLoader loader;
		private final File file;
		private final Path jar;

		private NestedAssetLoader(AssetCollector source) {
			super(source);
			this.loader = new AssetLoader(this.source);
			this.file = this.source.getRunPath();
			this.jar = this.source.getJarPath();
		}

		@Override
		protected final boolean testExtension(String extension) {
			return false;
		}

		@Override
		protected final boolean testFile(File file, String extension) {
			return file.equals(this.file);
		}

		@Override
		protected final void loadFile(String key, File file) throws IOException {
			this.loadAdd();
		}

		@Override
		protected final boolean testJar(Path file, String extension) {
			return file.equals(this.jar);
		}

		@Override
		public final void loadJar(String key, String file) throws IOException {
			this.loadAdd();
		}

		private final void loadAdd() {
			this.loader.loadAll();
			if (this.isEmpty()) {
				this.add(false, null, this.loader);
			}
		}

		@Override
		protected final void unload(AssetHolder<AssetLoader> assetHolder) {
			this.loader.unloadAll();
		}

		@Override
		protected final AssetLoader getEmptyValue() {
			return this.loader;
		}

		@Override
		protected final AssetLoader copyValue(AssetLoader value) {
			return this.loader;
		}

		@Override
		public final String toString() {
			return this.loader.loadedToString();
		}
	}

	public final String loadedToString() {
		return this.assets.toString();
	}

	public final void printLoaded() {
		System.out.println(this.loadedToString());
	}

	public static void main(String[] args) {
		PrintWrapper.install();

		AssetLoader.INSTANCE.register("log", loader -> new TxtAsset(AssetCollector.jarFile("log4j2.xml", loader)) {
			@Override
			protected boolean testExtension(String fileName) {
				return super.testExtension(fileName) || fileName.equals("xml");
			}
		});

		AssetLoader jar = AssetLoader.INSTANCE.registerAssetLoader("jar", (loader) -> AssetCollector.jarFolder("test", loader));
		jar.register("file", loader -> new TxtAsset(AssetCollector.jarFile("test.txt", loader)) {
			@Override
			protected boolean testExtension(String fileName) {
				return super.testExtension(fileName) || fileName.equals("xml");
			}
		});
		jar.register("folder", loader -> new TxtAsset(AssetCollector.jarFolder("", loader)) {
			@Override
			protected boolean testExtension(String fileName) {
				return super.testExtension(fileName) || fileName.equals("xml");
			}
		});

		AssetLoader.INSTANCE.loadAll();

		AssetLoader.INSTANCE.printLoaded();
		System.out.println();

		AssetLoader.INSTANCE.register("tf2/config", loader -> new TxtAsset(AssetCollector.runFile("tf2/config.json", loader)));
		AssetLoader.INSTANCE.register("oba", loader -> new PngAsset(AssetCollector.runFile("oba", loader)));
		AssetLoader tf2_loader = AssetLoader.INSTANCE.registerAssetLoader("tf2", (loader) -> AssetCollector.runFolder("tf2", loader));
		tf2_loader.register("config", loader -> new TxtAsset(AssetCollector.runFile("config.json", loader)));
		tf2_loader.register("scripts", loader -> new TxtAsset(AssetCollector.runFile("script.js", loader)) {
			@Override
			protected boolean testFile(File file, String fileName) {
				return super.testFile(file, fileName) || fileName.equals("js");
			}
		});
		tf2_loader.register("folder", loader -> new TxtAsset(AssetCollector.folder("", loader))  {
			@Override
			protected boolean testFile(File file, String fileName) {
				return super.testFile(file, fileName) || fileName.equals("js") || fileName.equals("html");
			}
		});

		tf2_loader.getValue("config").get();
		AssetLoader.INSTANCE.loadAll();

		AssetLoader.INSTANCE.printLoaded();
		AssetLoader.INSTANCE.unloadAll();
		AssetLoader.INSTANCE.printLoaded();
		AssetLoader.INSTANCE.loadAll();
		AssetLoader.INSTANCE.printLoaded();
		AssetLoader.INSTANCE.register("tf2", loader -> new TxtAsset(AssetCollector.file("tf2/config.json", loader)));
		AssetLoader.INSTANCE.printLoaded();
		tf2_loader.printLoaded();
	}
}
