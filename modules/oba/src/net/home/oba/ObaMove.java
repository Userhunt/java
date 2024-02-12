package net.home.oba;

import net.api.window.JNAWindow;
import net.api.window.Inputs;
import net.api.window.JNAWindow.WindowInfo;

public class ObaMove extends Inputs {

	private static WindowInfo INFO = null;
	//private static int LAST;

	public static final void init() {
		INFO = null;
		focus();
	}

	public static final void focus() {
		if (INFO == null) {
			INFO = JNAWindow.getWindowInfo("OneBit Adventure");
		}
		//LAST = FindWindow.GetActiveWindow();
		JNAWindow.setForegroundWindow(INFO.hwnd());
	}

	public static final void backFocus() {
		//System.out.println(LAST);
		//FindWindow.setForegroundWindow(LAST);
	}
}
