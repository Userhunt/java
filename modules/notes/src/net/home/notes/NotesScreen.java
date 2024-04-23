package net.home.notes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JScrollPane;

import com.google.gson.JsonObject;

import net.api.window.FrameWin;
import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.w3e.base.json.FileUtil;

public class NotesScreen extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new NotesScreen());
		MainFrame.run(args);
	}

	private static final int w = 600;
	private static final int h = 400;

	private NoteFolder root;
	private JScrollPane pane;

	protected final void init(FrameWin fw, List<String> args) {
		this.root = new NoteFolder();
		this.pane = this.createPane();

		JsonObject json = FileUtil.readObject("notes/root.json");
		if (json != null) {
			this.root = (NoteFolder)Note.load("root", json);
		}
		this.update(this.pane, this.root);

		fw.add(this.pane);
		fw.setSize(w, h);
	}

	private final JScrollPane createPane() {
		JScrollPane pane = new JScrollPane();
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setBounds(0, 30, w - 15, h - 30 - 35);
		return pane;
	}

	private final void update(JScrollPane pane, NoteFolder folder) {
		Map<String, NoteFile> files = new LinkedHashMap<>();
		for (Entry<String, Note> entry : folder.files().entrySet()) {
			Note value = entry.getValue();
			if (value instanceof NoteFile file) {
				files.put(entry.getKey(), file);
			} else {
				
			}
		}
	}

	@Override
	public final String getName() {
		return "Notes";
	}

	@Override
	public final String fastKey() {
		return "notes";
	}

	@Override
	public final int[] version() {
		return new int[]{1,0,0};
	}
}
