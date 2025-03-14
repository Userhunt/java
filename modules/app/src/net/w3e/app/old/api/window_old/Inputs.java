package net.w3e.app.old.api.window_old;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import net.w3e.lib.utils.RobotUtils;

public class Inputs extends RobotUtils {

	public static final void drag(int x, int y) {
		int[] pos = mousePos();
		drag(pos[0], pos[1], x, y);
	}

	public static final void drag(int x1, int y1, int x2, int y2) {
		drag(x1, y1, x2, y2, 50);
	}

	public static final void drag(int x1, int y1, int x2, int y2, int speed) {
		int[] pos = mousePos();

		mouseMove(x1, y1);
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
			mouseMove(x, y);
			sleep(10);
		}
		sleep(10);

		ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

		mouseMove(pos[0], pos[1]);
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

		mouseMove(x, y);
		click();
		mouseMove(pos[0], pos[1]);
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

	public static final void ctrl(Runnable run) {
		ROBOT.keyPress(KeyEvent.VK_CONTROL);
		int sleep = 10;
		sleep(sleep);
		run.run();
		sleep(sleep);
		ROBOT.keyRelease(KeyEvent.VK_CONTROL);
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