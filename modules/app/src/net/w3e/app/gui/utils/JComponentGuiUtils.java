package net.w3e.app.gui.utils;

public interface JComponentGuiUtils extends JGuiUtils {

	@Override
	default void addBorder(int border) {
		JGuiUtils.addBorder(getAsJComponent(), border);
	}
}
