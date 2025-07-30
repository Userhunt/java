package net.w3e.sjni;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class SJNIClassLoader extends ClassLoader {

	public static final SJNIClassLoader INSTANCE = new SJNIClassLoader();

	private SJNIClassLoader() {
	}

	public Class<?> compileClass(String name, byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		synchronized (getClassLoadingLock(name)) {
			return defineClass(name, bytes, 0, bytes.length);
		}
	}


	@Override
	public URL getResource(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<URL> getResources(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected URL findResource(String moduleName, String name) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected URL findResource(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<URL> findResources(String name) {
		throw new UnsupportedOperationException();
	}
}