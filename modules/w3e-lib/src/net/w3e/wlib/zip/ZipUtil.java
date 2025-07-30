package net.w3e.wlib.zip;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

	public static final ZipFolderNode read(Path path) {
		ZipFolderNode root = new ZipFolderNode("");
		try (ZipFile zipFile = new ZipFile(path.toAbsolutePath().toString())) {
			Enumeration<? extends ZipEntry> iterator = zipFile.entries();
			while (iterator.hasMoreElements()) {
				ZipEntry entry = iterator.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				try (InputStream stream = zipFile.getInputStream(entry)) {
					root.add(stream, entry);
					stream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			zipFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
		/*try (InputStream file = Files.newInputStream(path)) {
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
		return root;*/
	}
}
