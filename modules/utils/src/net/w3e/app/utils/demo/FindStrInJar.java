package net.w3e.app.utils.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FindStrInJar {

	public String condition; // Query condition

	public ArrayList<String> jarFiles = new ArrayList<String>();

	public FindStrInJar() {}

	public FindStrInJar(String condition) {
		this.condition = condition;
	}

	public FindStrInJar(String condition, String exclude) {
		this.condition = condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<String> find(String dir, boolean recurse, boolean toLowerCase) {
		searchDir(dir, recurse, toLowerCase);
		return this.jarFiles;
	}

	public List<String> getFilenames() {
		return this.jarFiles;
	}

	protected String getClassName(ZipEntry entry) {
		StringBuffer className = new StringBuffer(entry.getName().replace("/", "."));
		return className.toString();
	}

	protected void searchDir(String dir, boolean recurse, boolean toLowerCase) {
		try {
			File d = new File(dir);
			if (!d.isDirectory()) {
				System.err.println("not found directory");
				return;
			}
			File[] files = d.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (recurse && files[i].isDirectory()) {
					searchDir(files[i].getAbsolutePath(), recurse, toLowerCase);
				} else {
					String filename = files[i].getAbsolutePath();
					if (filename.endsWith(".jar") || filename.endsWith(".zip")) {
						try (ZipFile zip = new ZipFile(filename)) {
							Enumeration<?> entries = zip.entries();
							while (entries.hasMoreElements()) {
								ZipEntry entry = (ZipEntry) entries.nextElement();
								String thisClassName = getClassName(entry);
								BufferedReader r = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
								while (r.read() != -1) {
									String tempStr = r.readLine();
									if (null != tempStr && (toLowerCase ? tempStr.toLowerCase() : tempStr).indexOf(condition) > -1) {
										this.jarFiles.add(thisClassName);
										break;
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		FindStrInJar findInJar = new FindStrInJar("TIMEOUT_DISCONNECTION_MESSAGE"); // String to be looking for

		//List<String> jarFiles = findInJar.find("D:/Minecraft/fabric/data-pack-mod-template-1.20.1/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-92f061055d/24w19b-loom.mappings.24w19b.layered+hash.2198-v2", true, true);

		List<String> jarFiles = findInJar.find("D:/sd/SD-World-Mod-skydex-server/modules/skydex-client/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-02b89d809d/1.20.6-loom.mappings.1_20_6.layered+hash.2198-v2", true, true);
		//List<String> jarFiles = findInJar.find("D:/Universal-Core/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-f3559f27f0/24w19b-loom.mappings.24w19b.layered+hash.2198-v2", true, true);

		if (jarFiles.isEmpty()) {
			System.out.println("Not Found");
		} else {
			for (int i = 0; i < jarFiles.size(); i++) {
				System.out.println(jarFiles.get(i));
			}
		}
	}

}