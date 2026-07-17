package net.w3e.app.frames;

import net.skds.lib2.demo.DemoFrame;
import net.w3e.app.gui.*;
import net.w3e.app.gui.dungeon.DGFrame;
import net.w3e.lib.utils.ResourceUtil;
import net.w3e.wlib.awtutils.AppArgs;
import net.w3e.wlib.awtutils.frame.AppJFrameAbstractFactory;
import net.w3e.wlib.collection.TaskQueue;
import net.w3e.wlib.registry.WDynamicRegistry;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class BaseFrameFactory implements AppJFrameAbstractFactory.MultipleFactory {

	private static final String PROGRESS_KEY = "Progress Frame";

	@Override
	public Collection<Object> values() {
		List<Object> list = new ArrayList<>();
		list.add(new NamedFactoryImpl("Bezier", this::bezier));
		list.add(new NamedFactoryImpl("Color", this::color));
		list.add(new NamedFactoryImpl("Console", this::console));
		list.add(new NamedFactoryImpl("Noise", this::noise));
		list.add(new NamedFactoryImpl(PROGRESS_KEY, this::progress));
		list.add(new NamedFactoryImpl("Test Registry", this::registry));
		list.add(new NamedFactoryImpl("Task Executor", this::task));
		list.add(new NamedFactoryImpl("Read Jar", this::readJar));
		list.add(new NamedFactoryImpl("Sasai", this::sasai));
		list.add(new NamedFactoryImpl("Dungeon Generator", this::dungeon));
		return list;
	}

	private JFrame bezier(JFrame parent, AppArgs args) {
		BezierFrame frame = new BezierFrame();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}

	private JFrame color(JFrame parent, AppArgs args) {
		ColorFrame frame = new ColorFrame();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}

	private JFrame console(JFrame parent, AppArgs args) {
		ConsoleFrame frame = new ConsoleFrame("Simple Console");
		frame.initScreen();
		frame.enableConsole();
		frame.setSize(700, 500);
		return frame;
	}

	private JFrame noise(JFrame parent, AppArgs args) {
		NoiseFrame frame = new NoiseFrame();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}

	private JFrame progress(JFrame parent, AppArgs args) {
		ProgressFrame frame = new ProgressFrame(PROGRESS_KEY);
		frame.initScreen();
		frame.enableConsole();
		frame.setSize(700, 500);
		frame.testTask();
		return frame;
	}

	private JFrame registry(JFrame parent, AppArgs args) {
		WDynamicRegistry.main(null);
		return null;
	}

	private JFrame task(JFrame parent, AppArgs args) {
		TaskQueue.main(null);
		return null;
	}

	private JFrame readJar(JFrame parent, AppArgs args) {
		System.out.println(ResourceUtil.getResourceFiles("test"));
		return null;
	}

	private JFrame sasai(JFrame parent, AppArgs args) {
		return new DemoFrame();
	}

	private JFrame dungeon(JFrame parent, AppArgs args) {
		DGFrame frame = new DGFrame();
		frame.initScreen();
		return frame;
	}
}
