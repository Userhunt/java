package net.home.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.PrimitiveIterator.OfInt;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.w3e.base.ReflectionUtils;
import net.w3e.base.api.ApiJsonHelper;
import net.w3e.base.api.window.FrameWin;
import net.w3e.base.api.window.Inputs;
import net.w3e.base.jar.JarUtil;

public class MainFrame {

	public static final Random RANDOM = new Random();
	public static final Logger LOGGER = LogManager.getLogger("");
	public static final ApiJsonHelper JSON_HELPER = new ApiJsonHelper(LOGGER);

	public static final int[] version() {
		return new int[]{1,0,0};
	}

	public static final String version(int[] version) {
		OfInt iterator = Arrays.stream(version).iterator();
		StringBuilder builder = new StringBuilder();
		while(iterator.hasNext()) {
			builder.append(iterator.nextInt());
			if (iterator.hasNext()) {
				builder.append(".");
			}
		}
		return builder.toString();
	}

	private static final FrameWin FRAME = new FrameWin("Base " + version(version())) {
		@Override
		public void open(JFrame frameWin) {
			this.setLocation(frameWin.getLocation());
		}
	};

	static {
		net.w3e.base.PrintWrapper.install();
		FRAME.setResizable(false);
	}

	private static final List<FrameObject> FRAMES = new ArrayList<>();

	public static void register(FrameObject frame) {
		if (frame != null) {
			FRAMES.add(frame);
		}
	}

	public static void main(String[] args) throws IOException {
		run(args);
	}

	public static void run(String[] args) {
		run(args, false);
	}

	private static void run(String[] args, boolean clear) {
		if (clear) {
			FRAMES.clear();
		}
		load();
		init();

		int x = 5;
		int y = 5;
		for (FrameObject frame : FRAMES) {
			JButton button = new JButton(frame.getName());
			button.addActionListener(FrameWin.onClick(() -> {
				frame.run(FRAME, Collections.emptyList());
			}));

			button.setBounds(x, y, 150, 26);
			y += 30;

			FRAME.add(button);
		}

		FRAME.setSize(300, y + 41);

		FRAME.setVisible(true);

		MainArgs.main(args, FRAME, FRAMES);
	}

	@SuppressWarnings("deprecation")
	private static void load() {
		if (!JarUtil.isDebug()) {
			try {
				for (File file : new File("mods").listFiles()) {
					if (file.getName().endsWith(".jar")) {
						ZipFile zipFile = new ZipFile(file.toString());
						ZipEntry entry = zipFile.getEntry("META-INF/MANIFEST.MF");
						String[] array = new String(zipFile.getInputStream(entry).readAllBytes()).split("\n");
						for (String string : array) {
							if (string.startsWith("Main-Class: ")) {
								string = string.substring(13, string.length() - 2);
								LOGGER.info("init " + string);
								try {
									Class<?> clazz = Class.forName(string);
									if (ReflectionUtils.instaceOf(clazz, FrameObject.class)) {
										FRAMES.add((FrameObject)clazz.newInstance());
									}
								} catch (Exception e) {;
									e.printStackTrace();
								}
								break;
							}
						}
						zipFile.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void init() {
		register(new TestScreen());
	}

	public static final void sleep(int i) {
		Inputs.sleep(i);
	}
}
