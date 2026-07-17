package net.w3e.app.v1.u.utils.tasklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.skds.lib2.awtutils.layouts.LayoutMode;
import net.skds.lib2.awtutils.layouts.VerticalLayout;
import net.w3e.app.v1.gui.AppJFrame;

public class TaskListGui extends AppJFrame {

	public TaskListGui() {
		this.setLayout(new VerticalLayout(5, LayoutMode.FILL));

		addButton("Run", this::run);
	}

	private void run() {
		List<String> lines = getStrings();
		List<List<String>> arrays = getLists(lines);
		arrays.removeFirst();
		arrays.removeFirst();
		int value = 0;
		for (List<String> array : arrays) {
			String last = array.getLast().replace("" + ((char) 65533), "");
			value += Integer.parseInt(last);
		}
		System.out.println(value + "K");
		value /= 1024;
		System.out.println(value + "M");
		value /= 1024;
		System.out.println(value + "G");
	}

	private static List<String> getStrings() {
		List<String> lines = new ArrayList<>();

		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "chcp 437 && tasklist");
		builder.redirectErrorStream(true);
		Process p;
		try {
			p = builder.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;

		while (true) {
			try {
				line = r.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (line == null) { break; }
			lines.add(line);
		}

		lines.removeFirst();
		lines.removeFirst();
		return lines;
	}

	private static List<List<String>> getLists(List<String> lines) {
		String spaces = lines.get(1);
		int s = 0;
		IntList positions = new IntArrayList();
		while (true) {
			final int p = spaces.indexOf(' ');
			int pos = p;
			if (p == -1) {
				pos = spaces.length() - 1;
			}
			pos += 1;
			positions.add(s + pos - 1);
			s += pos;
			spaces = spaces.substring(pos);
			if (p == -1) {
				break;
			}
		}
		List<List<String>> arrays = new ArrayList<>();
		for (final String line : lines) {
			List<String> list = new ArrayList<>();
			s = 0;
			for (final int pos : positions) {
				String l = line.substring(s, pos);
				while (l.charAt(l.length() -1) == ' ') {
					l = l.substring(0, l.length() - 1);
				}
				while (l.charAt(0) == ' ') {
					l = l.substring(1);
				}
				list.add(l);
				s = pos + 1;
			}
			arrays.add(list);
		}
		return arrays;
	}

	private JButton addButton(String name, Runnable click) {
		JButton button = new JButton(name);
		button.setSize(130, 26);
		button.addActionListener(_ -> {
			click.run();
		});
		this.add(button);
		return button;
	}

}
