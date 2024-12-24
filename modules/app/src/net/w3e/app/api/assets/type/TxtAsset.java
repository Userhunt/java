package net.w3e.app.api.assets.type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.w3e.app.api.assets.Asset;
import net.w3e.app.api.assets.AssetCollector;
import net.w3e.app.api.assets.AssetHolder;

public class TxtAsset extends Asset<List<String>> {

	public TxtAsset(AssetCollector source) {
		super(source);
	}

	@Override
	protected boolean testExtension(String extension) {
		return extension.equals("txt") || extension.equals("json") || extension.equals("log");
	}

	@Override
	protected final void loadFile(String key, File file) throws IOException {
		this.load(key, true, new FileReader(file));
	}

	@Override
	public final void loadJar(String key, String file) throws IOException {
		this.load(key, false, new InputStreamReader(this.getJarInputStream(file)));
	}

	private final void load(String key, boolean file, Reader reader) throws IOException {
		try(BufferedReader br = new BufferedReader(reader)) {
			List<String> list = new ArrayList<>();
			String line = br.readLine();

			while (line != null) {
				list.add(line);
				line = br.readLine();
			}
			this.add(file, key, list);
		}
	}

	@Override
	protected final void unload(AssetHolder<List<String>> assetHolder) {}

	@Override
	protected final List<String> getEmptyValue() {
		return new ArrayList<>();
	}

	@Override
	protected final List<String> copyValue(List<String> value) {
		return new ArrayList<>(value);
	}
}
