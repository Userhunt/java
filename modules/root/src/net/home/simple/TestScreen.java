package net.home.simple;

import java.awt.Component;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.api.registry.DynamicRegistry;
import net.api.registry.DynamicRegistry.CacheDynamicRegistry;
import net.api.window.BackgroundExecutor;
import net.api.window.FrameWin;
import net.api.window.BackgroundExecutor.BackgroundExecutorBuilder;
import net.home.FrameObject;
import net.home.MainFrame;
import net.w3e.base.BStringUtil;
import net.w3e.base.collection.ArraySet;
import net.w3e.base.collection.CollectionOfCollections;
import net.w3e.base.collection.ModifiedQueue;
import net.w3e.base.collection.RandomCollection;
import net.w3e.base.collection.ModifiedQueue.QueueTask;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.jar.JarUtil;

public class TestScreen extends FrameObject {

	private boolean toArrayList;
	private final Random RANDOM = new Random();

	@Override
	protected final void init(FrameWin fw, List<String> args) {
		List<Component> list = new ArrayList<>();

		list.add(this.addCmonentListiner(new JCheckBox("toArrayList"), e -> this.toArrayList = e.isSelected()));
		list.add(this.addCmonentListiner(new JButton("Random Sort"), this::randomSort));
		list.add(this.addCmonentListiner(new JButton("Random Shuffle"), this::randomShuffle));
		list.add(this.addCmonentListiner(new JButton("Random Shuffle Sort"), this::randomShuffleSort));
		list.add(this.addCmonentListiner(new JButton("Read jar"), this::readJar));
		list.add(this.addCmonentListiner(new JButton("Quote string"), this::quote));
		list.add(this.addCmonentListiner(new JButton("PutLinkedMap"), this::putLinkedMap));
		list.add(this.addCmonentListiner(new JButton("Registry"), this::registry));
		list.add(new RGBAPanel(true, true));
		list.add(this.addCmonentListiner(new JButton("CollectinoOfCollections"), this::collectionOfCollections));
		list.add(this.addCmonentListiner(new JButton("concurrent"), this::concurrent));
		list.add(this.addCmonentListiner(new JButton("queue"), this::modifiedQueue));
		list.add(this.addCmonentListiner(new JButton("sneak"), this::sneakDisplay));
		list.add(this.addCmonentListiner(new JButton("randomCollection"), this::randomCollection));
		list.add(this.addCmonentListiner(new JButton("ThreadList"), this::threadList));
		list.add(this.addCmonentListiner(new JButton("Frame Location"), this::location));
		list.add(this.addCmonentListiner(new JButton("Noise Screen"), this::noiseScreen));

		this.simpleColumn(fw.getContentPane(), list);

		fw.pack();
	}

	private final List<Integer> randomExample() {
		Random random = new Random(0);
		List<Integer> list = this.toArrayList ? new ArrayList<>() : new ArraySet<>();
		for (int i = 0; i < 10; i++) {
			list.add(random.nextInt(10));
		}
		return list;
	}

	private final void println() {
		System.out.println();
	}

	private final void println(Object object) {
		System.out.println(object);
	}

	private final void randomSort(JButton btn) {
		List<Integer> list = randomExample();
		this.println(list.getClass());
		this.println(list);
		list.sort(null);
		this.println(list);
	}

	private final void randomShuffle(JButton btn) {
		List<Integer> list = randomExample();
		this.println(list.getClass());
		this.println(list);
		Collections.shuffle(list);
		this.println(list);
		if (list instanceof ArraySet<Integer> set) {
			Random random = new Random(0);
			set.shuffle(random);
		} else {
			Collections.shuffle(list);
		}
		this.println(list);
	}

	private final void randomShuffleSort(JButton btn) {
		List<Integer> list = randomExample();
		this.println(list.getClass());
		this.println(list);
		Collections.shuffle(list);
		this.println(list);
		if (list instanceof ArraySet<Integer> set) {
			set.shuffle(MainFrame.RANDOM);
		} else {
			Collections.shuffle(list);
		}
		this.println(list);
		list.sort(null);
		this.println(list);
	}

	private final void readJar(JButton btn) {
		List<Path> list = JarUtil.getJarFolder("");
		this.println(list);
	}

	private final void quote(JButton btn) {
		String key = String.format(
			"{\"%s\":%s}", 
			"key", 
			BStringUtil.quote(String.format(
				"lorem %s ipsum", BStringUtil.quote(String.valueOf(1))
				)
			)
		);
		this.println();
		this.println(key);
		String quote = BStringUtil.quote(key);
		this.println(quote);
		this.println(BStringUtil.unQuote(quote));
	}

	private final void putLinkedMap(JButton btn) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("1", "a");
		map.put("2", "b");
		map.put("3", "c");
		map.put("1", "d");
		this.println(map);
	}

	private final void registry(JButton btn) {
		//init
		DynamicRegistry<String> registry = new CacheDynamicRegistry<>("key", null);
		registry.register("key1", "key1");
		registry.register("key2", "key2");
		registry.register("key3", "key3");
		String dynamic1 = "dynamic1";
		String dynamic2 = "dynamicB";
		registry.registerDynamic(dynamic1, "dynamicA", "1", "dynamic");
		registry.registerDynamic("dynamic2", dynamic2, "2", "dynamic");
		registry.registerDynamic("dynamic3", "dynamicC", "3", "dynamic");
		MainFrame.LOGGER.warn("start registry");
		System.out.println(registry.keys());
		System.out.println(registry.dynamic());

		// register existes
		MainFrame.LOGGER.debug("register existes");
		registry.registerDynamic("dynamic3", "dynamicC", "null/1");
		System.out.println(registry.dynamic());

		//modiy exists
		MainFrame.LOGGER.debug("modiy exists");
		registry.registerDynamicByKey(dynamic1, "null/2");
		registry.registerDynamicByValue(dynamic2, "null/3", null);
		System.out.println(registry.dynamic());

		//modify not exists
		MainFrame.LOGGER.debug("modify not exists");
		registry.registerDynamicByKey(dynamic2, "fake");
		System.out.println(registry.dynamic());

		//remove reasons
		MainFrame.LOGGER.debug("remove reasons");
		registry.unregisterByKey(dynamic1, "1", "null/4");
		System.out.println(registry.dynamic());
		registry.unregisterByValue(dynamic2, "2", null);
		System.out.println(registry.dynamic());
		registry.unregisterReason("dynamic");
		System.out.println(registry.dynamic());
	}

	/*private final void rgba(YPos y) {
		y.add(new RGBAPanel().create(this.getFrame(), 5, y.get(), true, true));
	}*/

	private final void collectionOfCollections(JButton btn) {
		Collection<Integer> collection = CollectionOfCollections.newCollection(Arrays.asList(1, 4, 5), Arrays.asList(4, 3, 10), Arrays.asList(50, 123, 51, 51, 6, 43));
		IntList list = new IntArrayList();
		for (int key : collection) {
			list.add(key);
		}
		println(collection);
		println(list);
		println(Arrays.asList(collection.toArray()));
		println(collection.stream().filter(e -> e % 2 == 1).toList());
		println(CollectionOfCollections.newSet(new LinkedHashSet<>(Arrays.asList(1, 4, 5)), new LinkedHashSet<>(Arrays.asList(4, 3, 10)), new LinkedHashSet<>(Arrays.asList(50, 123, 51, 51, 6, 43))));
		println(CollectionOfCollections.newList(Arrays.asList(1, 4, 5), Arrays.asList(4, 3, 10), Arrays.asList(50, 123, 51, 51, 6, 43)));
	}

	private final void concurrent(JButton button) {
		ConcurrentLinkedQueue<ConcurrentEntry> collection = new ConcurrentLinkedQueue<ConcurrentEntry>();
		collection.add(new ConcurrentEntry(1));
		println();
		for (ConcurrentEntry entry : collection) {
			this.println(entry);
		}
		this.println(collection);
		IntHolder i = new IntHolder(2);
		new BackgroundExecutorBuilder("Concurent", this.getFrame()).setExecute((oldProgress, executor) -> {
			sleep(500);
			iterateConcurrent(collection, i);
			int size = collection.size();
			if (size == 0) {
				executor.clear();
				collection.add(new ConcurrentEntry(1));
				this.println(collection);
			}
			return Math.min(size * 7, 100);
		}).run();
	}

	private final void iterateConcurrent(ConcurrentLinkedQueue<ConcurrentEntry> collection, IntHolder i) {
		int size = collection.size();
		if (size >= 100 || size == 0) {
			return;
		}
		Iterator<ConcurrentEntry> iterator = collection.iterator();
		this.println("start " + collection);
		while(iterator.hasNext()) {
			ConcurrentEntry entry = iterator.next();
			if (entry.bl) {
				this.println("iterate " + entry);
				if (RANDOM.nextInt(100) < 25) {
					iterator.remove();
				}
				if (RANDOM.nextInt(100) < 50) {
					collection.add(new ConcurrentEntry(i.getAsInt()));
					i.add();
				}
			} else {
				this.println("ignore " + entry);
				entry.bl = true;
			}
		}
	}

	private class ConcurrentEntry {
		public final int value;
		public boolean bl;

		public ConcurrentEntry(int value) {
			this.value = value;
		}

		@Override
		public final String toString() {
			return String.format("{%s=%s}", this.value, this.bl ? "+" : "-");
		}
	}

	private final void modifiedQueue(JButton button) {
		this.println();
		this.println();
		this.println();
		ModifiedQueue<QueueTask> queue = new ModifiedQueue<>();
		QueueTask task1 = () -> {
			println("1");
			return true;
		};

		queue.add(task1);
		queue.run();

		this.println();
		queue.add(() -> {
			println("2.1");
			queue.add(task1);
			println("2.2");
			return true;
		});
		queue.run();
		println("2.3");
		queue.run();

		this.println();
		IntHolder tuple = new IntHolder(11);
		queue.add(() -> {
			println("3." + (12-tuple.get()));
			tuple.remove();
			return tuple.get() <= 0;
		});
		for (int i = 0; i < 11; i++) {
			println("i " + (i + 1));
			queue.run();
		}
	}

	private final void sneakDisplay(JButton button) {
		SneakType[][] array = new SneakType[10][];
		for (int i = 0; i < array.length; i++) {
			array[i] = new SneakType[]{SneakType.empty, SneakType.empty, SneakType.empty, SneakType.empty, SneakType.empty, SneakType.empty, SneakType.empty, SneakType.empty, SneakType.empty, SneakType.empty};
		}
		System.out.println("sneak");

		array[1][4] = SneakType.eat;
		array[4][0] = SneakType.empty;
		array[4][1] = SneakType.sneak;
		array[3][1] = SneakType.sneak;
		array[2][1] = SneakType.sneak;

		int i = 0;
		for (SneakType[] sneakTypes : array) {
			System.out.println();
			System.out.print(i + "    |");
			for (SneakType type : sneakTypes) {
				switch (type) {
					case empty -> System.out.print(" - ");
					case sneak -> System.out.print(" * ");
					case eat -> System.out.print(" ■ ");
				}
			}
			i++;
		}
	}

	private final void randomCollection(JButton button) {
		this.randomCollection(false);
	}

	private final void randomCollection(boolean sort) {
		int iteration = 1_000_000;
		int size = 100;

		RandomCollection<Integer> collection = new RandomCollection<Integer>().autoSort();
		IntList list = new IntArrayList();
		for (int i = 0; i < size; i++) {
			list.add(i + 1);
		}
		Collections.shuffle(list, new Random(0));
		list.forEach(i -> collection.add(i, i));

		if (sort) {
			collection.sort();
		}
		int array[] = new int[size];
		IntHolder intTuple = new IntHolder(100 * iteration);

		long time = System.currentTimeMillis();

		new BackgroundExecutor.BackgroundExecutorBuilder("Title", this.getFrame()).runThread((executor) -> {
			boolean stop = false;
			try {
				while(intTuple.getAsInt() > 0) {
					intTuple.remove();
					array[collection.getRandom() - 1] += 1;
					if (executor.isStop()) {
						stop = true;
						break;
					}
				}
			} catch (Exception e) {
				stop = true;
			}
			executor.print(() -> {
				println(list);
				println();
				println(BStringUtil.toString(array));
				println();
				float a = array[size - 1];
				float b = array[0];
				println(a / iteration);
				println(b / iteration);
				println(sort + " " + (System.currentTimeMillis() - time));
			});
			if (!executor.isStop()) {
				if (!sort) {
					randomCollection(true);
				}
				if (stop) {
					executor.stop();
					return;
				}
			}
		}, () -> 100 - intTuple.getAsInt() / iteration);
	}

	private final void threadList(JButton button) {
		System.out.println(Thread.getAllStackTraces().keySet());
	}

	private final void location(JButton button) {
		System.out.println(this.getFrame().getLocation());
	}

	private final void noiseScreen(JButton button) {
		new NoiseScreen(this.getFrame());
	}


	private static enum SneakType {
		empty,
		sneak,
		eat;
	}

	@Override
	public final String getName() {
		return "Test";
	}

	@Override
	public final String fastKey() {
		return "test";
	}

	@Override
	public final int[] version() {
		return new int[]{};
	}
}
