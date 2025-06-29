package net.w3e.app.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;

import net.w3e.app.gui.AppJFrame.AppJFrameFactoryMultiple;
import net.w3e.app.gui.AppJFrame.AppJFrameNamedFactoryImpl;
import net.w3e.app.main.MainFrame.Args;
import net.w3e.app.utils.manga.MangaGui;
import net.w3e.app.utils.poe.PoeGui;
import net.w3e.app.utils.tf2.Tf2Gui;

public class UtilsFrameFactory implements AppJFrameFactoryMultiple {

	@Override
	public Collection<Object> values() {
		List<Object> list = new ArrayList<>();
		list.add(new AppJFrameNamedFactoryImpl("tf2", this::tf2));
		list.add(new AppJFrameNamedFactoryImpl("Poe2", this::poe2));
		list.add(new AppJFrameNamedFactoryImpl("Manga", this::manga));

		return list;
	}

	private JFrame tf2(JFrame parent, Args args) {
		Tf2Gui frame = new Tf2Gui();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}

	private JFrame poe2(JFrame parent, Args args) {
		PoeGui poeGui = new PoeGui();
		poeGui.setVisible(true);
		return null;
	}

	private JFrame manga(JFrame parent, Args args) {
		MangaGui frame = new MangaGui();
		frame.initScreen();
		frame.pack();
		frame.atRightPosition(parent);
		frame.setVisible(true);
		return null;
	}
	
}
