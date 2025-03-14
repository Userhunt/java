package net.w3e.app.gui.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public interface JGuiUtils {
	Font MONOSPACE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	default JComponent getAsJComponent() {
		return (JComponent)this;
	}

	default Component getAsComponent() {
		return (Component)this;
	}

	default void addBorder() {
		addBorder(10);
	}

	void addBorder(int border);

	public static void addBorder(JComponent component, int border) {
		addBorder(component, border, border, border, border);
	}

	static void addBorder(JComponent component, int top, int left, int bottom, int right) {
		component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	default void setSize(int width, int height) {
		setSize(getAsComponent(), width, height);
	}

	static void setSize(Component component, int width, int height) {
		component.setPreferredSize(new Dimension(width, height));
		component.setMinimumSize(new Dimension(width, height));
		component.setMaximumSize(new Dimension(width, height));
	}
}
