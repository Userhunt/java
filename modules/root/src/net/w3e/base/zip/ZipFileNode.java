package net.w3e.base.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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

	public final JsonElement getAsJson() throws JsonIOException, JsonSyntaxException, UnsupportedEncodingException {
		if (!(this.object instanceof JsonElement)) {
			this.object = JsonParser.parseReader(new InputStreamReader(this.data, "UTF-8"));
		}
		return (JsonElement)this.object;
	}
	
}
