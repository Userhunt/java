package net.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

import net.w3e.base.PrintWrapper;

public class CustomOutputStream extends OutputStream {
	private final JTextArea textArea;
	private final PrintStream printStream;
	private boolean enabled;
	private PrintStream out = System.out;
	private PrintStream err = System.err;

	public CustomOutputStream(JTextArea textArea) {
		this(textArea, true);
	}

	public CustomOutputStream(JTextArea textArea, boolean custom) {
		this.textArea = textArea;
		this.printStream = custom ? new PrintWrapper(this) {
			protected void info(String string) {
				JTextArea area = CustomOutputStream.this.textArea;
				area.append(string + "\n");
				area.setCaretPosition(area.getDocument().getLength());
				area.update(area.getGraphics());
			}
		} : new PrintStream(this);
	}

	public final void enable() {
		if (this.enabled) {
			return;
		}
		out = System.out;
		err = System.err;
		this.enabled = true;
		System.setOut(this.printStream);
		System.setErr(this.printStream);
	}

	public final void disable() {
		if (!this.enabled) {
			return;
		}
		this.enabled = false;
		System.setOut(this.out);
		System.setErr(this.err);
	}

	public final void print(Runnable runnable) {
		boolean enabled = this.enabled;
		if (!enabled) {
			this.enable();
		}
		runnable.run();
		if (!enabled) {
			this.disable();
		}
	}

	@Override
	public void write(int b) throws IOException {
		// redirects data to the text area
		textArea.append(String.valueOf((char)b));
		// scrolls the text area to the end of data
		textArea.setCaretPosition(textArea.getDocument().getLength());
		// keeps the textArea up to date
		textArea.update(textArea.getGraphics());
	}
}
