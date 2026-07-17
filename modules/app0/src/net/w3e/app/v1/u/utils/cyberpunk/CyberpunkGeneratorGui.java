package net.w3e.app.v1.u.utils.cyberpunk;

import javax.swing.JButton;

import net.skds.lib2.awtutils.layouts.LayoutMode;
import net.skds.lib2.awtutils.layouts.VerticalLayout;
import net.w3e.app.v1.gui.AppJFrame;
import net.w3e.app.v1.u.utils.cyberpunk.generators.CyberwareGenerator;
import net.w3e.app.v1.u.utils.cyberpunk.generators.QuickhackGenerator;
import net.w3e.app.v1.u.utils.cyberpunk.generators.ShardsGenerator;

public class CyberpunkGeneratorGui extends AppJFrame {

	private CyberpunkGenerator generator = new CyberpunkGenerator();

	public CyberpunkGeneratorGui() {
		this.setLayout(new VerticalLayout(5, LayoutMode.FILL));

		addButton("Clear", () -> {
			generator = new CyberpunkGenerator();
		});
		addButton("Cyberware", () -> {
			new CyberwareGenerator(this.generator);
		});
		addButton("Shards", () -> {
			new ShardsGenerator(this.generator);
		});
		addButton("Quickhack", () -> {
			new QuickhackGenerator(this.generator);
		});
		addButton("Save", () -> {
			this.generator.save();
		});
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
