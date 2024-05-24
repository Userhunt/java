package net.home.oba2;

import net.api.assets.AssetCollector;
import net.api.assets.AssetLoader;
import net.api.assets.type.PngAsset;

public class OneBitAssets {
	public static final AssetLoader INSTANCE = AssetLoader.INSTANCE.registerAssetLoader("oba", loader -> AssetCollector.jarFolder("oba", loader));

	static {
		RandomLoader.register();
	}

	public static final class RandomLoader {
		public static final AssetLoader RANDOM = INSTANCE.registerAssetLoader("random", loader -> AssetCollector.jarFolder("random", loader));
		public static final AssetLoader SETUP = RANDOM.registerAssetLoader("setup", loader -> AssetCollector.jarFolder("setup", loader));

		public static final PngAsset END_ADVENTURE_BUTTON = SETUP.register("end_adventure_button", loader -> singleImage("end_adventure.png", loader));
		public static final PngAsset ASCENSION_ICON = SETUP.register("ascension_icon", loader -> singleImage("ascension_icon.png", loader));
		public static final PngAsset ASCENSION_BUTTON = SETUP.register("ascension_button", loader -> singleImage("ascension_button.png", loader));

		private static void register() {}
	}

	public static final PngAsset singleImage(String path, AssetLoader loader) {
		return singleImage(path, loader, false);
	}

	public static final PngAsset singleImage(String path, AssetLoader loader, boolean needCopy) {
		return image(AssetCollector.jarFile(path, loader), needCopy);
	}

	public static final PngAsset multipleImage(String path, AssetLoader loader) {
		return multipleImage(path, loader, false);
	}

	public static final PngAsset multipleImage(String path, AssetLoader loader, boolean needCopy) {
		return image(AssetCollector.jarFolder(path, loader), needCopy);
	}

	public static final PngAsset image(AssetCollector collector, boolean needCopy) {
		return new PngAsset(collector) {
			@Override
			protected final boolean needCopy() {
				return needCopy;
			}
		};
	}
}
