package net.w3e.app.gui.dungeon.registry;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.skds.lib2.awtutils.layouts.LayoutMode;
import net.skds.lib2.awtutils.layouts.VerticalLayout;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.gui.ConsoleFrame;
import net.w3e.app.gui.dungeon.DGFrame;
import net.w3e.wlib.awtutils.utils.JFrameGuiUtils;
import net.w3e.wlib.awtutils.utils.JGuiUtils;

public class DGRegistryFrame extends JFrame implements JFrameGuiUtils {

	private final DGFrame dgFrame;

	public DGRegistryFrame(DGFrame dgFrame) {
		super("Registry Frame generator");
		this.dgFrame = dgFrame;

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new VerticalLayout(5, LayoutMode.FILL));

		buttonsPanel.add(createButton("Clear", _ -> this.dgFrame.getRegistry().clear()));
		buttonsPanel.add(createButton("Load", _ -> this.dgFrame.getRegistry().load()));
		buttonsPanel.add(createButton("Save", _ -> this.dgFrame.getRegistry().save()));
		buttonsPanel.add(createButton("ToReference", _ -> this.dgFrame.getRegistry().toReference()));
		buttonsPanel.add(createButton("Delete", _ -> this.dgFrame.getRegistry().delete()));
		buttonsPanel.add(createButton("Print", _ -> {
			ConsoleFrame frame = new ConsoleFrame("Simple Console");
			frame.initScreen();
			frame.setSize(700, 500);
			frame.atRightPosition(this);
			frame.setVisible(true);

			SKDSLogger logger = frame.getLogger();
			logger.sout(this.dgFrame.getRegistry());
		}));

		this.add(buttonsPanel, BorderLayout.CENTER);
	}

	private JButton createButton(String text, ActionListener function) {
		JButton button = new JButton(text);
		JGuiUtils.setSize(button, 300, 26);
		button.addActionListener(function);
		return button;
	}

}
