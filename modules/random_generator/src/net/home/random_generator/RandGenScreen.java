package net.home.random_generator;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.api.window.BackgroundExecutor;
import net.api.window.FrameWin;
import net.api.window.ImageScreen;
import net.api.window.BackgroundExecutor.BackgroundExecutorBuilder;
import net.home.FrameObject;
import net.home.MainFrame;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonLayer;
import net.w3e.base.dungeon.DungeonLayer.IPathLayer;
import net.w3e.base.dungeon.DungeonLayer.ITemperatureLayer;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.math.MathData;
import net.w3e.base.math.OpenSimplex2S;
import net.w3e.base.math.vector.WBox;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;
import net.w3e.base.noise.DoublePerlinNoiseSampler;
import net.w3e.base.noise.DoublePerlinNoiseSampler.NoiseParameters;
import net.w3e.base.noise.InterpolatedNoiseSampler;
import net.w3e.base.noise.OctavePerlinNoiseSampler;
import net.w3e.base.noise.Perlin2D;
import net.w3e.base.noise.PerlinNoiseSampler;
import net.w3e.base.random.CheckedRandom;
import net.w3e.base.random.Xoroshiro128PlusPlusRandom;

public class RandGenScreen extends FrameObject {

	public static final void main(String[] args) {
		MainFrame.register(new RandGenScreen());
		MainFrame.run(args);
	}

	private BackgroundExecutor backgroundExecutor;
	private ImageScreen image;
	private final JSlider size = new JSlider(0, 2000);
	private final JSlider seed = new JSlider(0, 100);
	private final JSlider y = new JSlider(-64, 320);
	private final JSlider scale = new JSlider(1, 10);

	protected void init(FrameWin fw, List<String> args) {
		List<Component> buttons = new ArrayList<>();

		buttons.addAll(LongStream.range(0, 5).mapToObj(seed -> this.createButton(String.valueOf(seed), btn -> exampleDungeon(btn, seed))).toList());

		buttons.add(this.createButton("perlin 1", this::perlin1));
		buttons.add(this.createButton("perlin 2", this::perlin2));
		buttons.add(this.createButton("perlin 3", this::perlin3));
		buttons.add(this.createButton("perlin 4", this::perlin4));
		buttons.add(this.createButton("perlin 5", this::perlin5));
		buttons.add(this.createButton("perlin 6", this::perlin6));
		buttons.add(this.createButton("perlin 7", this::perlin7));
		buttons.add(this.createButton("perlin 8", this::perlin8));
		buttons.add(this.createButton("perlin 9", this::perlin9));
		buttons.add(this.createButton("perlin 10", this::perlin10));
		buttons.add(new JLabel("Размер"));
		buttons.add(this.setSettings(this.size, 15));
		buttons.add(new JLabel("Сид"));
		buttons.add(this.setSettings(this.seed, 10));
		buttons.add(new JLabel("Y"));
		buttons.add(this.setSettings(this.y, 0));
		buttons.add(new JLabel("Scale"));
		buttons.add(this.setSettings(this.scale, 1));

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

	private record SizeData(int size, int d) {
		public static SizeData create(RandGenScreen screen) {
			int size = screen.size.getValue();

			if (size % 2 == 0) {
				size++;
			}
			int d = size / 2;
			return new SizeData(size, d);
		} 
	}

	private final ImageScreen perlinImage(SizeData size, DoubleList values) {
		return this.perlinImage(size, values, Double.MAX_VALUE, Double.MIN_VALUE);
	}

	private final ImageScreen perlinImage(SizeData size, DoubleList values, double min, double max) {
		ImageScreen image = new ImageScreen.ImageScreenBuilder().setTitle("Map").setSize(size.size).build();

		MathData data = new MathData(4);

		for (double d : values.toDoubleArray()) {
			data.calculate(d);
		}

		int x = 0;
		int y = 0;

		int s = size.size;

		max = Math.max(max, data.getMax());
		min = Math.max(min, data.getMin());

		DoubleIterator iterator = values.doubleIterator();
		while (iterator.hasNext()) {
			image.setColor(x, y, data.toColor(iterator.nextDouble(), min, max));

			if (!iterator.hasNext()) {
				break;
			}
			x++;
			if (x >= s) {
				x = 0;
				y++;
			}
		}
		System.out.println(data.generateString());
		return image;
	}

	private final void perlin1(JButton button) {
		SizeData size = SizeData.create(this);

		DoubleArrayList list = new DoubleArrayList();

		PerlinNoiseSampler perlin = new PerlinNoiseSampler(new Random(this.seed.getValue()));

		int scale = BMatUtil.pow(10, this.scale.getValue() - 1);
		int y = this.y.getValue();
		for (int x = 0; x < size.size; x++) {
			for (int z = 0; z < size.size; z++) {
				list.add(perlin.sample((x - size.d) * scale, y, (z - size.d) * scale));
			}
		}
		this.perlinImage(size, list);
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

	private final void perlin3(JButton button) {
		SizeData size = SizeData.create(this);
		
		DoubleArrayList list = new DoubleArrayList();

		OctavePerlinNoiseSampler noise = OctavePerlinNoiseSampler.createLegacy(new Xoroshiro128PlusPlusRandom(this.seed.getValue()), IntStream.rangeClosed(-15, 0));

		int scale = BMatUtil.pow(10, this.scale.getValue() - 1);
		int y = this.y.getValue();
		for (int x = 0; x < size.size; x++) {
			for (int z = 0; z < size.size; z++) {
				list.add(noise.sample((x - size.d) * scale, y, (z - size.d) * scale));
			}
		}
		this.perlinImage(size, list);
	}

	private final void perlin4(JButton button) {
		SizeData size = SizeData.create(this);

		DoubleArrayList list = new DoubleArrayList();

		Xoroshiro128PlusPlusRandom random = new Xoroshiro128PlusPlusRandom(this.seed.getValue());

		InterpolatedNoiseSampler noise = new InterpolatedNoiseSampler(
			OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-15, 0)),
			OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-7, 0)),
		.25, 0.125, 80.0, 160.0, 8.0);
		//InterpolatedNoiseSampler.noise_nether(new RandomProvider(this.seed.getValue()));

		int scale = BMatUtil.pow(10, this.scale.getValue() - 1);
		int y = this.y.getValue();
		for (int x = 0; x < size.size; x++) {
			for (int z = 0; z < size.size; z++) {
				list.add(noise.sample(new InterpolatedNoiseSampler.NoisePos((x - size.d) * scale, y, (z - size.d) * scale)));
			}
		}
		this.perlinImage(size, list);
	}

	private final void perlin5(JButton button) {
		SizeData size = SizeData.create(this);

		DoubleArrayList list = new DoubleArrayList();

		Xoroshiro128PlusPlusRandom random = new Xoroshiro128PlusPlusRandom(this.seed.getValue());

		DoublePerlinNoiseSampler noise = DoublePerlinNoiseSampler.create(random, new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0, 1.0, 1.0, 0.0));
		//DoublePerlinNoiseSampler.create(random, -4, 1);

		int scale = BMatUtil.pow(10, this.scale.getValue() - 1);
		int y = this.y.getValue();
		for (int x = 0; x < size.size; x++) {
			for (int z = 0; z < size.size; z++) {
				list.add(noise.sample((x - size.d) * scale, y, (z - size.d) * scale));
			}
		}
		this.perlinImage(size, list);
	}

	private final void perlin6(JButton button) {
		SizeData size = SizeData.create(this);

		DoubleArrayList list = null;

		PerlinNoiseSampler perlin = new PerlinNoiseSampler(new Random(this.seed.getValue()));

		double scale = BMatUtil.pow(10, this.scale.getValue() - 1) / .1;

		int y = Math.max(this.y.getValue(), -63);
		for (int i = 0; i < y + 64; i++) {
			DoubleArrayList sub = new DoubleArrayList();
			for (int x = 0; x < size.size; x++) {
				for (int z = 0; z < size.size; z++) {
					sub.add(perlin.sample((x - size.d) * scale, i, (z - size.d) * scale));
				}
			}
			if (list == null) {
				list = sub;
			} else {
				for (int j = 0; j < list.size(); j++) {
					list.set(j, list.getDouble(j) + sub.getDouble(j));
				}
			}
		}

		this.perlinImage(size, list);
	}
	
	private final void perlin7(JButton button) {
		SizeData size = SizeData.create(this);

		DoubleArrayList list = null;

		{
			for (int k = 0; k < 2; k++) {
				Xoroshiro128PlusPlusRandom random = new Xoroshiro128PlusPlusRandom(this.seed.getValue() + k * 2);

				InterpolatedNoiseSampler noise1 = new InterpolatedNoiseSampler(
					OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-15, 10)),
					OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-15, 10)),
					OctavePerlinNoiseSampler.create(random, IntStream.rangeClosed(-7, 0)),
				.25, 0.125, 8.0, 160.0, 8.0);
	
				//PerlinNoiseSampler noise2 = new PerlinNoiseSampler(random);
				int scale = BMatUtil.pow(10, this.scale.getValue() - 1);
	
				int y = Math.max(this.y.getValue(), -63);
				for (int i = 0; i < y + 64; i++) {
					DoubleArrayList sub = new DoubleArrayList();
					for (int x = 0; x < size.size; x++) {
						for (int z = 0; z < size.size; z++) {
							sub.add(
								noise1.sample(new InterpolatedNoiseSampler.NoisePos((x - size.d) * scale, i, (z - size.d) * scale)) 
								//+ noise2.sample((x - size.d) * scale, i, (z - size.d) * scale) * 10
							);
						}
					}
					if (list == null) {
						list = sub;
					} else {
						for (int j = 0; j < list.size(); j++) {
							list.set(j, list.getDouble(j) + sub.getDouble(j));
						}
					}
				}
			}
		}
		this.perlinImage(size, list).getImage();
	}

	private final void perlin8(JButton button) {
		SizeData size = SizeData.create(this);

		DoubleArrayList list = new DoubleArrayList();

		NoiseParameters param = new DoublePerlinNoiseSampler.NoiseParameters(
			-10,
			1.5,
			0,
			1,
			0,
			0,
			0
			);

		System.out.println(param);

		DoublePerlinNoiseSampler noise = DoublePerlinNoiseSampler.create(new CheckedRandom(this.seed.getValue()), param);
		int scale = BMatUtil.pow(10, this.scale.getValue() - 1);
		int y = this.y.getValue();
		for (int x = 0; x < size.size; x++) {
			for (int z = 0; z < size.size; z++) {
				list.add(noise.sample((x - size.d) * scale, y, (z - size.d) * scale));
			}
		}

		this.perlinImage(size, list).getImage();
	}

	private final void perlin9(JButton button) {
		SizeData size = SizeData.create(this);

		Perlin2D noise = new Perlin2D(this.seed.getValue());

		DoubleArrayList list = new DoubleArrayList();

		int scale = BMatUtil.pow(10, this.scale.getValue() - 1);

		System.out.println(noise.noise(2.5f, 0.5f));

		for (int x = 0; x < size.size; x++) {
			for (int z = 0; z < size.size; z++) {
				list.add(noise.noise((x - size.d) * scale, (z - size.d) * scale));
			}
		}

		this.perlinImage(size, list).getImage();
	}

	private final void perlin10(JButton button) {
		SizeData size = SizeData.create(this);

		DoubleArrayList list = new DoubleArrayList();

		int seed = this.seed.getValue();

		int scale = BMatUtil.pow(10, this.scale.getValue() - 1);

		int octave = 10;

		for (int x = 0; x < size.size; x++) {
			for (int z = 0; z < size.size; z++) {
				float value = octave(
					octave, 
					(x1, y1) -> OpenSimplex2S.noise2(seed, x1, y1), 
					(x - size.d) * scale,
					(z - size.d) * scale,
					.75f
				);
				list.add(value);
			}
		}

		this.perlinImage(size, list, -1, 1).getImage();
	}
	
	private final float octave(int octaves, INoise2 noise, int fx, int fy, float persistence) {
		float amplitude = 1;
		float max = 0;
		float result = 0;

		while (octaves-- > 0)
		{
			max += amplitude;
			result += noise.noise(fx, fy) * amplitude;
			amplitude *= persistence;
			fx *= 2;
			fy *= 2;
		}

		return result/max;
	}

	private static interface INoise2 {
		double noise(int x, int y);
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
