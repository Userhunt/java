package net.api.window.jcomponent;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.border.TitledBorder;

public class TitledEmtptyBorder extends TitledBorder {
	private final int top, left, bottom, right;

	public TitledEmtptyBorder(String title) {
		this(title, 5, 10, 10, 10);
	}

	public TitledEmtptyBorder(String title, int top, int left, int bottom, int right) {
		super(title);
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	@Override
	public final Insets getBorderInsets(Component c, Insets insets) {
		insets = super.getBorderInsets(c, insets);
		insets.left += left;
		insets.top += top;
		insets.right += right;
		insets.bottom += bottom;
		return insets;
	}

	@Override
	public final Insets getBorderInsets(Component c) {
		return super.getBorderInsets(c);
	}
}