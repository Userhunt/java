package net.w3e.app.gui;

import java.awt.GraphicsConfiguration;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.UIManager;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.app.gui.utils.JFrameGuiUtils;
import net.w3e.app.main.MainFrame.Args;

public class AppJFrame extends JFrame implements JFrameGuiUtils {

	static {
		SKDSLogger.replaceOuts();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	@Setter
	@Getter(onMethod_ = @Override)
	private JFrame parentFrame;

    public AppJFrame() {
        super();
    }

    public AppJFrame(GraphicsConfiguration gc) {
        super(gc);
    }

	public AppJFrame(String title) {
        super(title);
    }

    public AppJFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
    }

	protected void setTitleWithVersion(String title) {
		String versions = version(this.version());
		if (!versions.isBlank()) {
			title += " (" + versions + ")";
		}
		this.setTitle(title);
	}

	protected int[] version() {
		return new int[]{};
	}

	protected static final String version(int[] version) {
		if (version != null && version.length > 0) {
			IntList list = new IntArrayList(version);
			int size = list.size();
			while(size >= 0) {
				size--;
				if (list.getInt(size) <= 0) {
					list.removeInt(size);
				} else {
					break;
				}
			}
			IntListIterator iterator = list.iterator();
			StringBuilder builder = new StringBuilder();
			while(iterator.hasNext()) {
				builder.append(iterator.nextInt());
				if (iterator.hasNext()) {
					builder.append(".");
				}
			}
			return builder.toString();
		} else {
			return "";
		}
	}


	public interface AppJFrameAbstractFactory {
	}
	public interface AppJFrameFactoryMultiple extends AppJFrameAbstractFactory {
		Collection<Object> values();
	}
	public interface AppJFrameFactory extends AppJFrameAbstractFactory {
		JFrame build(JFrame parent, Args args);
	}
	public interface AppJFrameNamedFactory extends AppJFrameFactory {
		String keyName();
	}

	@AllArgsConstructor
	public static class AppJFrameNamedFactoryImpl implements AppJFrameNamedFactory {

		private final String name;
		private final AppJFrameFactory factory;

		@Override
		public String keyName() {
			return this.name;
		}

		@Override
		public JFrame build(JFrame parent, Args args) {
			return this.factory.build(parent, args);
		}
	}
}
