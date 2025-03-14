package net.w3e.app.gui.components;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

public class JWebPanel extends JScrollPane {

	private final JEditorPane pane;

	public JWebPanel(int x, int y, int w, int h, Consumer<String> click) {
		super(new JEditorPane());
		this.pane = (JEditorPane)getViewport().getView();
		this.pane.setEnabled(true);
		this.pane.setEditable(false);
		this.pane.setContentType("text/html");

		this.pane.addHyperlinkListener(e -> {
			if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
				try {
					String clickText = (String)e.getSourceElement().getAttributes().getAttribute("java-click");
					if (clickText != null) {
						click.accept(clickText);
						return;
					}
					this.open(e.getURL());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		this.setBounds(0, 0, w - 15, h);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}

	@SuppressWarnings("deprecation")
	public final void open(String file) {
		try {
			this.open(new File(file).toURL());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void open(URL link) {
		try {
			this.pane.setPage(link);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
