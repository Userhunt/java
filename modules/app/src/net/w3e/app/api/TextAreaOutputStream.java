package net.w3e.app.api;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JTextArea;

import net.w3e.wlib.PrintWrapper;

public class TextAreaOutputStream extends OutputStream {

	private static final PrintStream OUT = System.out;
	private static final PrintStream ERR = System.err;

	private final JTextArea textArea;
	private final PrintStream printStream;
	private final Queue<PrintStream> out = new ConcurrentLinkedQueue<>();
	private final Queue<PrintStream> err = new ConcurrentLinkedQueue<>();

	public TextAreaOutputStream(JTextArea textArea) {
		this(textArea, true);
	}

	public TextAreaOutputStream(JTextArea textArea, boolean custom) {
		this.textArea = textArea;
		this.printStream = custom ? new PrintWrapper(this) {
			@Override
			protected final void info(String string) {
				JTextArea area = TextAreaOutputStream.this.textArea;
				area.append(string + "\n");
				area.setCaretPosition(area.getDocument().getLength());
				area.update(area.getGraphics());
			}
		} : new PrintStream(this);
	}

	public final void enable() {
		this.out.add(System.out);
		this.err.add(System.err);
		System.setOut(this.printStream);
		System.setErr(this.printStream);
	}

	public final void disable() {
		PrintStream out = TextAreaOutputStream.OUT;
		PrintStream err = TextAreaOutputStream.ERR;
		if (this.out.size() > 0) {
			out = this.out.remove();
		}
		if (this.err.size() > 0) {
			err = this.err.remove();
		}
		System.setOut(out);
		System.setErr(err);
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
		this.printStream.println(object);
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
