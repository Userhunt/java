package net.home.main;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.api.ApiJsonHelper;
import net.api.window.FrameWin;
import net.api.window.Inputs;
import net.home.main.simple.TestScreen;
import net.w3e.base.ReflectionUtils;
import net.w3e.base.jar.JarUtil;

public class MainFrame {

	public static final Random RANDOM = new Random();
	public static final Logger LOGGER = LogManager.getLogger("");
	public static final ApiJsonHelper JSON_HELPER = new ApiJsonHelper(LOGGER);

	public static final int[] version() {
		return new int[]{1,2};
	}

	public static final String version(int[] version) {
		if (version != null && version.length > 0) {
			IntList list = new IntArrayList(version);
			int size = list.size();
			while(size >= 0) {
				size--;
				if (list.getInt(size) <= 0) {
					list.removeInt(size);
				} else {
					break;
				}
			}
			IntListIterator iterator = list.iterator();
			StringBuilder builder = new StringBuilder();
			while(iterator.hasNext()) {
				builder.append(iterator.nextInt());
				if (iterator.hasNext()) {
					builder.append(".");
				}
			}
			return builder.toString();
		} else {
			return "";
		}
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
		run(args, false, null);
	}

	private static void run(String[] args, boolean clear, Object fake) {
		if (clear) {
			FRAMES.clear();
		}
		load();
		init();

		Iterator<FrameObject> iterator = FRAMES.iterator();
		while(iterator.hasNext()) {
			FrameObject frame = iterator.next();
			JButton button = new JButton(frame.getName());
			button.addActionListener(FrameWin.onClick(() -> {
				frame.run(FRAME, Collections.emptyList(), fake);
			}));
			FrameWin.setSize(button, 150, 26);
			button.setAlignmentX(Component.CENTER_ALIGNMENT);
			FRAME.add(button);
			if (iterator.hasNext()) {
				FRAME.add(Box.createVerticalStrut(10));
			}
		}

		FRAME.setMinimumSize(new Dimension(250, 0));
		FRAME.pack();

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
