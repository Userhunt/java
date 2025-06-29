package net.w3e.wlib.zip;

import java.io.IOException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ZipNode {

	protected final String name;

	public abstract void close() throws IOException;

}
