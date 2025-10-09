package net.w3e.wlib.zip;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;

import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.utils.ImageUtils;

public class ZipFileNode extends ZipNode {

	private final byte[] data;
	private Object object;

	public ZipFileNode(InputStream stream, ZipEntry entry) throws IOException {
		super(entry.getName());
		this.data = stream.readAllBytes();
	}

	public boolean isImage() {
		String extension = this.getExtension();
		return "jpg".equals(extension) || "png".equals(extension);
	}

	public byte[] getAsData() {
		return this.data;
	}

	public String getExtension() {
		int i = this.name.lastIndexOf('.');
		if (i > 0) {
			return this.name.substring(i+1);
		}
		return null;
	}

	public final BufferedImage getAsImage() {
		if (this.object instanceof BufferedImage as) {
			return as;
		}
		if (this.isImage()) {
			String extension = this.getExtension();
			if ("jpg".equals(extension)) {
				this.object = ImageUtils.readJPG(new ByteArrayInputStream(this.data));
			}
			if ("png".equals(extension)) {
				this.object = ImageUtils.readPNG(new ByteArrayInputStream(this.data));
			}
		}
		return (BufferedImage)this.object;
	}

	public final JsonElement getAsJson() throws UnsupportedEncodingException {
		if (this.object instanceof JsonElement as) {
			return as;
		}
		if ("json".equals(this.getExtension())) {
			this.object = SosisonUtils.getFancyRegistry().getDeserializer(JsonElement.class).parse(new String(this.data));
		}
		return (JsonElement)this.object;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public String toString() {
		return "{name:\"%s\",read:%s}".formatted(this.name, this.object != null);
	}
}
