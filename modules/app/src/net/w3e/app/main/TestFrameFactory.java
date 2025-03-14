package net.w3e.app.main;

import javax.swing.JFrame;

import net.w3e.app.gui.AppJFrame.AppJFrameNamedFactory;
import net.w3e.app.main.MainFrame.Args;

public class TestFrameFactory implements AppJFrameNamedFactory {

	@Override
	public String keyName() {
		return "Test";
	}

	@Override
	public JFrame build(JFrame parent, Args args) {
		return null;
	}
}
