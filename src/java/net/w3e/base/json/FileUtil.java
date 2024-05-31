package net.w3e.base.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.w3e.base.jar.JarUtil;
import net.w3e.base.message.MessageUtil;

import org.apache.logging.log4j.Logger;

public class FileUtil {

	public static JsonElement read(File file) {
		try {
			return BJsonUtil.read(Files.readAllBytes(file.toPath()));
		} catch (Exception e) {}
		return null;
	}

	public static JsonElement read(String file) {
		return read(new File(file));
	}

	public static JsonObject readObject(File file) {
		JsonObject jsonObject = (JsonObject)read(file);
		if (jsonObject == null) {
			return new JsonObject();
		}
		return jsonObject;
	}

	public static JsonObject readObject(String file) {
		return readObject(new File(file));
	}

	public static JsonObject readObjectFromJar(String file) {
		try {
			return BJsonUtil.read(JarUtil.getResourceAsStream(file).readAllBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void write(File file, JsonElement object) {
		writeString(file, BJsonUtil.toPrettyString(object));
	}

	public static void writeString(File file, String string) {
		FileWriter writter = null;
		try {
			new File(file.getAbsolutePath()).getParentFile().mkdirs();
			writter = new FileWriter(file);
			writter.write(string);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writter != null) {
				try {
					writter.flush();
					writter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static JsonObject write(String result) {
		return JsonParser.parseString(result).getAsJsonObject();
	}

	public static void writeJson(File file, String result) {
		write(file, JsonParser.parseString(result).getAsJsonObject());
	}

	public static boolean copyFromJar(Logger log, String target) {
		return copyFromJar(log, new File(new File(target).getAbsolutePath()), target);
	}

	public static boolean copyFromJar(Logger log, File file) {
		return copyFromJar(log, file, file.getPath());
	}

	public static boolean copyFromJar(Logger log, File file, String target) {
		file.getParentFile().mkdirs();
		if (file.exists()) {
			return false;
		}
		try {
			InputStream saved = JarUtil.getResourceAsStream(target);
			Files.copy(saved, file.toPath());
			return true;
		} catch (Exception e) {
			MessageUtil.FILE_NOT_FOUND_JAR.error(log, target);
			return false;
		}
	}

	/**
	 *
	 * @param path - path to file
	 * @param jar - path to jar, used if file is not exists
	 * @return file
	 * @see FileReaderUtil#singleFile(File, String)
	 */
	public static File file(Logger log, String path, String jar) {
		jar = addJson(jar);
		return file(log, new File(path, jar), jar);
	}

	/**
	 *
	 * @param file - file
	 * @param jar - path to jar, used if file is not exists
	 * @return file
	 */
	public static File file(Logger log, File file, String jar) {
		jar = addJson(jar);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			copyFromJar(log, file, jar);
		}
		return file;
	}

	public static String addJson(String str) {
		if (!str.endsWith(".json")) {
			str += ".json";
		}
		return str;
	}

	public static File configFolder(String path) {
		File file = new File(configFolder(), path);
		file.mkdirs();
		return file;
	}

	public static File configFolder() {
		File file = new File(new File("").getAbsolutePath() + "\\config\\w3e\\");
		file.mkdirs();
		return file;
	}

	public static void backup(File folder, File file, String name) {
		int a = 1;
		folder.mkdirs();
		File f = new File(folder, name + ".json");
		if (f.exists()) {
			while (true) {
				String n = name + "_" + a;
				f = new File(folder, n + ".json");
				if (f.exists()) {
					a++;
				} else {
					break;

				}
			}
		}
		try {
			f.getParentFile().mkdirs();
			Files.copy(file.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean exists(File dir, String filename){
		String[] files = dir.list();
		for(String file : files) {
			if (file.equals(filename)) {
				return true;
			}
		}
		return false;
	}

	public static File readAndCopyFromJarFile(Logger logger, String folder, String path) {
		path = addJson(path);
		File file = new File(folder, path);
		for (int i = 0; i < 1; i++) {
			if (file.exists()) {
				return file;
			} else {
				file.delete();
				copyFromJar(logger, file, path);
			}
		}
		return file;
	}

	public static JsonObject readAndCopyFromJarObject(Logger logger, File file, String jarPath) {
		JsonObject jsonObject = readObject(file);

		for (int i = 0; i < 1; i++) {
			if (jsonObject.size() != 0) {
				return jsonObject;
			} else {
				file.delete();
				copyFromJar(logger, file, jarPath);
			}
		}
		return new JsonObject();
	}
}
