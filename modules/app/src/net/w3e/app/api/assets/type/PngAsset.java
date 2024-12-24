package net.w3e.app.api.assets.type;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.skds.lib2.utils.ImageUtils;
import net.w3e.app.api.ImageUtil;
import net.w3e.app.api.assets.Asset;
import net.w3e.app.api.assets.AssetCollector;
import net.w3e.app.api.assets.AssetHolder;

public class PngAsset extends Asset<BufferedImage> {

	public PngAsset(AssetCollector source) {
		super(source);
	}

	@Override
	protected boolean testExtension(String extension) {
		return extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("bmp");
	}

	@Override
	protected final void loadFile(String key, File file) throws IOException {
		this.add(true, key, ImageUtils.readPNG(new FileInputStream(file)));
	}

	@Override
	public void loadJar(String key, String file) throws IOException {
		this.add(false, key, ImageUtils.readPNG(this.getJarInputStream(file)));
	}

	@Override
	protected final void unload(AssetHolder<BufferedImage> assetHolder) {}

	@Override
	protected final BufferedImage getEmptyValue() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}

	@Override
	protected final BufferedImage copyValue(BufferedImage value) {
		return ImageUtil.deepCopy(value);
	}
}
