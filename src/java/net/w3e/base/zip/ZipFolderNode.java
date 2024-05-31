package net.w3e.base.zip;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFolderNode extends ZipNode {

	private final Map<String, ZipNode> nodes = new HashMap<>();

	public final void add(ZipInputStream stream, ZipEntry entry) throws IOException {
		this.add(stream, new LinkedList<>(Arrays.asList(entry.getName().split("/"))), entry);
	}

	private final void add(ZipInputStream stream, List<String> names, ZipEntry entry) throws IOException {
		if (names.size() == 1) {
			if (!entry.isDirectory()) {
				this.nodes.put(names.get(0), new ZipFileNode(stream, entry));
			}
		} else {
			((ZipFolderNode)this.nodes.computeIfAbsent(names.remove(0), key -> new ZipFolderNode())).add(stream, names, entry);
		}
	}

	public final ZipNode get(String name) {
		return this.get(new LinkedList<>(Arrays.asList(name.split("/"))));
	}

	private final ZipNode get(List<String> names) {
		ZipNode node = this.nodes.get(names.remove(0));
		if (names.isEmpty()) {
			return node;
		} else if (node instanceof ZipFolderNode folder) {
			return folder.get(names);
		} else {
			return null;
		}
	}
	
}
