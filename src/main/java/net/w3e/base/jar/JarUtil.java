package net.w3e.base.jar;

import java.io.InputStream;

public class JarUtil {

	public static final InputStream getResourceAsStream(String target) {
		return JarUtil.class.getClassLoader().getResourceAsStream(target);
	}

	/*public static void main(final String[] args) throws URISyntaxException, ZipException, IOException {
		final URI uri;
		final URI exe;

		uri = getJarURI();
		exe = getFile(uri, JarUtil.class.getSimpleName() + ".class");
	}*/

	/*public static URI getFile(String arg) {
		final URI uri;
		final URI exe;

		try {
			uri = getJarURI();
			exe = getFile(uri, arg);
			return exe;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static URI getJarURI() throws URISyntaxException {
		final ProtectionDomain domain;
		final CodeSource	   source;
		final URL			  url;
		final URI			  uri;

		domain = JarUtil.class.getProtectionDomain();
		source = domain.getCodeSource();
		url	= source.getLocation();
		uri	= url.toURI();

		return (uri);
	}

	private static URI getFile(final URI where, final String fileName) throws ZipException, IOException {
		final File location;
		final URI fileURI;

		location = new File(where);

		// not in a JAR, just return the path on disk
		if(location.isDirectory()) {
			fileURI = URI.create(where.toString() + fileName);
		} else {
			final ZipFile zipFile;

			zipFile = new ZipFile(location);

			try {
				fileURI = extract(zipFile, fileName);
			} finally {
				zipFile.close();
			}
		}

		return (fileURI);
	}

	private static URI extract(final ZipFile zipFile, final String  fileName) throws IOException {
		final File tempFile;
		final ZipEntry entry;
		final InputStream  zipStream;
		OutputStream fileStream;

		tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
		tempFile.deleteOnExit();
		entry = zipFile.getEntry(fileName);

		if(entry == null) {
			throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
		}

		zipStream  = zipFile.getInputStream(entry);
		fileStream = null;

		try {
			final byte[] buf;
			int i;

			fileStream = new FileOutputStream(tempFile);
			buf = new byte[1024];
			i = 0;

			while((i = zipStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}
		} finally {
			close(zipStream);
			close(fileStream);
		}

		return (tempFile.toURI());
	}

	private static void close(final Closeable stream) {
		if(stream != null) {
			try {
				stream.close();
			} catch(final IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	*/
	/*private static final String readFromURL(final URL source) {
		try (InputStream in = source.openStream()) {
			return stream2String(in).orElseThrow(() -> new Exception("Can't read " + source));
		} catch (Exception e) {
			return e.getMessage();
		}
	}*/
}