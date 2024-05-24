package net.api.window.jcomponent;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.api.window.AbstractFrameWin;

public class JConsole extends JTextArea {

	public final JScrollPane scroll;

	public JConsole(int width, int height) {
		this(width, height, true);
	}

	public JConsole(int width, int height, boolean scroll) {
		this.setEditable(false);
		this.setLineWrap(true);
		this.setWrapStyleWord(false);
		this.setFont(AbstractFrameWin.FONT);

		if (scroll) {
			this.scroll = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			this.scroll.setPreferredSize(new Dimension(width, height));
		} else {
			this.scroll = null;
		}
	}

	public final void clear() {
		this.setText(null);
	}
}
