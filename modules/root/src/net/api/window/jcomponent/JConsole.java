package net.api.window.jcomponent;

import javax.swing.JTextArea;

import net.api.window.AbstractFrameWin;

public class JConsole extends JTextArea {

	public JConsole() {
		this.setEditable(false);
		this.setLineWrap(true);
		this.setWrapStyleWord(false);
		this.setFont(AbstractFrameWin.FONT);
	}
}
