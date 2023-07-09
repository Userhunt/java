package net.home.main;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import net.w3e.base.api.window.FrameWin;
import net.w3e.base.tuple.number.WIntTuple;

public abstract class FrameObject {

	protected final FrameWin run(FrameWin parent, List<String> args) {
		parent.setVisible(false);
		FrameWin fw = new FrameWin(getName() + versionString(), parent, true) {
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

	protected int width = 150;
	protected int height = 26;

	protected int dX() {
		return this.width + 15;
	}

	protected final int dY() {
		return this.height + 5;
	}

	protected final JButton addButton(String text, Consumer<JButton> onClick, WIntTuple y, FrameWin fw) {
		return this.addButton(text, onClick, 5, y, fw);
	}

	protected final JButton addButton(String text, Consumer<JButton> onClick, int x, WIntTuple y, FrameWin fw) {
		return this.addCmonent(new JButton(text), onClick, x, y, fw);
	}

	protected static final ActionListener EMPTY_CLICK = FrameWin.onClick(() -> {});

	protected final JCheckBox addCheckBox(String text, WIntTuple y, FrameWin fw) {
		return this.addCheckBox(text, 5, y, fw);
	}

	protected final JCheckBox addCheckBox(String text, int x, WIntTuple y, FrameWin fw) {
		return this.addCmonent(new JCheckBox(text), x, y, fw);
	}

	protected final JCheckBox addCheckBox(String text, Consumer<JCheckBox> onClick, WIntTuple y, FrameWin fw) {
		return this.addCheckBox(text, onClick, 5, y, fw);
	}

	protected final JCheckBox addCheckBox(String text, Consumer<JCheckBox> onClick, int x, WIntTuple y, FrameWin fw) {
		return this.addCmonent(new JCheckBox(text), onClick, x, y, fw);
	}

	protected final <T extends Component> T addCmonent(T component, int x, WIntTuple y, FrameWin fw) {
		component.setBounds(x, y.get(), this.width, this.height);
		y.add(dY());
		fw.add(component);
		return component;
	}

	protected final <T extends AbstractButton> T addCmonent(T component, Consumer<T> onClick, int x, WIntTuple y, FrameWin fw) {
		component.addActionListener(FrameWin.onClick(component, onClick));
		return this.addCmonent(component, x, y, fw);
	}

	protected class YPos extends WIntTuple {

		public YPos() {
			this(5);
		}

		public YPos(int y) {
			super(y);
		}

		public YPos back() {
			this.remove(FrameObject.this.dY());
			return this;
		}

		public YPos next() {
			this.add(FrameObject.this.dY());
			return this;
		}
	}

	protected class XPos extends WIntTuple {

		public XPos() {
			this(5);
		}

		public XPos(int y) {
			super(y);
		}

		public XPos back() {
			this.remove(FrameObject.this.dX());
			return this;
		}

		public XPos next() {
			this.add(FrameObject.this.dX());
			return this;
		}
	}

	protected void onClose() {}

	protected abstract void init(FrameWin fw, List<String> args);

	public abstract String getName();

	public String fastKey() {
		return getName();
	}

	public boolean displayVersion() {
		return true;
	}

	private final String versionString() {
		if (!displayVersion()) {
			return "";
		} else {
			return " " + MainFrame.version(MainFrame.version()) + "#" + MainFrame.version(this.version());
		}
	}

	public abstract int[] version();
}
