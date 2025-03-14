package net.w3e.wlib.zip;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.skds.lib2.io.chars.BufferedReaderCharInput;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.elements.JsonElement;

public class ZipFileNode extends ZipNode {

	private final ByteArrayInputStream data;
	private Object object;

	public ZipFileNode(ZipInputStream stream, ZipEntry entry) throws IOException {
		this.data = new CopyStream(stream).toInputStream();
	}

	private class CopyStream extends ByteArrayOutputStream {
	
		public CopyStream(ZipInputStream stream) throws IOException {
			stream.transferTo(this);
		}

		public ByteArrayInputStream toInputStream() {
			return new ByteArrayInputStream(this.buf, 0, this.count);
		}
	}

	public final JsonElement getAsJson() throws UnsupportedEncodingException {
		if (!(this.object instanceof JsonElement)) {
			BufferedReaderCharInput input = new BufferedReaderCharInput(new BufferedReader(new InputStreamReader(this.data, "UTF-8")));
			this.object = JsonUtils.getFancyRegistry().getDeserializer(JsonElement.class).parse(input);
		}
		return (JsonElement)this.object;
	}
	
}
