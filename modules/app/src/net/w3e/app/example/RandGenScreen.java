package net.w3e.app.example;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.LongStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.CustomLog;
import net.skds.lib2.mat.Direction;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.Vec3I;
import net.skds.lib2.utils.Holders.IntHolder;
import net.w3e.app.api.window.AbstractFrameWin;
import net.w3e.app.api.window.BackgroundExecutor;
import net.w3e.app.api.window.BackgroundExecutor.BackgroundExecutorBuilder;
import net.w3e.app.api.window.FrameWin;
import net.w3e.app.api.window.IBackgroundExecutor;
import net.w3e.app.api.window.ImageScreen;
import net.w3e.wlib.collection.CollectionBuilder;
import net.w3e.wlib.dungeon.DungeonException;
import net.w3e.wlib.dungeon.DungeonGenerator;
import net.w3e.wlib.dungeon.DungeonLayer;
import net.w3e.wlib.dungeon.DungeonRoomInfo;
import net.w3e.wlib.dungeon.json.sasai.DungeonJsonAdaptersString;
import net.w3e.wlib.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.wlib.dungeon.DungeonLayer.IPathLayer;
import net.w3e.wlib.dungeon.layers.ClearLayer;
import net.w3e.wlib.dungeon.layers.DistanceLayer;
import net.w3e.wlib.dungeon.layers.FeatureLayer;
import net.w3e.wlib.dungeon.layers.ListLayer;
import net.w3e.wlib.dungeon.layers.RoomLayer;
import net.w3e.wlib.dungeon.layers.terra.BiomeLayer;
import net.w3e.wlib.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.wlib.mat.WBoxI;

@CustomLog
public class RandGenScreen extends AbstractFrameWin {

	static {
		DungeonJsonAdaptersString.initString();
	}

	private final Change CHANGE = new Change();

	private BackgroundExecutor backgroundExecutor;
	private ImagePainter image;
	private JCheckBox showWall;
	private JCheckBox showSoftPath;
	private JCheckBox showTemperature;
	private JCheckBox showWet;
	private JCheckBox showBiome;
	private JCheckBox fast;
	private JCheckBox print;
	private JCheckBox rotate;
	private JCheckBox debugSave;

	public RandGenScreen(JFrame parent) {
		super("Random Generator");
		this.init();
		this.atRightPosition(parent);

		this.setVisible(true);
	}

	protected final void init() {
		List<Component> buttons = new ArrayList<>();
		List<Component> settings = new ArrayList<>();

		this.showWall = new JCheckBox("Wall");
		this.showWall.addChangeListener(CHANGE);
		settings.add(this.showWall);

		this.showSoftPath = new JCheckBox("Soft Path");
		this.showSoftPath.addChangeListener(CHANGE);
		settings.add(this.showSoftPath);

		this.showTemperature = new JCheckBox("Temperature");
		this.showTemperature.addChangeListener(CHANGE);
		settings.add(this.showTemperature);

		this.showWet = new JCheckBox("Wet");
		this.showWet.addChangeListener(CHANGE);
		settings.add(this.showWet);

		this.showBiome = new JCheckBox("Biome");
		this.showBiome.addChangeListener(CHANGE);
		settings.add(this.showBiome);

		this.fast = new JCheckBox("Fast");
		this.fast.addChangeListener(CHANGE);
		settings.add(this.fast);

		this.print = new JCheckBox("Print", true);
		this.print.addChangeListener(CHANGE);
		settings.add(this.print);

		this.rotate = new JCheckBox("Rotate", false);
		//this.rotate.addChangeListener(CHANGE);
		settings.add(this.rotate);

		this.debugSave = new JCheckBox("Debug Save");
		//this.debugSave.addChangeListener(CHANGE);
		settings.add(this.debugSave);

		buttons.addAll(LongStream.range(0, settings.size() - buttons.size()).mapToObj(seed -> this.createButton(String.valueOf(seed), btn -> exampleDungeon(btn, seed))).toList());

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));

		JPanel left = new JPanel();
		left.setBorder(null);
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		this.add(left);

		simpleColumn(left, buttons);

		JPanel right = new JPanel();
		right.setBorder(null);
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

		simpleColumn(right, settings);
		this.add(right);

		this.pack();
	}

	private final JButton createButton(String text, Consumer<JButton> function) {
		JButton button = new JButton(text);
		FrameWin.setSize(button, 300, 26);
		addCmonentListiner(button, function);
		return button;
	}

	@Override
	protected final void onClose() {
		if (this.image != null) {
			this.image.close();
			this.image = null;
		}
		if (this.backgroundExecutor != null) {
			this.backgroundExecutor.stopAndClose(false).run();
			this.backgroundExecutor = null;
		}
	}

	private final void change() {
		if (this.image != null) {
			this.image.refillImage();
			this.image.update();
		}
	}

	private final void exampleDungeon(JButton button, long seed) {
		this.creteDungeon(button.getText(), DungeonGenerator.example(seed, this.rotate.isSelected() ? Direction.WEST : Direction.SOUTH, this.debugSave.isSelected()));
	}

	private final void creteDungeon(String name, DungeonGenerator dungeon) {
		this.onClose();
		dungeon.regenerate();
		WBoxI dimension = dungeon.dimension();
		Vec3I size = dimension.size().addI(Vec3I.SINGLE);
		IntHolder limit = new IntHolder();
		this.image = new ImageScreen.ImageScreenBuilder().setLocation(this).setSize(size.xi() * 4 + 1, size.zi() * 4 + 1).setScale(9).buildWith((frameTitle, width, height, scale, background) -> 
			new ImagePainter(frameTitle, width, height, scale, background, dungeon)
		);
		this.backgroundExecutor = new BackgroundExecutorBuilder(name, this).setExecute((oldProgres, executor) -> this.execute(oldProgres, executor, limit, dungeon)).setParentVisible(true).setUpdateParentPosition(false).build();
		int x = this.getX() + this.getWidth();
		this.backgroundExecutor.setLocation(FastMath.clamp(this.image.getX() + this.image.getWidth(), x, x + 2000) - 5, this.getY());
		this.backgroundExecutor.setSize(new Dimension(this.backgroundExecutor.getWidth(), Math.min(this.getHeight() + this.image.getHeight(), 1650)));

		this.backgroundExecutor.run();
	}

	private final int execute(int oldProgres, IBackgroundExecutor executor, IntHolder limit, DungeonGenerator dungeon) {
		if (executor.isStop()) {
			return oldProgres;
		}
		sleep(1000);
		boolean fast = RandGenScreen.this.fast.isSelected();

		int progress = 0;
		if (limit.getValue() >= 25) {
			log.warn("limit reached");
			executor.stop();
			return oldProgres;
		}
		limit.increment();

		int count = 0;
		long time = System.currentTimeMillis();
		boolean next = true;
		boolean print = this.print.isSelected();
		while (next) {
			if (executor.isStop()) {
				return oldProgres;
			}
			count++;

			DungeonLayer layer = dungeon.getFirst();
			try {
				progress = dungeon.generate();
			} catch (DungeonException e) {
				e.printStackTrace();
				executor.stop();
				return oldProgres;
			}

			int[] data = this.image.refillImage();

			next = (count == 0 || (fast && System.currentTimeMillis() - time <= 5)) && progress < 100;
			if (progress > 100) {
				log.warn("progress is more than 100");
			}
			if (!print) {
				if (count != 1 && next) {
					continue;
				}
			}

			if (layer instanceof IPathLayer) {
				System.out.println(this.printString(limit, count, "path", String.format("paths %s, connections:[%s,%s]", data[0], data[1], data[2])));
				continue;
			}
			if (layer instanceof ListLayer list) {
				String listProgress = String.format(" %s/%s", list.size(), list.filled());
				String roomsProgress = "rooms" + listProgress;

				String layerName = null;
				String arg = roomsProgress;

				if (list instanceof DistanceLayer) {
					layerName = "distance";
					arg = "enterances" + listProgress;
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
						System.out.println(this.printString(limit, count, layerName, "setup"));
					} else {
						System.out.println(this.printString(limit, count, layerName, arg));
					}
					continue;
				}
				System.out.println(this.printString(limit, count, "list/unhandled", layer.getClass().getSimpleName()));
				continue;
			}
			if (layer instanceof ClearLayer) {
				System.out.println(this.printString(limit, count, "clear walls", layer.getClass().getSimpleName()));
				continue;
			}
			System.out.println(this.printString(limit, count, "unhandled", layer.getClass().getSimpleName()));
		}
		System.out.println();
		this.image.update();

		return progress;
	}

	private final String printString(IntHolder limit, int count, String name, String args) {
		return String.format("[%s/%s](%s) %s", limit.getValue(), count, name, args);
	}

	private class ImagePainter extends ImageScreen implements MouseListener {

		private final int scale;
		private final DungeonGenerator dungeon;
		private final Vec3I size;
		private final Vec3I min;
		private final List<AbstractFrameWin> windows = new ArrayList<>();

		private ImagePainter(String frameTitle, int width, int height, int scale, Color background, DungeonGenerator dungeon) {
			super(frameTitle, width, height, scale, background);
			this.scale = scale * 4;
			this.dungeon = dungeon;
			WBoxI dimension = dungeon.dimension();
			this.size = dimension.size().addI(Vec3I.SINGLE);
			this.min = dimension.min().inverseI();

			this.setVisible(true);
			this.image.addMouseListener(this);
		}

		private final int[] refillImage() {
			int[] data = new int[]{0,0,0};

			boolean showWall = RandGenScreen.this.showWall.isSelected();
			boolean showSoftPath = RandGenScreen.this.showSoftPath.isSelected();
			boolean showTemperature = RandGenScreen.this.showTemperature.isSelected();
			boolean showWet = RandGenScreen.this.showWet.isSelected();
			boolean showBiome = RandGenScreen.this.showBiome.isSelected();

			this.setColor(Color.WHITE);

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

			xS -= 4;
			zS -= 4;

			for (Map<Vec3I, DungeonRoomInfo> entry1 : this.dungeon.getChunks().values()) {
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
								if (!value.data().get(BiomeLayer.KEY).equals("void")) {
									this.setColor(x + 1, z + 1, Color.MAGENTA);
								}
							} catch (Exception e) {
								e.printStackTrace();
								showBiome = false;
								RandGenScreen.this.showBiome.setSelected(false);
							}
						}

						Color color = Color.LIGHT_GRAY;
						if (value.isEnterance()) {
							color = Color.DARK_GRAY;
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
		public final void mouseClicked(MouseEvent event) {
			int x = event.getX() / this.scale - this.min.xi();
			int z = event.getY() / this.scale - this.min.zi();
			//x *= -1;
			z *= -1;
			DungeonRoomCreateInfo data = this.dungeon.get(new Vec3I(x, 0, z));
			if (!data.exists()) {
				System.err.println("not found");
			} else {
				DungeonRoomInfo room = data.room();

				AbstractFrameWin frame = new AbstractFrameWin("Room Info") {
					@Override
					protected void onClose() {
						this.setEnabled(false);
					}
				};
				frame.setLocation(event.getXOnScreen() - (event.getXOnScreen() % this.scale) + 18, event.getYOnScreen() - (event.getYOnScreen() % this.scale) + 18);

				List<Component> list = new ArrayList<>();

				list.add(new JLabel(String.format("Pos: %s", room.pos())));
				list.add(new JLabel(String.format("Chunk: %s", room.chunk())));
				list.add(new JLabel(String.format("Distance: %s", room.getDistance())));
				list.add(new JLabel(String.format("IsWall: %s", room.isWall())));
				list.add(new JLabel(String.format("IsEnterance: %s", room.isEnterance())));
				list.add(new JLabel(String.format("Connections: %s", CollectionBuilder.list(String.class)
					.add(room.isConnect(Direction.UP) ? Direction.UP.name() : null)
					.add(room.isConnect(Direction.DOWN) ? Direction.DOWN.name() : null)
					.add(room.isConnect(Direction.NORTH) ? Direction.NORTH.name() : null)
					.add(room.isConnect(Direction.SOUTH) ? Direction.SOUTH.name() : null)
					.add(room.isConnect(Direction.WEST) ? Direction.WEST.name() : null)
					.add(room.isConnect(Direction.EAST) ? Direction.EAST.name() : null)
				.removeNull().build())));
				list.add(new JLabel(String.format("Data: %s", room.data())));

				RandGenScreen.simpleColumn(frame, list);

				frame.pack();
				frame.setVisible(true);
				this.windows.removeIf(e -> !e.isEnabled());
				for (AbstractFrameWin fw : this.windows) {
					fw.requestFocus();
				}
				this.windows.add(frame);
				frame.requestFocus();
			}
		}

		@Override
		public final void mousePressed(MouseEvent e) {}

		@Override
		public final void mouseReleased(MouseEvent e) {}

		@Override
		public final void mouseEntered(MouseEvent e) {}

		@Override
		public final void mouseExited(MouseEvent e) {}
	}

	private final class Change implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			RandGenScreen.this.change();
		}
	}

	@Override
	public final String getName() {
		return "Random Generator";
	}
}
