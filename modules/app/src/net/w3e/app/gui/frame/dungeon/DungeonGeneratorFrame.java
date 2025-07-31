package net.w3e.app.gui.frame.dungeon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.LongStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.skds.lib2.awtutils.layouts.HorizontalLayout;
import net.skds.lib2.awtutils.layouts.LayoutMode;
import net.skds.lib2.awtutils.layouts.VerticalLayout;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.AnsiEscape;
import net.skds.lib2.utils.logger.SKDSLoggerConfig;
import net.w3e.app.gui.AppJFrame;
import net.w3e.app.gui.frame.JImageFrame;
import net.w3e.app.gui.frame.ProgressFrame;
import net.w3e.app.gui.frame.dungeon.layers.TestLayers;
import net.w3e.app.gui.utils.JFrameGuiUtils;
import net.w3e.app.gui.utils.JGuiUtils;
import net.w3e.app.old.api.window_old.FrameWin;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.DungeonGeneratorResult;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonLayer.IPathLayer;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.DungeonJsonAdaptersString;
import net.w3e.wlib.dungeon.json.DungeonKeySupplier;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.ListLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.mat.WBoxI;

@CustomLog
public class DungeonGeneratorFrame extends AppJFrame {

	private final Change CHANGE = new Change();
	static {
		TestLayers.init();
		DungeonJsonAdaptersString.initString();
	}

	private final JCheckBox showWallBox = new JCheckBox("Wall");
	private final JCheckBox showSoftPathBox = new JCheckBox("Soft Path");
	private final JCheckBox showTemperatureBox = new JCheckBox("Temperature");
	private final JCheckBox showWetBox = new JCheckBox("Wet");
	private final JCheckBox showBiomeBox = new JCheckBox("Biome");
	private final JCheckBox fastBox = new JCheckBox("Fast");
	private final JCheckBox printBox =  new JCheckBox("Print", true);
	private final JCheckBox rotateBox = new JCheckBox("Rotate");
	private final JCheckBox debugSaveBox = new JCheckBox("Debug Save");

	private final ConcurrentLinkedQueue<DungeonImagePainter> dungeons = new ConcurrentLinkedQueue<>();

	private int processId = 0;

	public DungeonGeneratorFrame() {
		setTitleWithVersion("Dungeon Generator");

		JComboBox<DungeonGeneratorDebug.Mode> box = new JComboBox<>(DungeonGeneratorDebug.Mode.values());

		this.add(box, BorderLayout.NORTH);

		//JPanel container = new JPanel();
		//container.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new HorizontalLayout(5, LayoutMode.CENTER));

		List<JComponent> settings = new ArrayList<>();

		this.showWallBox.addItemListener(CHANGE);
		settings.add(this.showWallBox);

		this.showSoftPathBox.addItemListener(CHANGE);
		settings.add(this.showSoftPathBox);

		this.showTemperatureBox.addItemListener(CHANGE);
		settings.add(this.showTemperatureBox);

		this.showWetBox.addItemListener(CHANGE);
		settings.add(this.showWetBox);

		this.showBiomeBox.addItemListener(CHANGE);
		settings.add(this.showBiomeBox);

		this.rotateBox.addItemListener(CHANGE);
		settings.add(this.rotateBox);

		this.fastBox.addItemListener(CHANGE);
		settings.add(this.fastBox);

		this.printBox.addItemListener(CHANGE);
		settings.add(this.printBox);

		this.debugSaveBox.addItemListener(CHANGE);
		settings.add(this.debugSaveBox);


		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new VerticalLayout(8, LayoutMode.FILL));

		for (JComponent jComponent : settings) {
			settingsPanel.add(jComponent);
		}

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new VerticalLayout(5, LayoutMode.FILL));

		for (JComponent jComponent : LongStream
			.range(0, settings.size() - 0)
			.mapToObj(seed -> this.createButton(String.valueOf(seed), e -> {
				Direction direction = this.rotateBox.isSelected() ? Direction.WEST : Direction.SOUTH;
				boolean debugSave = this.debugSaveBox.isSelected();
				DungeonGeneratorDebug.Mode mode = DungeonGeneratorDebug.Mode.values()[box.getSelectedIndex()];
				this.creteDungeon(
					((JButton)e.getSource()).getText(),
					DungeonGeneratorDebug.example(seed, direction, debugSave, mode)
				);
			}))
			.toList()) {
			buttonsPanel.add(jComponent);
		}

		panel.add(buttonsPanel);
		panel.add(settingsPanel);

		this.add(panel, BorderLayout.CENTER);

		this.addCloseEvent(_ -> {
			this.processId = Integer.MAX_VALUE;
			for (DungeonImagePainter painter : this.dungeons) {
				painter.dispose();
			}
		});
	}

	private final void creteDungeon(String name, DungeonGenerator dungeon) {
		this.processId++;
		ProgressFrame progressFrame = new ProgressFrame("Dungeon Generator: " + name + "(" + this.processId + ")");

		progressFrame.initScreen();

		WBoxI dimension = dungeon.dimension();
		Vec3I size = dimension.size().addI(Vec3I.SINGLE);

		DungeonImagePainter imageFrame = new JImageFrame.JImageScreenBuilder().setLocationUnder(this).setSize(size.xi() * 4 + 1, size.zi() * 4 + 1).setScale(9).setTitle(name).buildWith((frameTitle, width, height, scale, background) -> 
			new DungeonImagePainter(frameTitle, width, height, scale, background, dungeon, progressFrame)
		);
		this.dungeons.add(imageFrame);

		int x = this.getX() + this.getWidth();
		progressFrame.setLocation(FastMath.clamp(imageFrame.getX() + imageFrame.getWidth(), x, x + 2000) - 5, this.getY());
		progressFrame.setSize(new Dimension(progressFrame.getWidth(), Math.min(imageFrame.getY() + imageFrame.getHeight(), 1000 - this.getY())));

		progressFrame.setVisible(true);

		final int id = this.processId;
		final LinkedBlockingQueue<Boolean> future = new LinkedBlockingQueue<>();

		progressFrame.addTask((_) -> {
			boolean old = future.take();
			if (old) {
				return imageFrame.result.progress();
			} else {
				return 1;
			}
		});

		new Thread(new DGProcess(dungeon, id, future, progressFrame, imageFrame)).start();
	}

	@RequiredArgsConstructor
	private class DGProcess implements Runnable {

		private int layerId = 1;
		private int layerStep = 0;
		private long lastPaintTime = 0;

		private final DungeonGenerator generator;
		private final int id;
		private final LinkedBlockingQueue<Boolean> future;
		private final ProgressFrame progressFrame;
		private final DungeonImagePainter imageFrame;

		@Override
		public void run() {
			try {
				System.out.println(runSafe().get() != null ? "done" : "not done");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		public CompletableFuture<DungeonGeneratorResult> runSafe() {
			return generator.generateAsync(result -> {
				if (DungeonGeneratorFrame.this.processId == id && result != null) {
					if (progressFrame.isPaused()) {
						synchronized (progressFrame) {
							progressFrame.wait();
						}
					}
					if (progressFrame.isStop()) {
						return CompletableFuture.completedFuture(false);
					}
					progressFrame.enableConsole();

					boolean fast = fastBox.isSelected();
					long now = System.currentTimeMillis();
					boolean print = !fast || result.progress() >= 1 || now + 1000 <= this.lastPaintTime;
					if (print) {
						this.lastPaintTime = now;
					}

					if (imageFrame.result != null && imageFrame.result.lastLayer() != result.lastLayer()) {
						layerStep = 0;
						layerId++;
						if (printBox.isSelected() && layerId != 2 && print) {
							System.out.println();
						}
					}
					this.layerStep++;

					imageFrame.result = result;
					future.add(true);

					int[] data = null;
					//if (print) {
						data = imageFrame.repaintGui();
					//}

					if (printBox.isSelected()) {
						printProgress(data, result);
					}

					if (!fast) {
						Thread.sleep(1000);
					}
					progressFrame.disableConsole();
					return CompletableFuture.completedFuture(true);
				}
				future.add(false);
				return CompletableFuture.completedFuture(false);
			});
		}

		private void printProgress(int[] data, DungeonGeneratorResult result) {
			if (!printBox.isSelected()) {
				return;
			}

			DungeonLayer layer = result.lastLayer();

			if (layer instanceof IPathLayer) {
				this.printString("path", String.format("paths %s, connections:[%s,%s]", data[0], data[1], data[2]));
				return;
			}
			if (layer instanceof ListLayer list) {
				String listProgress = String.format(" %s/%s", list.size(), list.filled());
				String roomsProgress = "rooms" + listProgress;

				String layerName = null;
				String arg = roomsProgress;

				if (list instanceof DistanceLayer) {
					layerName = "distance";
					arg = "entrances" + listProgress;
				}
				if (list instanceof BiomeLayer) {
					layerName = "terra/biome";
					arg = "biomes" + listProgress;
				}
				if (list instanceof CompositeTerraLayer) {
					layerName = "terra/composite";
				}
				if (list instanceof RoomLayer) {
					layerName = "room";
				}
				if (list instanceof FeatureLayer) {
					layerName = "feature";
				}
				if (layerName != null) {
					if (list.size() == -1) {
						this.printString(layerName, "setup");
					} else {
						this.printString(layerName, arg);
					}
					return;
				}
				this.printString("list/unhandled", layer.getClass().getSimpleName());
				return;
			}
			if (layer instanceof ClearLayer) {
				this.printString("clear walls", layer.getClass().getSimpleName());
				return;
			}
			this.printString("unhandled", layer.getClass().getSimpleName());
		}

		private void printString(String name, String args) {
			SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
			String time = config.getTimeFormat().format(new Date(System.currentTimeMillis()));
			String message = String.format("[%s/%s](%s) %s", layerId, layerStep, name, args);
			System.out.print(AnsiEscape.BLUE.sequence + time + " " + AnsiEscape.BRIGHT_MAGENTA.sequence + message + AnsiEscape.DEFAULT.sequence + "\n");
		}
	}

	private JButton createButton(String text, ActionListener function) {
		JButton button = new JButton(text);
		JGuiUtils.setSize(button, 300, 26);
		button.addActionListener(function);
		return button;
	}

	private void repaintGui() {
		for (DungeonImagePainter painter : this.dungeons) {
			painter.repaintGui();
		}
	}

	private class DungeonImagePainter extends JImageFrame implements MouseListener {

		private final int scale;
		private final Vec3I size;
		private final Vec3I min;
		private final List<JFrame> windows = new ArrayList<>();

		public DungeonGeneratorResult result; 

		private DungeonImagePainter(String frameTitle, int width, int height, int scale, Color background, DungeonGenerator dungeon, ProgressFrame progressFrame) {
			super(frameTitle, width, height, scale, background);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.scale = scale * 4;
			WBoxI dimension = dungeon.dimension();
			this.size = dimension.size().addI(Vec3I.SINGLE);
			this.min = dimension.min().inverseI();

			this.setVisible(true);
			this.imagePanel.addMouseListener(this);

			// TODO не вызывается когда закрываю главный фрейм
			addCloseEvent(_ -> {
				DungeonGeneratorFrame.this.dungeons.remove(this);
				progressFrame.stopAndWait();
				progressFrame.dispose();
			});
		}

		public int[] repaintGui() {
			int[] data = this.refillImage();
			this.updateImage();
			return data;
		}

		private int[] refillImage() {
			int[] data = new int[]{0,0,0};

			final DungeonGeneratorFrame frame = DungeonGeneratorFrame.this;

			boolean showWall = frame.showWallBox.isSelected();
			boolean showSoftPath = frame.showSoftPathBox.isSelected();
			boolean showTemperature = frame.showTemperatureBox.isSelected();
			boolean showWet = frame.showWetBox.isSelected();
			boolean showBiome = frame.showBiomeBox.isSelected();

			this.fillImageWhite();

			int xS = this.size.xi();
			int zS = this.size.zi();

			// border
			{
				for (int x = 0; x < xS; x++) {
					for (int z = 0; z < zS; z++) {
						for (int i = 0; i < 4; i++) {
							for (int j = 0; j < 4; j++) {
								if (i == 0 || j == 0) {
									this.setColor(x * 4 + i, z * 4 + j, Color.GRAY);
								}
							}
						}
					}
				}
				xS *= 4;
				zS *= 4;
				for (int x = 0; x < xS; x++) {
					this.setColor(x, zS, Color.GRAY);
				}
				for (int z = 0; z < zS; z++) {
					this.setColor(xS, z, Color.GRAY);
				}
				this.setColor(xS, zS, Color.GRAY);

			}
			if (this.result == null) {
				return data;
			}

			xS -= 4;
			zS -= 4;

			for (Map<Vec3I, DungeonRoomInfo> entry1 : this.result.chunks().values()) {
				data[0] += entry1.size();
				for (Entry<Vec3I, DungeonRoomInfo> entry2 : entry1.entrySet()) {
					Vec3I pos = entry2.getKey();
					int x = (pos.xi() + min.xi()) * 4 + 2;
					int z = zS - (pos.zi() + min.zi()) * 4 + 2;
					try {
						DungeonRoomInfo value = entry2.getValue();

						if (showTemperature) {
							int temp = value.data().getInt("temperature");
							if (temp < 0) {
								this.setColor(x - 1, z - 1, Color.BLUE);
							} else if (temp > 0) {
								this.setColor(x- 1, z - 1, Color.ORANGE);
							}
						}
						if (showWet) {
							int wet = value.data().getInt("wet");
							if (wet < 50) {
								this.setColor(x + 1, z - 1, Color.CYAN);
							} else if (wet > 0) {
								this.setColor(x + 1, z - 1, Color.YELLOW);
							}
						}
						if (showBiome) {
							try {
								DungeonKeySupplier biome = value.data().getT(BiomeLayer.KEY);
								if (biome != null && !biome.getRaw().equals("void")) {
									this.setColor(x + 1, z + 1, Color.MAGENTA);
								}
							} catch (Exception e) {
								e.printStackTrace();
								showBiome = false;
								frame.showBiomeBox.setSelected(false);
							}
						}

						Color color = Color.LIGHT_GRAY;
						if (value.isEntrance()) {
							color = Color.DARK_GRAY.brighter();
						} else {
							if (!value.isWall()) {
								color = Color.BLACK;
							}
						}
						if (showWall || color != Color.LIGHT_GRAY) {
							this.setColor(x, z, color);
						}
						for (Direction direction : Direction.values()) {
							if (direction != Direction.UP && direction != Direction.DOWN) {
								Vec3I relative = direction.getOffset();
								if (value.isConnect(direction, true)) {
									data[1]++;
									this.setColor(x + relative.xi(), z - relative.zi(), Color.RED);
								} else if (value.isConnect(direction, false)) {
									data[2]++;
									if (showSoftPath) {
										this.setColor(x + relative.xi(), z - direction.getOffset().zi(), Color.GREEN);
									}
								}
							}
						}
					} catch (Exception e) {
						throw e;
					}
				}
			}
			return data;
		}

		@Override
		public void mouseClicked(MouseEvent event) {
			int x = event.getX() / this.scale - this.min.xi();
			int z = event.getY() / this.scale - this.min.zi();
			//x *= -1;
			z *= -1;
			DungeonRoomCreateInfo data = result.get(new Vec3I(x, 0, z));
			if (!data.exists()) {
				System.err.println("not found");
			} else {
				DungeonRoomInfo room = data.room();

				JFrame frame = new JFrame("Room Info");
				JGuiUtils.addBorder(frame.getRootPane(), 10);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				JFrameGuiUtils.initScreen(frame, null);
				JFrameGuiUtils.addCloseEvent(frame, _ -> {
					frame.setEnabled(false);
				});

				frame.setLocation(event.getXOnScreen() - (event.getXOnScreen() % this.scale) + 18, event.getYOnScreen() - (event.getYOnScreen() % this.scale) + 18);

				List<JLabel> list = room.displayString().stream().map(e -> {
					JLabel label = new JLabel(e);
					Font font = label.getFont();
					font = font.deriveFont(font.getSize() + 5f);
					label.setFont(font);
					return label;
				}).toList();

				FrameWin.simpleColumn(frame, list);

				frame.pack();
				frame.setVisible(true);
				this.windows.removeIf(e -> !e.isEnabled());
				for (JFrame fw : this.windows) {
					fw.requestFocus();
				}
				this.windows.add(frame);
				frame.requestFocus();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}

	private class Change implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			DungeonGeneratorFrame.this.repaintGui();
		}
	}
}
