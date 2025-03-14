package net.w3e.app.main;

import java.io.IOException;

import javax.swing.JFrame;

import net.w3e.app.gui.AppJFrame.AppJFrameNamedFactory;
import net.w3e.app.main.MainFrame.Args;
import net.w3e.app.old.MainFrame;

@Deprecated
public class OldFrameFactory implements AppJFrameNamedFactory {

	@Override
	public String keyName() {
		return "Old";
	}

	@Override
	public JFrame build(JFrame parent, Args args) {
		try {
			MainFrame.main(new String[0]);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return MainFrame.FRAME;
	}
}
