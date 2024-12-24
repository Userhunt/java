package net.w3e.app.api.window;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Inputs {

	protected static final Robot ROBOT;
	static {
		Robot robot;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			robot = null;
		}
		ROBOT = robot;
	}

	public static final int[] mousePos() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point point = pointerInfo.getLocation();
		int xOld = (int)point.getX();
		int yOld = (int)point.getY();
		return new int[] {xOld, yOld};
	}

	public static final void move(int x, int y) {
		ROBOT.mouseMove(x, y);
	}

	public static final void drag(int x, int y) {
		int[] pos = mousePos();
		drag(pos[0], pos[1], x, y);
	}

	public static final void drag(int x1, int y1, int x2, int y2) {
		drag(x1, y1, x2, y2, 50);
	}

	public static final void drag(int x1, int y1, int x2, int y2, int speed) {
		int[] pos = mousePos();

		move(x1, y1);
		ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);

		int x = x1;
		int y = y1;
		int dx = x1 > x2 ? -1 : 1;
		int dy = y1 > y2 ? -1 : 1;

		sleep(10);
		while(x != x2 || y != y2) {
			for (int i = 0; i < speed; i++) {
				if (x != x2) {
					x += dx;
				}
				if (y != y2) {
					y += dy;
				}
			}
			move(x, y);
			sleep(10);
		}
		sleep(10);

		ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		move(pos[0], pos[1]);
	}

	public static final void click() {
		sleep(20);
		ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		sleep(20);
		ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		sleep(20);
	}

	public static final void click(int x, int y) {
		int[] pos = mousePos();

		move(x, y);
		click();
		move(pos[0], pos[1]);
	}

	public static final void w() {
		key(KeyEvent.VK_W);
	}

	public static final void a() {
		key(KeyEvent.VK_A);
	}

	public static final void s() {
		key(KeyEvent.VK_S);
	}

	public static final void d() {
		key(KeyEvent.VK_D);
	}

	public static final void space() {
		key(KeyEvent.VK_SPACE);
	}

	public static final void enter() {
		key(KeyEvent.VK_ENTER);
	}

	public static final void esc() {
		key(KeyEvent.VK_ESCAPE);
	}

	public static final void key(int key) {
		ROBOT.keyPress(key);
		sleep(100);
		ROBOT.keyRelease(key);
	}

	public static final void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}