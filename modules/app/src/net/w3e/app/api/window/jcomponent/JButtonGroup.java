package net.w3e.app.api.window.jcomponent;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class JButtonGroup<T> extends ButtonGroup {

	@SuppressWarnings("unchecked")
	public final T getSelectedButtonArg() {
		for (Enumeration<AbstractButton> buttons = this.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();

			if (button.isSelected()) {
				return ((JRadioButton)button).arg;
			}
		}

		return null;
	}

	public class JRadioButton extends javax.swing.JRadioButton {
		private final T arg;

		public JRadioButton(String text, T arg) {
			super(text);
			this.arg = arg;
		}
	}
}
