package net.w3e.app.old;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import net.skds.lib2.utils.Holders.IntHolder;
import net.w3e.app.old.api.window_old.AbstractFrameWin;
import net.w3e.app.old.api.window_old.FrameWin;

public abstract class FrameObject {

	private FrameWin frame;

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
		this.frame = fw;
		init(fw, args);
		return fw;
	}

	public static final void sleep(int i) {
		AbstractFrameWin.sleep(i);
	}

	protected int width = 150;
	protected int height = 26;

	protected final FrameWin getFrame() {
		return this.frame;
	}

	protected int dX() {
		return this.width + 15;
	}

	protected final int dY() {
		return this.height + 5;
	}

	protected final void simpleColumn(Container container, List<Component> list) {
		AbstractFrameWin.simpleColumn(container, list);
	}

	@Deprecated
	protected final JButton addButton(String text, Consumer<JButton> onClick, YPos y) {
		return this.addButton(text, onClick, 5, y);
	}

	@Deprecated
	protected final JButton addButton(String text, Consumer<JButton> onClick, int x, YPos y) {
		return this.addCmonent(new JButton(text), onClick, x, y);
	}

	protected static final ActionListener EMPTY_CLICK = FrameWin.onClick(() -> {});

	@Deprecated
	protected final JCheckBox addCheckBox(String text, YPos y) {
		return this.addCheckBox(text, 5, y);
	}

	@Deprecated
	protected final JCheckBox addCheckBox(String text, int x, YPos y) {
		return this.addCmonent(new JCheckBox(text), x, y);
	}

	@Deprecated
	protected final JCheckBox addCheckBox(String text, Consumer<JCheckBox> onClick, YPos y) {
		return this.addCheckBox(text, onClick, 5, y);
	}

	@Deprecated
	protected final JCheckBox addCheckBox(String text, Consumer<JCheckBox> onClick, int x, YPos y) {
		return this.addCmonent(new JCheckBox(text), onClick, x, y);
	}

	@Deprecated
	protected final <T extends Component> T addCmonent(T component, int x, YPos y) {
		component.setBounds(x, y.getValue(), this.width, this.height);
		y.increment(dY());
		FrameWin fw = this.getFrame();
		if (fw != null) {
			fw.add(component);
		}
		return component;
	}

	@Deprecated
	protected final <T extends AbstractButton> T addCmonent(T component, Consumer<T> onClick, int x, YPos y) {
		component.addActionListener(FrameWin.onClick(component, onClick));
		return this.addCmonent(component, x, y);
	}

	protected final <T extends AbstractButton> T addCmonentListiner(T component, Consumer<T> onClick) {
		component.addActionListener(FrameWin.onClick(component, onClick));
		return component;
	}

	@Deprecated
	public class YPos extends IntHolder {

		public YPos() {
			this(5);
		}

		public YPos(int y) {
			super(y);
		}

		public YPos back() {
			this.decrement(FrameObject.this.dY());
			return this;
		}

		public YPos next() {
			this.increment(FrameObject.this.dY());
			return this;
		}
	}

	public class XPos extends IntHolder {

		public XPos() {
			this(5);
		}

		public XPos(int y) {
			super(y);
		}

		public XPos back() {
			this.decrement(FrameObject.this.dX());
			return this;
		}

		public XPos next() {
			this.increment(FrameObject.this.dX());
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
			String version1 = MainFrame.version(MainFrame.version());
			String version2 = MainFrame.version(this.version());
			String version = " ";
			if (version1.length() > 0) {
				version += version1 + "#";
			}
			return version + version2;
		}
	}

	public abstract int[] version();
}
