package net.home.oba2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;

import com.sun.jna.platform.win32.WinDef.HWND;

import net.api.ImageUtil;
import net.api.window.FrameWin;
import net.api.window.Inputs;
import net.api.window.WUser32;
import net.api.window.WUser32.WindowInfo;
import net.api.window.jcomponent.JConsole;
import net.api.window.jcomponent.JImageLabel;
import net.api.window.jcomponent.TitledEmtptyBorder;
import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.home.oba2.OneBitState.OBAStatePredicate;
import net.home.oba2.states.LoginState;
import net.home.oba2.states.MainMenuState;
import net.home.oba2.states.PlayStateSetup;
import net.w3e.base.message.BMessageLoggerHelper;

public class OneBitAdventure extends FrameObject implements IOneBitHelper {

	public static final BMessageLoggerHelper MSG_UTIL = new BMessageLoggerHelper(LogManager.getLogger("OBA"));

	public static void main(String[] args) {
		MainFrame.register(new OneBitAdventure());
		MainFrame.run(args);
	}

	private final int imageScale = 17;

	private final JPanel left;
	private final JPanel right;

	private final JCheckBox runSetting;
	private final JCheckBox debugSetting;
	private final JCheckBox logSetting;

	private final JImageLabel map;

	private final JConsole gameStateLog;
	private final JConsole playStateLog;

	private final JConsole pathLog;
	private final JConsole rewardLog;
	private final JConsole baseLog;

	private final JImageLabel originalImage;
	private final JImageLabel grayImage;
	private final JImageLabel debugImage;

	private final JConsole console;
	private final JTextField input = new JTextField();

	private OneBitState gameState = null;
	private OneBitState waitState = null;
	private int waitTime = 0;

	private OneBitAdventure() {
		this.runSetting = new JCheckBox("Run");
		this.debugSetting = new JCheckBox("Debug");
		this.logSetting = new JCheckBox("Only Log");
		JPanel settings = FrameWin.verticalPanelBuilder().add(this.runSetting, this.debugSetting, this.logSetting).setWidth(120).build();
		settings.setBorder(new TitledEmtptyBorder("Настройки"));

		int height = 15 * this.imageScale;

		this.map = createImage(11, 15, this.imageScale);
		int w = 17;
		this.originalImage = createImage(w * this.imageScale, 15 * this.imageScale, 1);
		this.grayImage = createImage(w * this.imageScale, 15 * this.imageScale, 1);
		this.debugImage = createImage(w * this.imageScale, 15 * this.imageScale, 1);

		this.gameStateLog = new JConsole(50, height, false);
		this.gameStateLog.setBorder(new TitledEmtptyBorder("Состояние игры"));

		this.playStateLog = new JConsole(50, height, false);
		this.playStateLog.setBorder(new TitledEmtptyBorder("Состояние персонажа"));

		this.pathLog = new JConsole(273, 275, false);
		FrameWin.setSize(this.pathLog, 273, 275);
		this.rewardLog = new JConsole(273, 275, false);
		FrameWin.setSize(this.rewardLog, 273, 275);
		this.baseLog = new JConsole(345, 275, false);
		FrameWin.setSize(this.baseLog, 345, 275);

		int width = (int)(this.pathLog.getPreferredSize().getWidth() + 10 + this.rewardLog.getPreferredSize().getWidth() + 10 + this.baseLog.getPreferredSize().getWidth());
		// settings map gamestate playerstate
		JPanel first = FrameWin.horisontalPanelBuilder().add(settings, this.map, this.gameStateLog, playStateLog).setHeight(height).setMinWidth(width).build();
		// path reward base
		JPanel second = FrameWin.horisontalPanelBuilder().add(this.pathLog, this.rewardLog, this.baseLog).setHeight((int)this.pathLog.getPreferredSize().getHeight()).setMinWidth(width).build();
		// images
		JPanel third = FrameWin.horisontalPanelBuilder().add(this.originalImage, this.grayImage, this.debugImage).setHeight(height).setMinWidth(width).build();

		height = (int)(height + 10 + second.getPreferredSize().getHeight() + 10 + third.getPreferredSize().getHeight());

		this.left = FrameWin.verticalPanelBuilder().add(first, second, third).setHeight(height).setWidth(width).build();

		width = 500;
		this.console = new JConsole(width, height - 36);
		this.console.scroll.setMinimumSize(new Dimension(100, 30));

		this.input.setPreferredSize(new Dimension(width, 26));
		this.input.setMinimumSize(new Dimension(100, 26));
		this.input.setMaximumSize(new Dimension(Short.MAX_VALUE, 26));
		this.input.addActionListener(new ActionPerformed());
		this.input.addKeyListener(new KeyEsc());

		this.right = FrameWin.verticalPanelBuilder().add(this.console.scroll, this.input).setHeight(height).setWidth(width).build();
		this.right.setPreferredSize(new Dimension(width, height));
		this.right.setMinimumSize(new Dimension(100, 100));
		this.right.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
	}

	private final JImageLabel createImage(int width, int height, int scale) {
		JImageLabel image = new JImageLabel(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), width * scale, height * scale);
		image.setMinimumSize(image.getPreferredSize());
		image.setMaximumSize(image.getPreferredSize());
		image.fill(Color.WHITE);
		return image;
	}

	@Override
	protected final void init(FrameWin fw, List<String> args) {
		fw.setLayout(new BoxLayout(fw.getContentPane(), BoxLayout.X_AXIS));

		fw.add(this.left);
		fw.add(Box.createHorizontalStrut(10));
		fw.add(this.right);
		fw.pack();

		this.gameStateLog.clear();
		this.playStateLog.clear();
		this.pathLog.clear();
		this.rewardLog.clear();
		this.baseLog.clear();
		this.console.clear();

		this.gameState = null;
		this.waitState = null;
		this.waitTime = 0;

		fw.tick(500, this::tick);
	}

	private final void tick() {
		if (!this.runSetting.isSelected()) {
			return;
		}
		HWND window = WUser32.findWindow("OneBit Adventure");
		if (window == null) {
			info("Please, launch the game and press enter");
			this.gameState = null;
			this.waitState = null;
			this.waitFor(600);
			return;
		}
		WindowInfo info = WUser32.getWindowInfo(window);

		ObaHelper helper;
		try {
			helper = new ObaHelper(info.window(), info.rectangle());
			OneBitState oldState = this.gameState;

			this.gameState = this.createGameState(helper);
	
			if (this.gameState == null) {
				error("Game state is unknown " + oldState + " " + this.gameState);
				this.waitFor(50);
			} else {
				this.debug("Play " + this.gameState);
				this.gameState.play(helper);
				if (this.gameState == this.waitState) {
					this.debug("Wait for key " + this.waitState);
					this.waitFor(300);
				}
			}
			helper.save();
		} catch (Exception e) {
			error(e);
			sleep(1000);
			return;
		}
	}

	private final OneBitState createGameState(ObaHelper helper) {
		for (OBAStatePredicate predicate : new OBAStatePredicate[]{
			LoginState::test,
			MainMenuState::test,
			PlayStateSetup::test,
		}) {
			OneBitState state = predicate.test(this.gameState, helper);
			if (state != null) {
				return state;
			}
		}
		return null;
	}

	private final void waitFor(int time) {
		this.waitTime = time;
		while(waitTime > 0) {
			waitTime--;
			if (!this.runSetting.isSelected()) {
				this.waitTime = 0;
			} else {
				sleep(100);
			}
		}
	}

	@Override
	public final void info(Object str) {
		MSG_UTIL.info(str);
		this.print(str);
	}

	@Override
	public final void debug(Object str) {
		if (this.debugSetting.isSelected()) {
			MSG_UTIL.debug(str);
			this.print("[DEBUG] " + str);
		}
	}

	@Override
	public final void error(Object str) {
		MSG_UTIL.error(str);
		this.print("[ERROR] " + str);
	}

	@Override
	public final void error(Exception e) {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter( writer );
		e.printStackTrace( printWriter );
		printWriter.flush();

		String stackTrace = writer.toString();
		MSG_UTIL.error(stackTrace);
		this.print(stackTrace);
	}

	@Override
	public final void waitKey(OneBitState state) {
		this.waitState = state;
	}

	private final void print(Object str) {
		String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
		this.console.append(String.format("[%s]: %s\n", date, str));
		this.console.setCaretPosition(this.console.getDocument().getLength());
		this.console.update(this.console.getGraphics());
	} 

	private class ActionPerformed extends AbstractAction {
		@Override
		public final void actionPerformed(ActionEvent event) {
			handleInput(OneBitAdventure.this.input.getText());
		}
	}

	private class KeyEsc implements KeyListener {

		@Override
		public final void keyTyped(KeyEvent e) {}

		@Override
		public final void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				handleInput(null);
			}
		}

		@Override
		public final void keyReleased(KeyEvent e) {}
	}

	private final void handleInput(String text) {
		if (OneBitAdventure.this.waitState != null) {
			HWND self = WUser32.getActive();
			int[] pos = Inputs.mousePos();
			try {
				OneBitAdventure.this.waitState.handleKey(OneBitAdventure.this, text);
			} catch (Exception e) {
				OneBitAdventure.this.error(e);
				OneBitAdventure.this.waitState = null;
			}
			OneBitAdventure.this.waitTime = 0;
			WUser32.focus(self);
			Inputs.move(pos[0], pos[1]);
		} else if (OneBitAdventure.this.gameState == null) {
			OneBitAdventure.this.waitTime = 0;
		} else {
			OneBitAdventure.this.error(String.format("Nothing key is waited, but got \"%s\"", text));
		}
		OneBitAdventure.this.input.setText(null);
	}

	public final class ObaHelper implements IOneBitHelper {
		private final HWND window;
		private final Rectangle rectangle;
		private final HWND oldWindow;
		private final int[] oldPos;
		private BufferedImage color;
		private BufferedImage gray;

		private ObaHelper(HWND window, Rectangle rectangle) {
			this(window, rectangle, WUser32.getActive(), Inputs.mousePos());
		}

		private ObaHelper(HWND window, Rectangle rectangle, HWND oldWindow, int[] oldPos) {
			this.window = window;
			this.rectangle = rectangle;
			this.oldWindow = oldWindow;
			this.oldPos = oldPos;
			try {
				this.color = ImageUtil.capture(this.rectangle);
			} catch (Exception e) {
				error(e);
				sleep(1000);
				return;
			}
			this.gray = ImageUtil.toGRAY(this.color);
			this.applyImage();
		}

		public final ObaHelper copy() {
			OneBitState.sleep();
			return new ObaHelper(this.window, this.rectangle, this.oldWindow, this.oldPos);
		}

		public final void applyImage() {
			OneBitAdventure.this.originalImage.setImage(this.getColor());
			OneBitAdventure.this.grayImage.setImage(this.getGray());
		}

		public final BufferedImage getColor() {
			return this.color;
		}

		public final BufferedImage getGray() {
			return this.gray;
		}

		public final BufferedImage getImage(boolean gray) {
			return gray ? this.getGray() : this.getColor();
		}

		public final void focus() {
			WUser32.focus(this.window);
		}

		public final void focusBack() {
			WUser32.focus(this.oldWindow);
			Inputs.move(oldPos[0], oldPos[1]);
		}

		public final int width() {
			return this.rectangle.width;
		}
		
		public final int height() {
			return this.rectangle.height;
		}

		public final int[] getPos(double x, double y) {
			return new int[]{
				(int)(this.width() * x) + this.rectangle.x,
				(int)(this.height() * y) + this.rectangle.y,
			};
		}

		public final int[] getPosImage(double x, double y) {
			return new int[]{
				(int)(this.width() * x),
				(int)(this.height() * y),
			};
		}

		public final BufferedImage getImage(int x, int y, int width, int height) {
			return this.getImage(x, y, width, height, true);
		}

		public final BufferedImage getImage(int x, int y, int width, int height, boolean gray) {
			return ImageUtil.deepCopy(this.getImage(gray).getSubimage(x, y, width, height));
		}

		@Deprecated
		public final void crop() {
			
			this.applyImage();
		}

		public final void debugImage(BufferedImage image) {
			OneBitAdventure.this.debugImage.setImage(image);
			OneBitAdventure.this.debugImage.repaint();
		}

		public final boolean isPlay() {
			return !OneBitAdventure.this.logSetting.isSelected();
		}

		@Override
		public final void info(Object str) {
			OneBitAdventure.this.info(str);
		}

		@Override
		public final void debug(Object str) {
			OneBitAdventure.this.debug(str);
		}

		@Override
		public final void error(Object str) {
			OneBitAdventure.this.error(str);
		}

		@Override
		public final void error(Exception e) {
			OneBitAdventure.this.error(e);
		}

		@Override
		public final void waitKey(OneBitState state) {
			OneBitAdventure.this.waitKey(state);
		}

		public final void save() {
			ImageUtil.save(this.getColor(), "oba/source");
			ImageUtil.save(this.getGray(), "oba/gray");
			ImageUtil.save(OneBitAdventure.this.debugImage.getImage(), "oba/debug");
		}
	}

	@Override
	public final String getName() {
		return "One Bit Adventure";
	}

	@Override
	public final String fastKey() {
		return "OBA";
	}

	@Override
	public final int[] version() {
		return new int[]{1,0,0};
	}
}
