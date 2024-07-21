package net.api;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JTextArea;

import net.w3e.base.PrintWrapper;

public class CustomOutputStream extends OutputStream {

	private static final PrintStream OUT = System.out;
	private static final PrintStream ERR = System.err;

	private final JTextArea textArea;
	private final PrintStream printStream;
	private Queue<PrintStream> out = new ConcurrentLinkedQueue<>();
	private Queue<PrintStream> err = new ConcurrentLinkedQueue<>();

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
		this.out.add(System.out);
		this.err.add(System.err);
		System.setOut(this.printStream);
		System.setErr(this.printStream);
	}

	public final void disable() {
		PrintStream out = CustomOutputStream.OUT;
		PrintStream err = CustomOutputStream.ERR;
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
