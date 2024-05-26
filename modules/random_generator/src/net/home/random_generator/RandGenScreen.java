package net.home.random_generator;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.LongStream;

import javax.swing.JButton;
import javax.swing.JSlider;

import net.api.window.BackgroundExecutor;
import net.api.window.FrameWin;
import net.api.window.ImageScreen;
import net.api.window.BackgroundExecutor.BackgroundExecutorBuilder;
import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonLayer.IPathLayer;
import net.w3e.base.dungeon.DungeonLayer.ITemperatureLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.MathData;
import net.w3e.base.math.vector.WBox;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;
import net.w3e.base.noise.PerlinNoiseSampler;

public class RandGenScreen extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new RandGenScreen());
		MainFrame.run(args);
	}

	private BackgroundExecutor backgroundExecutor;
	private ImageScreen image;
	private final JSlider size = new JSlider(0, 400);
	private final JSlider seed = new JSlider(0, 100);

	protected void init(FrameWin fw, List<String> args) {
		List<Component> buttons = new ArrayList<>();

		buttons.addAll(LongStream.range(0, 5).mapToObj(seed -> this.createButton(String.valueOf(seed), btn -> exampleDungeon(btn, seed))).toList());

		buttons.add(this.createButton("perlin 1", this::perlin1));
		buttons.add(this.createButton("perlin 2", this::perlin2));
		buttons.add(this.setSettings(this.size, 15));
		buttons.add(this.setSettings(this.seed, 10));

		this.simpleColumn(fw, buttons);

		fw.pack();
	}

	private final JButton createButton(String text, Consumer<JButton> function) {
		JButton button = new JButton(text);
		FrameWin.setSize(button, 300, 26);
		this.addCmonentListiner(button, function);
		return button;
	}

	private final JSlider setSettings(JSlider slider, int value) {
		slider.setValue(value);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(10);
		return slider;
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

	private final void exampleDungeon(JButton button, long seed) {
		this.creteDungeon(button.getText(), DungeonGenerator.example(seed));
	}

	private final void creteDungeon(String name, DungeonGenerator<String> dungeon) {
		this.onClose();
		dungeon.regenerate();
		FrameWin fw = this.getFrame();
		WBox dimension = dungeon.dimension();
		WVector3 min = dimension.min().inverse();
		WVector3 size = dimension.size().add(new WVector3(1, 1, 1));
		IntHolder holder = new IntHolder();
		this.image = new ImageScreen.ImageScreenBuilder().setLocation(fw).setSize(size.getX() * 3, size.getZ() * 3).setScale(9).build();
		this.backgroundExecutor = new BackgroundExecutorBuilder(name, fw).setExecute((oldProgres, executor) -> {
			if (executor.isStop()) {
				return oldProgres;
			}

			sleep(1000);
			if (holder.getAsInt() >= 16) {
				System.out.println("limit reached");
				executor.stop();
				return oldProgres;
			}

			if (executor.isStop()) {
				return oldProgres;
			}

			DungeonLayer<String> generator = dungeon.getFirst();
			int progress = dungeon.generate();
			holder.add();
			int rooms = 0;
			int hard = 0;
			int soft = 0;
			this.image.setColor(Color.WHITE);
			for (Map<WVector3, DungeonRoomInfo<String>> entry1 : dungeon.getRooms().values()) {
				rooms += entry1.size();
				for (Entry<WVector3, DungeonRoomInfo<String>> entry2 : entry1.entrySet()) {
					WVector3 pos = entry2.getKey();
					int x = (pos.getX() + min.getX()) * 3 + 1;
					int z = (pos.getZ() + min.getZ()) * 3 + 1;
					try {
						DungeonRoomInfo<String> value = entry2.getValue();
						if (value.isEnterance()) {
							this.image.setColor(x, z, Color.BLUE);
						} else {
							this.image.setColor(x, z, Color.BLACK);
						}
						for (WDirection direction : WDirection.values()) {
							if (direction != WDirection.UP && direction != WDirection.DOWN) {
								if (value.isConnect(direction, true)) {
									hard++;
									this.image.setColor(x + direction.relative.getX(), z + + direction.relative.getZ(), Color.RED);
								} else if (value.isConnect(direction, false)) {
									soft++;
									this.image.setColor(x + direction.relative.getX(), z + + direction.relative.getZ(), Color.GREEN);
								}
							}
						}
					} catch (Exception e) {
						throw e;
					}
				}
			}
			if (generator instanceof IPathLayer) {
				System.out.println(String.format("[%s](path) rooms %s, connections:[%s,%s]", holder.getAsInt(), rooms, hard, soft));
			}
			if (generator instanceof ITemperatureLayer) {
				System.out.println(String.format("[%s](temperature) rooms %s?", holder.getAsInt(), rooms));
			}
			this.image.update();

			return progress;
		}).setParentVisible(true).setUpdateParentPosition(false).build();
		this.backgroundExecutor.setLocation(Math.max(this.image.getX() + this.image.getWidth(), fw.getX() + fw.getWidth())  - 5, fw.getY());

		this.backgroundExecutor.run();
	}

	private final void perlin1(JButton button) {
		int size = this.size.getValue();

		if (size % 2 == 0) {
			size++;
		}
		int d = size / 2;

		ImageScreen image = new ImageScreen.ImageScreenBuilder().setTitle("Map").setSize(size).build();

		PerlinNoiseSampler perlin = new PerlinNoiseSampler(new Random(this.seed.getValue()));
		MathData data = new MathData(4);
		double scalar = 0.1856;
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				double res = perlin.sample(x - d, 0, z - d);
				data.calculate(res);
				//System.out.print(String.format("%+.4f ", res * 10));

				res = BMatUtil.clamp(res, -scalar, scalar) + scalar;
				res *= 255d / (scalar * 2);
				int color = 255 - BMatUtil.round(res);
				image.setColor(x, z, new Color(color, color, color, 255));
			}
			//System.out.println();
		}
		System.out.println(data.generateString());
	}

	private final void perlin2(JButton button) {
		int size = this.size.getValue() * 10;

		if (size % 2 == 0) {
			size++;
		}
		int d = size / 2;
		size -= d;

		PerlinNoiseSampler perlin = new PerlinNoiseSampler(new Random(this.seed.getValue()));
		MathData data = new MathData();
		for (int x = -d; x < size; x++) {
			if (x % 10 == 0) {
				System.out.println(x);
			}
			for (int y = -d; y < size; y++) {
				for (int z = -d; z < size; z++) {
					double res = perlin.sample(x, y, z);
					data.calculate(res);
				}
			}
		}
		System.out.println(data.generateString());
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
