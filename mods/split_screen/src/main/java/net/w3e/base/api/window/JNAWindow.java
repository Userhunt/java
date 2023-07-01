package net.w3e.base.api.window;


import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinUser;
//import com.sun.jna.platform.win32.WinDef.*;

public class JNAWindow {

	public static void listAllWindows() throws AWTException, IOException {
		final List<WindowInfo> inflList = new ArrayList<WindowInfo>();
		final List<Integer> order = new ArrayList<Integer>();
		int top = User32.instance.GetTopWindow(0);
		while (top != 0) {
			order.add(top);
			top = User32.instance.GetWindow(top, User32.GW_HWNDNEXT);
		}

		User32.instance.EnumWindows(new WndEnumProc() {
			public boolean callback(int hWnd, int lParam) {
				WindowInfo info = getWindowInfo(hWnd);
				inflList.add(info);
				return true;
			}

		}, 0);
		Collections.sort(inflList, new Comparator<WindowInfo>() {
			public int compare(WindowInfo o1, WindowInfo o2) {
				return order.indexOf(o1.hwnd) - order.indexOf(o2.hwnd);
			}
		});
		for (WindowInfo w : inflList) {
			System.out.println(w);
		}
	}

	public static WindowInfo getWindowInfo(String title) {
		return getWindowInfo(User32.instance.FindWindowA(null, "OneBit Adventure"));
	}

	public static WindowInfo getWindowInfo(int hWnd) {
		RECT r = new RECT();
		User32.instance.GetWindowRect(hWnd, r);
		byte[] buffer = new byte[1024];
		User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
		String title = Native.toString(buffer);
		WindowInfo info = new WindowInfo(hWnd, r, title);
		return info;
	}

	public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
		boolean callback(int hWnd, int lParam);
	}

	public static interface User32 extends StdCallLibrary {
		public static final String SHELL_TRAY_WND = "Shell_TrayWnd";
		public static final int WM_COMMAND = 0x111;
		public static final int MIN_ALL = 0x1a3;
		public static final int MIN_ALL_UNDO = 0x1a0;

		public static final User32 instance = (User32) Native.load("user32", User32.class);

		boolean EnumWindows(WndEnumProc wndenumproc, int lParam);
		//boolean IsWindowVisible(int hWnd);
		int GetWindowRect(int hWnd, RECT r);
		void GetWindowTextA(int hWnd, byte[] buffer, int buflen);
		int GetTopWindow(int hWnd);
		int GetWindow(int hWnd, int flag);
		//boolean ShowWindow(int hWnd);
		//boolean BringWindowToTop(int hWnd);
		//int GetActiveWindow();
		HWND GetForegroundWindow();
		boolean SetForegroundWindow(int hWnd);
		int FindWindowA(String winClass, String title);
		//long SendMessageA(int hWnd, int msg, int num1, int num2);
		//LRESULT SendMessageA(int hWnd, int Msg, WPARAM wParam, LPARAM lParam);
		int SendMessageA(HWND hWnd, int Msg, int wParam, int lParam);
		//int GetWindowThreadProcessId(HWND hWnd, IntByReference pref);

		static final int GW_HWNDNEXT = 2;
	}

	public static class RECT extends Structure {
		public int left, top, right, bottom;

		@Override
		protected List<String> getFieldOrder() {
			List<String> order = new ArrayList<>();
			order.add("left");
			order.add("top");
			order.add("right");
			order.add("bottom");
			return order;
		}
	}

	public static record WindowInfo(int hwnd, RECT rect, String title) {
		public String toString() {
			return String.format("(%d,%d)-(%d,%d) : \"%s\"", rect.left, rect.top, rect.right, rect.bottom, title);
		}
	}

	public static void setForegroundWindow(int hwnd) {
		User32.instance.SetForegroundWindow(hwnd);
	}

	public static void sendKey(int hwnd) {
		User32.instance.SendMessageA(User32.instance.GetForegroundWindow(), WinUser.WH_KEYBOARD, 'w', 0);
	}

	/*public static int GetActiveWindow() {
		final IntByReference pid = new IntByReference();
		return User32.instance.GetWindowThreadProcessId(User32.instance.GetForegroundWindow(), pid);
	}*/

	public static java.awt.Rectangle rectangle(WindowInfo window) {
		return new java.awt.Rectangle(window.rect.left, window.rect.top, window.rect.right - window.rect.left, window.rect.bottom - window.rect.top);
	}

	public static BufferedImage capture(WindowInfo window) {
		return capture(rectangle(window));
	}

	public static BufferedImage capture(java.awt.Rectangle rectangle) {
		try {
			return new java.awt.Robot().createScreenCapture(rectangle);
		} catch (AWTException e) {
			e.printStackTrace();
			return null;
		}
	}
}
