package net.w3e.app.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import net.skds.lib2.demo.DemoFrame;
import net.w3e.app.gui.AppJFrame;
import net.w3e.app.gui.AppJFrame.AppJFrameNamedFactoryImpl;
import net.w3e.app.gui.frame.BezierFrame;
import net.w3e.app.gui.frame.ColorFrame;
import net.w3e.app.gui.frame.ConsoleFrame;
import net.w3e.app.gui.frame.NoiseFrame;
import net.w3e.app.gui.frame.ProgressFrame;
import net.w3e.app.gui.frame.dungeon.DungeonGeneratorFrame;
import net.w3e.app.main.MainFrame.Args;
import net.w3e.app.utils.registry.DynamicRegistry;
import net.w3e.lib.utils.ResourceUtil;
import net.w3e.wlib.collection.TaskQueue;

public class BaseFrameFactory implements AppJFrame.AppJFrameFactoryMultiple {

	private static final String PROGRESS_KEY = "Progress Frame";

	@Override
	public Collection<Object> values() {
		List<Object> list = new ArrayList<>();
		list.add(new AppJFrameNamedFactoryImpl("Bezier", this::bezier));
		list.add(new AppJFrameNamedFactoryImpl("Color", this::color));
		list.add(new AppJFrameNamedFactoryImpl("Console", this::console));
		list.add(new AppJFrameNamedFactoryImpl("Noise", this::noise));
		list.add(new AppJFrameNamedFactoryImpl(PROGRESS_KEY, this::progress));
		list.add(new AppJFrameNamedFactoryImpl("Test Registry", this::registry));
		list.add(new AppJFrameNamedFactoryImpl("Task Executor", this::task));
		list.add(new AppJFrameNamedFactoryImpl("Read Jar", this::readJar));
		list.add(new AppJFrameNamedFactoryImpl("Sasai", this::sasai));
		list.add(new AppJFrameNamedFactoryImpl("Dungeon Generator", this::dungeon));
		return list;
	}

	private JFrame bezier(JFrame parent, Args args) {
		BezierFrame frame = new BezierFrame();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}

	private JFrame color(JFrame parent, Args args) {
		ColorFrame frame = new ColorFrame();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}

	private JFrame console(JFrame parent, Args args) {
		ConsoleFrame frame = new ConsoleFrame("Simple Console");
		frame.initScreen();
		frame.enableConsole();
		frame.setSize(700, 500);
		return frame;
	}

	private JFrame noise(JFrame parent, Args args) {
		NoiseFrame frame = new NoiseFrame();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}

	private JFrame progress(JFrame parent, Args args) {
		ProgressFrame frame = new ProgressFrame(PROGRESS_KEY);
		frame.initScreen();
		frame.enableConsole();
		frame.setSize(700, 500);
		frame.testTask();
		return frame;
	}

	private JFrame registry(JFrame parent, Args args) {
		DynamicRegistry.main(null);
		return null;
	}

	private JFrame task(JFrame parent, Args args) {
		TaskQueue.main(null);
		return null;
	}

	private JFrame readJar(JFrame parent, Args args) {
		System.out.println(ResourceUtil.getResourceFiles("test"));
		return null;
	}

	private JFrame sasai(JFrame parent, Args args) {
		DynamicRegistry.main(null);
		return new DemoFrame();
	}

	private JFrame dungeon(JFrame parent, Args args) {
		DungeonGeneratorFrame frame = new DungeonGeneratorFrame();
		frame.initScreen();
		return frame;
	}
}
