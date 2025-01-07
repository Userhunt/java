package net.w3e.app.api;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

import net.skds.lib2.utils.logger.CustomPrintStream;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.lib2.utils.logger.CustomPrintStream.Type;

public class TextAreaOutputStream extends OutputStream {

	private final JTextArea textArea;
	private final PrintStream printStream;

	public TextAreaOutputStream(JTextArea textArea) {
		this(textArea, true);
	}

	public TextAreaOutputStream(JTextArea textArea, boolean custom) {
		this.textArea = textArea;
		this.printStream = custom ? new CustomPrintStream(Type.OUT, this) {
			@Override
			protected void logLine(String string) {
				int m = string.indexOf("m");
				if (m != -1) {
					string = string.substring(m + 1);
				}
				//SKDSLogger.ORIGINAL_OUT.println("\"" + string + "\"");
				JTextArea area = TextAreaOutputStream.this.textArea;
				area.append(string);
				area.setCaretPosition(area.getDocument().getLength());
				area.update(area.getGraphics());
			}
		} : new PrintStream(this);
	}

	public final void enable() {
		SKDSLogger.attachPrintStream(this.printStream);
		//SKDSLogger.setAttachToGlobal(false);
		SKDSLogger.setAttachToFile(false);
	}

	public final void disable() {
		SKDSLogger.detachPrintStream(this.printStream);
		//SKDSLogger.setAttachToGlobal(true);
		SKDSLogger.setAttachToFile(true);
	}

	public final void print(Runnable runnable) {
		this.enable();
		runnable.run();
		this.disable();
	}

	public final void print(Object object) {
		if (object == null) {
			object = "null";
		}
		System.out.println(object);
	}

	@Override
	public final void write(int b) {
		// redirects data to the text area
		textArea.append(String.valueOf((char)b));
		// scrolls the text area to the end of data
		textArea.setCaretPosition(textArea.getDocument().getLength());
		// keeps the textArea up to date
		textArea.update(textArea.getGraphics());
	}
}
