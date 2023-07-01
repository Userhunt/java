package net.w3e.base.api.window.jcomponent;

import javax.swing.JTextArea;

import net.w3e.base.api.window.AbstractFrameWin;

public class JConsole extends JTextArea {

	public JConsole() {
		this.setEditable(false);
		this.setLineWrap(true);
		this.setWrapStyleWord(false);
		this.setFont(AbstractFrameWin.FONT);
	}
}
