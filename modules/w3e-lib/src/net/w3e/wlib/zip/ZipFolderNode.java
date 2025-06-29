package net.w3e.wlib.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;

public class ZipFolderNode extends ZipNode implements Iterable<Map.Entry<String, ZipNode>> {

	private final Map<String, ZipNode> nodes = new LinkedHashMap<>();

	public ZipFolderNode(String name) {
		super(name);
	}

	public void add(InputStream inputStream, ZipEntry entry) throws IOException {
		this.add(inputStream, new LinkedList<>(Arrays.asList(entry.getName().split("/"))), entry);
	}

	private void add(InputStream inputStream, List<String> names, ZipEntry entry) throws IOException {
		if (names.size() == 1) {
			this.nodes.put(names.getFirst(), new ZipFileNode(inputStream, entry));
		} else {
			((ZipFolderNode)this.nodes.computeIfAbsent(names.removeFirst(), ZipFolderNode::new)).add(inputStream, names, entry);
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

	@Override
	public final Iterator<Map.Entry<String, ZipNode>> iterator() {
		return this.nodes.entrySet().iterator();
	}

	public int size() {
		return this.nodes.size();
	}

	@Override
	public void close() throws IOException {
		for (Entry<String, ZipNode> entry : this) {
			entry.getValue().close();
		}
	}

	@Override
	public String toString() {
		return this.nodes.toString();
	}
}
