package net.api.assets;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public record AssetCollector(String filePath, String jarPath, boolean singleFile, AssetLoader loader) {

	public static AssetCollector runFile(String runPath, AssetLoader loader) {
		return new AssetCollector(runPath, null, true, loader);
	}
	public static AssetCollector runFolder(String runPath, AssetLoader loader) {
		return new AssetCollector(runPath, null, false, loader);
	}
	public static AssetCollector jarFile(String jarPath, AssetLoader loader) {
		return new AssetCollector(null, jarPath, true, loader);
	}
	public static AssetCollector jarFolder(String jarPath, AssetLoader loader) {
		return new AssetCollector(null, jarPath, false, loader);
	}
	public static AssetCollector file(String path, AssetLoader loader) {
		return new AssetCollector(path, path, true, loader);
	}
	public static AssetCollector folder(String path, AssetLoader loader) {
		return new AssetCollector(path, path, false, loader);
	}

	public final AssetCollector asFolder() {
		return new AssetCollector(this.filePath, this.jarPath, false, this.loader);
	}

	public final File getRunPath() {
		if (this.filePath != null) {
			if (this.loader != null) {
				File file = this.loader.source.getRunPath();
				if (file != null) {
					return new File(file, this.filePath);
				} else {
					return null;
				}
			} else {
				return new File(new File("").getAbsolutePath());
			}
		} else {
			return null;
		}
	}

	public final Path getJarPath() {
		if (this.jarPath != null) {
			if (this.loader != null) {
				Path file = this.loader.source.getJarPath();
				if (file != null) {
					String path = file.toString();
					return Paths.get(path + (path.length() != 0 ? "\\" : "") + this.jarPath);
				} else {
					return null;
				}
			} else {
				return Paths.get(this.jarPath);
			}
		} else {
			return null;
		}

	}
}

