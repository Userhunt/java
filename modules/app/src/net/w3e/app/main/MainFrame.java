package net.w3e.app.main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;

import lombok.CustomLog;
import net.skds.lib2.awtutils.layouts.LayoutMode;
import net.skds.lib2.awtutils.layouts.VerticalLayout;
import net.w3e.app.gui.AppJFrame;
import net.w3e.app.gui.utils.JFrameGuiUtils;
import net.w3e.app.gui.utils.JGuiUtils;
import net.w3e.lib.utils.ResourceUtil;
import net.w3e.wlib.mat.WAlign;
import net.w3e.wlib.mat.WAlign.WAlignEnum;

@CustomLog
public class MainFrame extends AppJFrame {

	public MainFrame() {
		this.setTitleWithVersion("Main");
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String packageName = MainFrame.class.getPackage().toString().substring("package ".length());
		InputStream stream = ResourceUtil.getResourceAsStream(packageName.replace(".", "/"));

		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		List<Class<Object>> classes = reader.lines()
			.filter(line -> line.endsWith(".class"))
			.map(line -> getClass(line, packageName))
			.filter(e -> (JFrame.class.isAssignableFrom(e) || AppJFrameAbstractFactory.class.isAssignableFrom(e)) && !MainFrame.class.isAssignableFrom(e))
			.toList();

		Map<String, AppJFrameFactory> apps = new LinkedHashMap<>();
		for (Class<Object> cl : classes) {
			addFactory(apps, cl);
		}

		apps.remove(MainFrame.class.getSimpleName());

		//System.out.println(Arrays.asList(args));

		AppJFrameFactory app = null;
		int screen = -1;
		boolean fullScreen = false;
		WAlign align = WAlign.centerCenter;
		int x = 0;
		int y = 0;
		Args programmArgs = new Args();

		for (String arg : args) {
			int index = arg.indexOf("=");
			if (index == -1) {
				log.warn("cant find \"=\" for " + arg);
				continue;
			}
			String key = arg.substring(0, index);
			String val = arg.substring(index + 1);
			switch (key) {
				case "app" -> {
					app = apps.get(val);
				}
				case "screen" -> {
					screen = Integer.valueOf(val);
				}
				case "fullScreen" -> {
					fullScreen = Boolean.valueOf(val);
				}
				case "align" -> {
					align = WAlignEnum.valueOf(val).function;
				}
				case "x" -> {
					x = Integer.valueOf(val);
				}
				case "y" -> {
					y = Integer.valueOf(val);
				}
				default -> {
					programmArgs.put(key, val);
				}
			}
		}

		JFrame frame;

		if (app == null) {
			frame = new MainFrame();

			JGuiUtils.addBorder(frame.getRootPane(), 10);

			frame.setLayout(new VerticalLayout(5, LayoutMode.FILL));

			for (Entry<String, AppJFrameFactory> entry : apps.entrySet()) {
				JButton button = new JButton(entry.getKey());
				AppJFrameFactory factory = entry.getValue();
				button.addActionListener(_ -> {
					JFrame child = factory.build(frame, programmArgs);
					if (child != null) {
						child.setLocationRelativeTo(frame);
						child.setVisible(true);
					}
				});
				frame.add(button);
			}
		} else {
			frame = app.build(null, programmArgs);
		}
		if (fullScreen) {
			JFrameGuiUtils.showFullOnScreen(screen, frame);
		} else { 
			if (screen != -1) {
				JFrameGuiUtils.showOnScreen(screen, frame);
			}
			JFrameGuiUtils.initScreen(frame, align);

			frame.setLocation(frame.getX() + x, frame.getY() + y);
		}

		frame.setVisible(true);
	}

	private static void addFactory(Map<String, AppJFrameFactory> apps, Class<Object> cl) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (AppJFrameNamedFactory.class.isAssignableFrom(cl)) {
			AppJFrameNamedFactory factory = (AppJFrameNamedFactory)cl.getConstructor().newInstance();
			addFactory(apps, factory);
			return;
		}
		if (AppJFrameFactoryMultiple.class.isAssignableFrom(cl)) {
			AppJFrameFactoryMultiple factory = (AppJFrameFactoryMultiple)cl.getConstructor().newInstance();
			addFactory(apps, factory);
			return;
		}
		addFactory(apps, (Object)cl);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void addFactory(Map<String, AppJFrameFactory> apps, Object value) {
		if (value instanceof AppJFrameNamedFactory factory) {
			apps.put(factory.keyName(), factory);
			return;
		}
		if (value instanceof AppJFrameFactoryMultiple factory) {
			for (Object v : factory.values()) {
				addFactory(apps, v);
			}
			return;
		}
		if (value instanceof Class cl) {
			Constructor<JFrame> factory;
			try {
				factory = ((Class<JFrame>)(Class)cl).getConstructor();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			apps.put(cl.getSimpleName(), (_, _) -> {
				try {
					return factory.newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
					throw new RuntimeException(exception);
				}
			});
		}
		log.error("Cant create factory " + value);
	}

	@SuppressWarnings("unchecked")
	private static Class<Object> getClass(String className, String packageName) {
        try {
            return (Class<Object>)Class.forName(packageName + "."
              + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {}
        return null;
    }

	public static class Args extends LinkedHashMap<String, String> {}
}
