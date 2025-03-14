package net.w3e.app.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import lombok.CustomLog;
import net.skds.lib2.io.CustomAbstractPrintStream;
import net.skds.lib2.utils.AnsiEscape;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.gui.AppJFrame;
import net.w3e.app.gui.components.JConsoleTextArea;

@CustomLog
public class ConsoleFrame extends AppJFrame {

	private static final OutputStream STREAM = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			throw new UnsupportedOperationException();
		}
	};

	private final AtomicInteger enabled = new AtomicInteger(0);
	protected final ConsoleOutputStream printStream = new ConsoleOutputStream();

	protected final JConsoleTextArea textArea = new JConsoleTextArea(1100, 700);

	public ConsoleFrame(String title) {
		setTitleWithVersion(title);
		this.addCloseEvent(_ -> {
			disableConsole();
		});

		this.setLayout(new BorderLayout());

		this.add(this.textArea.getAddComponent(), BorderLayout.CENTER);
		this.textArea.setBackground(new Color(30, 30, 30));
	}

	public void enableConsole() {
		int a = this.enabled.incrementAndGet();
		//log.debug("enable " + a + " " + Thread.currentThread().getStackTrace()[2]);
		if (a > 1) {
			return;
		}
		SKDSLogger.attachPrintStream(this.printStream);
		//log.debug("attach " + a);
	}

	public void disableConsole() {
		int a = this.enabled.accumulateAndGet(0, (prev, _) -> {
			return Math.max(0, prev - 1);
		});
		//log.debug("disable " + a + " " + Thread.currentThread().getStackTrace()[2]);
		if (a > 0) {
			return;
		}
		//log.debug("detach " + a);
		SKDSLogger.detachPrintStream(this.printStream);
	}

	private static final Map<String, Color> FOREGROUND = new HashMap<>();

	static {
		FOREGROUND.put(String.valueOf(AnsiEscape.BLACK.getCode()), Color.BLACK);
		FOREGROUND.put(String.valueOf(AnsiEscape.RED.getCode()), Color.RED);
		FOREGROUND.put(String.valueOf(AnsiEscape.GREEN.getCode()), new Color(13, 188, 121));
		FOREGROUND.put(String.valueOf(AnsiEscape.YELLOW.getCode()), new Color(229, 229, 16));
		FOREGROUND.put(String.valueOf(AnsiEscape.BLUE.getCode()), new Color(36, 114, 200));
		FOREGROUND.put(String.valueOf(AnsiEscape.MAGENTA.getCode()), Color.MAGENTA);
		FOREGROUND.put(String.valueOf(AnsiEscape.CYAN.getCode()), Color.CYAN);
		FOREGROUND.put(String.valueOf(AnsiEscape.WHITE.getCode()), Color.WHITE);

		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_BLACK.getCode()), new Color(102, 102, 102));
		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_RED.getCode()), new Color(241, 76, 76));
		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_GREEN.getCode()), Color.GREEN.brighter());
		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_YELLOW.getCode()), Color.YELLOW.brighter());
		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_BLUE.getCode()), Color.BLUE.brighter());
		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_MAGENTA.getCode()), new Color(214, 112, 214));
		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_CYAN.getCode()), Color.CYAN.brighter());
		FOREGROUND.put(String.valueOf(AnsiEscape.BRIGHT_WHITE.getCode()), Color.WHITE.brighter());
	}

	private class ConsoleOutputStream extends CustomAbstractPrintStream {
	
		public ConsoleOutputStream() {
			super(STREAM);
		}

		@Override
		protected void logLine(String x, boolean ln) {
			if (x.isEmpty()) {
				return;
			}

			StyledDocument doc = textArea.getStyledDocument();

			SimpleAttributeSet keyWord = new SimpleAttributeSet();

			List<LineData> lines = new ArrayList<>();

			while (true) {
				int index = x.indexOf(AnsiEscape.getStartKeyCodeStyle());
				
				if (index != -1) {
					if (index == 0) {
						index = x.indexOf("m") + 1;
						String ansi = x.substring(2, index - 1);
						x = x.substring(index);
						
						if (ansi.equals(AnsiEscape.getDefaultStyle())) {
							keyWord = new SimpleAttributeSet();
							continue;
						}
						if (ansi.equals(String.valueOf(AnsiEscape.NORMAL.code))) {
							keyWord = new SimpleAttributeSet();
							continue;
						}
						Color color = FOREGROUND.get(ansi);
						if (color != null) {
							StyleConstants.setForeground(keyWord, color);
							continue;
						}
					} else {
						lines.add(new LineData(x.substring(0, index), keyWord));
						x = x.substring(index);
						keyWord = new SimpleAttributeSet();
					}
					//StyleConstants.setForeground(keyWord, Color.RED);
					//StyleConstants.setBackground(keyWord, Color.YELLOW);
					//StyleConstants.setBold(keyWord, true);
				} else {
					lines.add(new LineData(x, keyWord));
					break;
				}
			}

			for (LineData lineData : lines) {
				try {
					doc.insertString(doc.getLength(), lineData.line, lineData.keyWord);
				} catch (Exception e) {
					e.printStackTrace(SKDSLogger.ORIGINAL_OUT);
				}
			}
			textArea.setCaretPosition(doc.getLength());
		}

		private record LineData(String line, SimpleAttributeSet keyWord) {}

		/*@Override
		public void println() {
			logLine("", true);
		}*/
	}
}
