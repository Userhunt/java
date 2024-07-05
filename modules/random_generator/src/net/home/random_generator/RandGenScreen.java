package net.home.random_generator;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.api.window.AbstractFrameWin;
import net.api.window.BackgroundExecutor;
import net.api.window.FrameWin;
import net.api.window.IBackgroundExecutor;
import net.api.window.ImageScreen;
import net.api.window.BackgroundExecutor.BackgroundExecutorBuilder;
import net.home.FrameObject;
import net.home.MainFrame;
import net.w3e.base.collection.CollectionBuilder;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonGenerator.DungeonRoomCreateInfo;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonLayer.IPathLayer;
import net.w3e.base.dungeon.layers.DistanceLayer;
import net.w3e.base.dungeon.layers.IListLayer;
import net.w3e.base.dungeon.layers.terra.BiomeLayer;
import net.w3e.base.dungeon.layers.terra.CompositeTerraLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.math.vector.WBox;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;

public class RandGenScreen extends FrameObject {

	private final Change CHANGE = new Change();

	public static final void main(String[] args) {
		MainFrame.register(new RandGenScreen());
		MainFrame.run(args);
	}

	private BackgroundExecutor backgroundExecutor;
	private ImagePainter image;
	private JCheckBox showWall;
	private JCheckBox showSoftPath;
	private JCheckBox showTemperature;
	private JCheckBox showWet;
	private JCheckBox showBiome;
	private JCheckBox fast;

	protected final void init(FrameWin fw, List<String> args) {
		List<Component> buttons = new ArrayList<>();
		List<Component> settings = new ArrayList<>();

		buttons.addAll(LongStream.range(0, 6).mapToObj(seed -> this.createButton(String.valueOf(seed), btn -> exampleDungeon(btn, seed))).toList());
		settings.addAll(LongStream.range(0, 6).mapToObj(seed -> new JCheckBox("null")).toList());

		int i = 0;

		this.showWall = new JCheckBox("Show Wall");
		this.showWall.addChangeListener(CHANGE);
		settings.set(i++, this.showWall);

		this.showSoftPath = new JCheckBox("Show Soft Path");
		this.showSoftPath.addChangeListener(CHANGE);
		settings.set(i++, this.showSoftPath);

		this.showTemperature = new JCheckBox("Show Temperature");
		this.showTemperature.addChangeListener(CHANGE);
		settings.set(i++, this.showTemperature);

		this.showWet = new JCheckBox("Show Wet");
		this.showWet.addChangeListener(CHANGE);
		settings.set(i++, this.showWet);

		this.showBiome = new JCheckBox("Show Biome");
		this.showBiome.addChangeListener(CHANGE);
		settings.set(i++, this.showBiome);

		this.fast = new JCheckBox("Fast");
		this.fast.addChangeListener(CHANGE);
		settings.set(i++, this.fast);


		fw.setLayout(new BoxLayout(fw.getContentPane(), BoxLayout.X_AXIS));

		JPanel left = new JPanel();
		left.setBorder(null);
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		fw.add(left);

		this.simpleColumn(left, buttons);
		
		JPanel right = new JPanel();
		right.setBorder(null);
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

		this.simpleColumn(right, settings);
		fw.add(right);

		fw.pack();
	}

	private final JButton createButton(String text, Consumer<JButton> function) {
		JButton button = new JButton(text);
		FrameWin.setSize(button, 300, 26);
		this.addCmonentListiner(button, function);
		return button;
	}

	@Override
	protected final void onClose() {
		if (this.image != null) {
			this.image.close();
			this.image = null;
		}
		if (this.backgroundExecutor != null) {
			this.backgroundExecutor.stopAndClose().run();
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
		this.creteDungeon(button.getText(), DungeonGenerator.example(seed));
	}

	private final void creteDungeon(String name, DungeonGenerator dungeon) {
		this.onClose();
		dungeon.regenerate();
		FrameWin fw = this.getFrame();
		WBox dimension = dungeon.dimension();
		WVector3 size = dimension.size().add(new WVector3(1, 1, 1));
		IntHolder limit = new IntHolder();
		this.image = new ImageScreen.ImageScreenBuilder().setLocation(fw).setSize(size.getX() * 4, size.getZ() * 4).setScale(9).buildWith((frameTitle, width, height, scale, background) -> 
			new ImagePainter(frameTitle, width, height, scale, background, dungeon)
		);
		this.backgroundExecutor = new BackgroundExecutorBuilder(name, fw).setExecute((oldProgres, executor) -> this.execute(oldProgres, executor, limit, dungeon)).setParentVisible(true).setUpdateParentPosition(false).build();
		this.backgroundExecutor.setLocation(Math.max(this.image.getX() + this.image.getWidth(), fw.getX() + fw.getWidth())  - 5, fw.getY());
		this.backgroundExecutor.setSize(new Dimension(this.backgroundExecutor.getWidth(), this.getFrame().getHeight() + this.image.getHeight()));

		this.backgroundExecutor.run();
	}

	private final int execute(int oldProgres, IBackgroundExecutor executor, IntHolder limit, DungeonGenerator dungeon) {
		if (executor.isStop()) {
			return oldProgres;
		}
		sleep(1000);
		boolean fast = RandGenScreen.this.fast.isSelected();
		long time = System.currentTimeMillis();
		int progress = 0;
		if (limit.getAsInt() >= 25) {
			System.out.println("limit reached");
			executor.stop();
			return oldProgres;
		}
		limit.add();

		int count = 0;
		while ((count == 0 || (fast && System.currentTimeMillis() - time <= 9)) && progress < 100) {
			if (executor.isStop()) {
				return oldProgres;
			}
			count++;

			DungeonLayer generator = dungeon.getFirst();
			progress = dungeon.generate();

			int[] data = this.image.refillImage();

			if (generator instanceof IPathLayer) {
				System.out.println(this.printString(limit, count, "path", String.format("rooms %s, connections:[%s,%s]", data[0], data[1], data[2])));
				continue;
			}
			if (generator instanceof IListLayer list) {
				if (list instanceof CompositeTerraLayer) {
					if (list.size() == -1) {
						System.out.println(this.printString(limit, count, "terra/composite", "setup"));
						continue;
					}
					System.out.println(this.printString(limit, count, "terra/composite", String.format("rooms %s/%s", list.size(), list.filled())));
					continue;
				}
				if (list instanceof BiomeLayer) {
					if (list.size() == -1) {
						System.out.println(this.printString(limit, count, "terra/biome", "setup"));
						continue;
					}
					System.out.println(this.printString(limit, count, "terra/biome", String.format("biomes %s/%s", list.size(), list.filled())));
					continue;
				}
				if (list instanceof DistanceLayer) {
					if (list.size() == -1) {
						System.out.println(this.printString(limit, count, "distance", "setup"));
						continue;
					}
					System.out.println(this.printString(limit, count, "distance", String.format("enterances %s/%s", list.size(), list.filled())));
					continue;
				}
				System.out.println(this.printString(limit, count, "list/unhandled", generator.getClass().getSimpleName()));
				continue;
			}
			System.out.println(this.printString(limit, count, "unhandled", generator.getClass().getSimpleName()));
		}
		System.out.println();
		this.image.update();

		return progress;
	}

	private final String printString(IntHolder limit, int count, String name, String args) {
		return String.format("[%s/%s](%s) %s", limit.get(), count, name, args);
	}

	private class ImagePainter extends ImageScreen implements MouseListener {

		private final int scale;
		private final DungeonGenerator dungeon;
		private final WVector3 size;
		private final WVector3 min;
		private final List<AbstractFrameWin> windows = new ArrayList<>();

		private ImagePainter(String frameTitle, int width, int height, int scale, Color background, DungeonGenerator dungeon) {
			super(frameTitle, width, height, scale, background);
			this.scale = scale * 4;
			this.dungeon = dungeon;
			WBox dimension = dungeon.dimension();
			this.size = dimension.size().add(new WVector3(1, 1, 1));
			this.min = dimension.min().inverse();

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

			for (int x = 0; x < this.size.getX(); x++) {
				for (int z = 0; z < this.size.getZ(); z++) {
					for (int i = 0; i < 4; i++) {
						for (int j = 0; j < 4; j++) {
							if (i == 0 || j == 0) {
								this.setColor(x * 4 + i, z * 4 + j, Color.GRAY);
							}
						}
					}
				}
			}

			for (Map<WVector3, DungeonRoomInfo> entry1 : this.dungeon.getRooms().values()) {
				data[0] += entry1.size();
				for (Entry<WVector3, DungeonRoomInfo> entry2 : entry1.entrySet()) {
					WVector3 pos = entry2.getKey();
					int x = (pos.getX() + min.getX()) * 4 + 2;
					int z = (pos.getZ() + min.getZ()) * 4 + 2;
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
						for (WDirection direction : WDirection.values()) {
							if (direction != WDirection.UP && direction != WDirection.DOWN) {
								if (value.isConnect(direction, true)) {
									data[1]++;
									this.setColor(x + direction.relative.getX(), z + + direction.relative.getZ(), Color.RED);
								} else if (value.isConnect(direction, false)) {
									data[2]++;
									if (showSoftPath) {
										this.setColor(x + direction.relative.getX(), z + + direction.relative.getZ(), Color.GREEN);
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
			int x = event.getX() / this.scale - this.min.getX();
			int z = event.getY() / this.scale - this.min.getZ();
			DungeonRoomCreateInfo data = this.dungeon.get(new WVector3(x, 0, z));
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

				list.add(new JLabel(String.format("Pos: %s", room.pos().toStringArray())));
				list.add(new JLabel(String.format("Chunk: %s", room.chunk().toStringArray())));
				list.add(new JLabel(String.format("Distance: %s", room.getDistance())));
				list.add(new JLabel(String.format("IsWall: %s", room.isWall())));
				list.add(new JLabel(String.format("IsEnterance: %s", room.isEnterance())));
				list.add(new JLabel(String.format("Connections: %s", CollectionBuilder.list(String.class)
					.add(room.isConnect(WDirection.UP) ? WDirection.UP.name() : null)
					.add(room.isConnect(WDirection.DOWN) ? WDirection.DOWN.name() : null)
					.add(room.isConnect(WDirection.NORTH) ? WDirection.NORTH.name() : null)
					.add(room.isConnect(WDirection.SOUTH) ? WDirection.SOUTH.name() : null)
					.add(room.isConnect(WDirection.WEST) ? WDirection.WEST.name() : null)
					.add(room.isConnect(WDirection.EAST) ? WDirection.EAST.name() : null)
				.removeNull().build())));
				list.add(new JLabel(String.format("Data: %s", room.data())));

				RandGenScreen.this.simpleColumn(frame, list);

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

	@Override
	public final String fastKey() {
		return "rand_gen";
	}

	@Override
	public final int[] version() {
		return new int[]{1,0,0};
	}
}
