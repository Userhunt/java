package net.w3e.app.gui.components;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import lombok.NoArgsConstructor;
import net.w3e.app.gui.utils.JComponentGuiUtils;
import net.w3e.app.gui.utils.JImageGuiUtils;

@NoArgsConstructor
public abstract class JImagePanel extends JPanel implements JComponentGuiUtils, JImageGuiUtils {

	protected BufferedImage image;

	public JImagePanel(BufferedImage image) {
		setImage(image);
	}

	@Override
	public final BufferedImage getImage() {
		return this.image;
	}

	@Override
	public void setImage(BufferedImage image) {
		if (image != null) {
			this.image = image;
			this.repaint();
		}
	}

	@Override
	public void updateImage() {
		this.repaint();
	}

	@Override
	protected final void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.image != null) {
			paintImage(g);
		}
	}

	protected abstract void paintImage(Graphics g);
}
