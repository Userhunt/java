package net.w3e.base.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
	
	public static final ZipFolderNode read(Path path) {
		ZipFolderNode root = new ZipFolderNode();
		try (InputStream file = Files.newInputStream(path)) {
			try (ZipInputStream stream = new ZipInputStream(file)) {
				ZipEntry entry;
				while((entry = stream.getNextEntry()) != null) {
					root.add(stream, entry);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}
}
