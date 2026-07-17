package net.w3e.app.v1.u.main;

import java.io.IOException;

import javax.swing.JFrame;

import net.w3e.app.v1.gui.AppJFrame.AppJFrameNamedFactory;
import net.w3e.app.v1.u.main.MainFrame.Args;
import net.w3e.app.v1.old.MainFrame;

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
