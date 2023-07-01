package net.home.main;

import java.util.List;

import javax.swing.JButton;

import net.w3e.base.api.window.FrameWin;
import net.w3e.base.tuple.number.WIntTuple;

public abstract class FrameObject {
	
	protected final FrameWin run(FrameWin parent, List<String> args) {
		parent.setVisible(false);
		FrameWin fw = new FrameWin(getName(), parent, true) {
			@Override
			protected void onCloseIns() {
				FrameObject.this.onClose();
			}
		};
		fw.setLocation(parent.getLocation());
		fw.setSize(300, 0);
		fw.setVisible(true);
		init(fw, args);
		return fw;
	}

	public static final void sleep(int i) {
		MainFrame.sleep(i);
	}

	protected final void addButton(String text, Runnable onClick, WIntTuple y, FrameWin fw) {
		JButton button = new JButton(text);
		button.addActionListener(FrameWin.onClick(onClick));
		button.setBounds(5, y.get(), 150, 26);
		y.add(31);
		fw.add(button);
	}

	protected void onClose() {}

	protected abstract void init(FrameWin fw, List<String> args);

	public abstract String getName();

	public String fastKey() {
		return getName();
	}
}
