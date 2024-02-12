package net.api.window;

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

	public static final void move(int x, int y) {
		ROBOT.mouseMove(x, y);
	}

	@SuppressWarnings("deprecation")
	public static final void click(int x, int y) {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point point = pointerInfo.getLocation();
		int xOld = (int) point.getX();
		int yOld = (int) point.getY();

		move(x, y);
		sleep(10);
		ROBOT.mousePress(InputEvent.BUTTON1_MASK);
		sleep(10);
		ROBOT.mouseRelease(InputEvent.BUTTON1_MASK);
		sleep(10);
		move(xOld, yOld);
	}

	public static final void w() {
		ROBOT.keyPress(KeyEvent.VK_W);
		sleep(100);
		ROBOT.keyRelease(KeyEvent.VK_W);
	}

	public static final void a() {
		ROBOT.keyPress(KeyEvent.VK_A);
		sleep(100);
		ROBOT.keyRelease(KeyEvent.VK_A);
	}

	public static final void s() {
		ROBOT.keyPress(KeyEvent.VK_S);
		sleep(100);
		ROBOT.keyRelease(KeyEvent.VK_S);
	}

	public static final void d() {
		ROBOT.keyPress(KeyEvent.VK_D);
		sleep(100);
		ROBOT.keyRelease(KeyEvent.VK_D);
	}

	public static final void space() {
		ROBOT.keyPress(KeyEvent.VK_SPACE);
		sleep(100);
		ROBOT.keyRelease(KeyEvent.VK_SPACE);
	}

	public static final void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}