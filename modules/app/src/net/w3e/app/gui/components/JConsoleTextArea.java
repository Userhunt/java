package net.w3e.app.gui.components;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.w3e.app.gui.utils.JComponentGuiUtils;

public class JConsoleTextArea extends JTextPane implements JComponentGuiUtils {

	private final JScrollPane scroll;

	public JConsoleTextArea(int width, int height) {
		this(width, height, true);
	}

	public JConsoleTextArea(int width, int height, boolean scroll) {
		this.setEditable(false);
		this.setFont(MONOSPACE_FONT);

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

	public final JComponent getAddComponent() {
		if (this.scroll != null) {
			return this.scroll;
		}
		return this;
	}
}

