package net.home.main.simple;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;

import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.w3e.base.BStringUtil;
import net.w3e.base.api.window.FrameWin;
import net.w3e.base.collection.ArraySet;
import net.w3e.base.collection.ArraySet.ArraySetStrict;
import net.w3e.base.jar.JarUtil;
import net.w3e.base.registry.DynamicRegistry;
import net.w3e.base.registry.DynamicRegistry.CacheDynamicRegistry;

public class TestScreen extends FrameObject {

	private boolean toArrayList;

	@Override
	protected void init(FrameWin fw, List<String> args) {

		this.width = 275;
		this.toArrayList = false;

		YPos y = new YPos(5);

		addCheckBox("toArrayList", e -> this.toArrayList = e.isSelected(), y, fw);
		addButton("Random Sort", this::randomSort, y, fw);
		addButton("Random Shuffle", this::randomShuffle, y, fw);
		addButton("Random Shuffle Sort", this::randomShuffleSort, y, fw);
		addButton("Read jar", this::readJar, y, fw);
		addButton("Quote string", this::quote, y, fw);
		addButton("PutLinkedMap", this::putLinkedMap, y, fw);
		addButton("Registry", this::registry, y, fw);
		this.rgba(fw, y);

		fw.setSize(300, y.get() + 41);
	}

	private List<Integer> randomExample() {
		Random random = new Random(0);
		ArraySetStrict<Integer> list = new ArraySetStrict<>();
		for (int i = 0; i < 10; i++) {
			list.add(random.nextInt(10) + 1);
		}
		return this.toArrayList ? new ArrayList<>(list) : list;
	}

	private void println() {}

	private void println(Object object) {
		System.out.println(object);
	}

	private void randomSort(JButton btn) {
		List<Integer> list = randomExample();
		this.println(list.getClass());
		this.println(list);
		list.sort(null);
		this.println(list);
	}

	private void randomShuffle(JButton btn) {
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

	private void randomShuffleSort(JButton btn) {
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

	private void readJar(JButton btn) {
		List<Path> list = JarUtil.getJarFolder("");
		this.println(list);
	}

	private void quote(JButton btn) {
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

	private void putLinkedMap(JButton btn) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("1", "a");
		map.put("2", "b");
		map.put("3", "c");
		map.put("1", "d");
		this.println(map);
	}

	private void registry(JButton btn) {
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

	private void rgba(FrameWin fw, YPos y) {
		y.add(new RGBAPanel().create(fw, 5, y.get(), true, true));
	}

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public String fastKey() {
		return "test";
	}

	@Override
	public int[] version() {
		return new int[]{};
	}
}
