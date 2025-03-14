package net.w3e.app.old.api.window_old;

public class FrameWin extends AbstractFrameWin {

	public FrameWin(String frameTitle) {
		this(frameTitle, null, false);
	}

	public FrameWin(String frameTitle, AbstractFrameWin parent, boolean stop) {
		super(frameTitle, parent, stop);
	}

	@Override
	protected final void onClose() {
		onCloseIns();
		if (this.parent != null) {
			if (this.showParentWhenClose()) {
				this.parent.setLocation(this.getLocation());
				this.parent.setVisible(true);
				this.parent.setEnabled(true);
			}
		} else {
			System.exit(0);
		}
	}

	protected void onCloseIns() {}

	protected boolean showParentWhenClose() {
		return true;
	}
}
