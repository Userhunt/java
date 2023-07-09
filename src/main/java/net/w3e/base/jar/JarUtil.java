package net.w3e.base.jar;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JarUtil {

	public static final boolean isDebug() {
		return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-Xdebug");
	}

	public static final InputStream getResourceAsStream(String target) {
		try {
			InputStream stream = JarUtil.class.getClassLoader().getResourceAsStream(target);
			if (stream != null) {
				return stream;
			}
		} catch (Exception ignored) {}
		try {
			return new FileInputStream(target);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final List<Path> getJarFolder(String folder) {
		if (isDebug()) {
			ClassLoader classLoader = JarUtil.class.getClassLoader();

			URL resource = classLoader.getResource(folder);

			// dun walk the root path, we will walk all the classes
			try {
				return Files.walk(Paths.get(resource.toURI())).filter(Files::isRegularFile).collect(Collectors.toList());
			} catch (Exception e) {
				return Collections.emptyList();
			}
		} else {
			String jarPath;
			try {
				jarPath = JarUtil.class.getProtectionDomain()
					.getCodeSource()
					.getLocation()
					.toURI()
					.getPath();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			// file walks JAR
			URI uri = URI.create("jar:file:" + jarPath);
			try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
				return Files.walk(fs.getPath(folder)).filter(Files::isRegularFile).collect(Collectors.toList());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}