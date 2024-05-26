package net.api.window;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser.WINDOWINFO;

public class WUser32 {
 
	public static final User32 INSTANCE = User32.INSTANCE;

	public static final HWND findWindow(String title) {
		return INSTANCE.FindWindow(null, title);
	}

	public static final HWND getActive() {
		return INSTANCE.GetForegroundWindow();
	}

	public static final void focus(HWND window) {
		INSTANCE.SetFocus(window);
		INSTANCE.SetForegroundWindow(window);
	}

	public static final WindowInfo getWindowInfo(HWND window) {
		WINDOWINFO info = new WINDOWINFO();
		INSTANCE.GetWindowInfo(window, info);

		User32.RECT r = info.rcClient;

		char[] buffer = new char[1024];
		INSTANCE.GetWindowText(window, buffer, buffer.length);
		String title = Native.toString(buffer);
		return new WindowInfo(window, title, r, new Rectangle(r.left, r.top, r.right - r.left, r.bottom - r.top));
	}

	public static record WindowInfo(HWND window, String title, User32.RECT points, Rectangle rectangle) {
		public String toString() {
			return String.format("(%d,%d)-(%d,%d) : \"%s\", %s", points.left, points.top, points.right, points.bottom, this.title, this.rectangle);
		}

		public WindowInfo recreare() {
			return getWindowInfo(this.window);
		}
	}

	public static final Map<HWND, String> getWindows() {
		Map<HWND, String> map = new HashMap<HWND, String>();
		User32.INSTANCE.EnumWindows(new User32.WNDENUMPROC() {
			@Override
			public boolean callback(HWND hWnd, Pointer arg1) {
				map.put(hWnd, getWindowInfo(hWnd).title);
				return true;
			}
		}, null);
		return map;
	}

	/*public static final void drag(int x1, int y1, int x2, int y2) {
		mouseAction(x1, y1, 1);
		mouseAction(x1, y1, 2);
		mouseAction(x2, y2, 1);
		mouseAction(x2, y2, 4);
	}

	private static void mouseAction(int x, int y, int flags) {
        INPUT input = new INPUT();
		POINT pos;
    	GetCursorPos(&pos);

		input.type = new DWORD(INPUT.INPUT_MOUSE);
		input.input.setType("mi");
		input.input.mi.dx = (pos.x + x)*(65536.0f / GetSystemMetrics(SM_CXSCREEN));
    	input.input.mi.dy = (pos.y + y)*(65536.0f / GetSystemMetrics(SM_CYSCREEN));
		input.input.mi.time = new DWORD(0);
		input.input.mi.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
		input.input.mi.dwFlags = new DWORD(flags);
		User32.INSTANCE.SendInput(new DWORD(1), new INPUT[]{input}, input.size());
    }*/

	/*
	public static final int MOD_ALT = 0x0001;
	public static final int MOD_CONTROL = 0x0002;
	public static final int MOD_SHIFT = 0x0004;
	public static final int MOD_WIN = 0x0008;
	public static final int WM_HOTKEY = 0x0312;

	public static final boolean RegisterHotKey(WinDef.HWND hWnd, int id, int fsModifiers, int vk) {
		return User32.INSTANCE.RegisterHotKey(hWnd, id, fsModifiers, vk);
	}
	public static final boolean UnregisterHotKey(Pointer hWnd, int id) {
		return User32.INSTANCE.UnregisterHotKey(hWnd, id);
	}
	public static final boolean PeekMessage(WinUser.MSG lpMsg, WinDef.HWND hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg) {
		return User32.INSTANCE.PeekMessage(lpMsg, hWnd, wMsgFilterMin, wMsgFilterMax, wRemoveMsg);
	}*/

	/*public static boolean isKeyDown(int vkCode) {
		short state = INSTANCE.GetAsyncKeyState(vkCode);
		// check most-significant bit for non-zero.
		return (0x1 & (state >> (Short.SIZE - 1))) != 0;
	}*/

	/*private static final INPUT createInput(int c, int type) {
		INPUT input = new INPUT();
		input.type = new WinDef.DWORD(INPUT.INPUT_KEYBOARD);
		input.input.setType("ki");
		input.input.ki.wScan = new WinUser.WORD(0);
		input.input.ki.time = new WinUser.DWORD(0);
		input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
		input.input.ki.wVk = new WinUser.WORD(c);
		input.input.ki.dwFlags = new WinUser.DWORD(type);
		return input;
	}

	public static final void sendKeyDown(int c) {
		WinUser.INPUT input = createInput(c, 0);
		INSTANCE.SendInput(new WinUser.DWORD(1), (INPUT[]) input.toArray(1), input.size());
	}

	public static final void sendKeyUp(int c) {
		WinUser.INPUT input = createInput(c, 2);
		INSTANCE.SendInput(new WinUser.DWORD(1), (INPUT[]) input.toArray(1), input.size());
	}*/
}