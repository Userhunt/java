package net.home.main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;

import net.w3e.base.BStringUtil;
import net.w3e.base.api.window.FrameWin;
import net.w3e.base.collection.ArraySet;
import net.w3e.base.collection.ArraySet.ArraySetStrict;
import net.w3e.base.jar.JarUtil;
import net.w3e.base.tuple.number.WIntTuple;

public class TestScreen extends FrameObject {

	private boolean toArrayList;

	@Override
	protected void init(FrameWin fw, List<String> args) {

		this.width = 275;
		this.toArrayList = false;

		WIntTuple y = new WIntTuple(5);

		addCheckBox("toArrayList", e -> this.toArrayList = e.isSelected(), y, fw);
		addButton("Random Sort", this::randomSort, y, fw);
		addButton("Random Shuffle", this::randomShuffle, y, fw);
		addButton("Random Shuffle Sort", this::randomShuffleSort, y, fw);
		addButton("Read jar", this::readJar, y, fw);
		addButton("Quote string", this::quote, y, fw);
		addButton("PutLinkedMap", this::putLinkedMap, y, fw);

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

	private void randomSort(JButton btn) {
		List<Integer> list = randomExample();
		System.out.println(list.getClass());
		System.out.println(list);
		list.sort(null);
		System.out.println(list);
	}

	private void randomShuffle(JButton btn) {
		List<Integer> list = randomExample();
		System.out.println(list.getClass());
		System.out.println(list);
		Collections.shuffle(list);
		System.out.println(list);
		if (list instanceof ArraySet<Integer> set) {
			Random random = new Random(0);
			set.shuffle(random);
		} else {
			Collections.shuffle(list);
		}
		System.out.println(list);
	}

	private void randomShuffleSort(JButton btn) {
		List<Integer> list = randomExample();
		System.out.println(list.getClass());
		System.out.println(list);
		Collections.shuffle(list);
		System.out.println(list);
		if (list instanceof ArraySet<Integer> set) {
			set.shuffle(MainFrame.RANDOM);
		} else {
			Collections.shuffle(list);
		}
		System.out.println(list);
		list.sort(null);
		System.out.println(list);
	}

	private void readJar(JButton btn) {
		List<Path> list = JarUtil.getJarFolder("");
		System.out.println(list);
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
		System.out.println();
		System.out.println(key);
		String quote = BStringUtil.quote(key);
		System.out.println(quote);
		System.out.println(BStringUtil.unQuote(quote));
	}

	private void putLinkedMap(JButton btn) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("1", "a");
		map.put("2", "b");
		map.put("3", "c");
		map.put("1", "d");
		System.out.println(map);
	}

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public int[] version() {
		return new int[]{1,0,0};
	}
}
