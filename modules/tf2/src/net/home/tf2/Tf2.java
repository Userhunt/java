package net.home.tf2;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.api.ApiJsonHelper;
import net.api.window.FrameWin;
import net.api.window.IBackgroundExecutor;
import net.api.window.BackgroundExecutor.BackgroundExecutorBuilder;
import net.home.FrameObject;
import net.home.MainFrame;
import net.w3e.base.collection.ArraySet;
import net.w3e.base.holders.ObjectHolder;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.FileUtil;
import net.w3e.base.message.MessageUtil;

public class Tf2 extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new Tf2());
		MainFrame.run(args);
	}

	private final File INDEX = new File("tf2/index.html");
	private final File CALCULATED = new File("tf2/calculate.html");
	private final File GENERATED = new File("tf2/generate.html");

	public static final ApiJsonHelper JSON = new ApiJsonHelper("Tf2");
	public static final Gson GSON = BJsonUtil.GSON().registerTypeAdapter(Tf2RegistryObject.class, new Tf2RegistryObject.Tf2Deserializer()).create();

	private final Map<String, List<Tf2RegistryObject>> GROUPS = new LinkedHashMap<>();
	private final List<JCheckBox> BUTTONS = new ArrayList<>();
	private final Map<Tf2RegistryObject, Tf2Price> PRICES = new LinkedHashMap<>();

	private float dollar;
	private float min;
	private int attempt;

	@Override
	protected void init(FrameWin fw, List<String> args) {
		copyFromJar();

		initJson();

		initButtons(fw);
	}

	private final void copyFromJar() {
		replaceFile("tf2/style.css");
		replaceFile("tf2/script.js");
		replaceFile("tf2/favicon.jpg");
		replaceFile("tf2/index.html");
		File config = new File("tf2/config.json");
		if (!config.exists()) {
			FileUtil.copyFromJar(JSON.logger(), config);
		}
	}

	private final void replaceFile(String path) {
		File file = new File(path);
		file.delete();
		FileUtil.copyFromJar(JSON.logger(), file);
	}

	private final void initJson() {
		JsonObject json = FileUtil.readObject("tf2/config.json");
		//JsonObject json = FileUtil.readObjectFromJar("tf2/config.json");

		this.min = JSON.readFloat(json, "min", 0.0f, true);
		if (this.min < 0) {
			this.min = 0;
		}
		this.dollar = JSON.readFloat(json, "dollar", 1.0f, true);
		this.attempt = JSON.readInt(json, "attempt", 3, true);

		List<Tf2RegistryObject> list = JSON.readList(json, "items", e -> BJsonUtil.load(GSON, e, Tf2RegistryObject.class), "Tf2RegistryObject");
		Map<String, Tf2RegistryObject> MAP = new LinkedHashMap<>();
		this.GROUPS.clear();
		for (Tf2RegistryObject object : list) {
			if (MAP.containsKey(object.id())) {
				JSON.logger().warn(MessageUtil.KEY_DUPLICATE.createMsg(MAP.keySet(), object.id()));
			} else {
				MAP.put(object.id(), object);
			}
		}
		for (Entry<String, Tf2RegistryObject> entry : MAP.entrySet()) {
			Tf2RegistryObject value = entry.getValue();
			for (String group : value.group()) {
				list = this.GROUPS.get(group);
				if (list == null) {
					list = new ArrayList<>();
					this.GROUPS.put(group, list);
				}
				list.add(value);
			}
		}
	}

	private final void initButtons(FrameWin fw) {
		fw.setLayout(null);
		fw.setSize(fw.getWidth(), GROUPS.size() / 2 * 20);
		fw.setLayout(new BoxLayout(fw.getContentPane(), BoxLayout.X_AXIS));
		List<Component> BUTTONS1 = new ArrayList<>();
		List<Component> BUTTONS2 = new ArrayList<>();

		this.BUTTONS.clear();

		boolean bl = true;

		for (Entry<String, List<Tf2RegistryObject>> entry : GROUPS.entrySet()) {
			JCheckBox button = new JCheckBox(entry.getKey());
			button.setSize(100, 26);

			if (bl) {
				BUTTONS1.add(button);
			} else {
				BUTTONS2.add(button);
			}
			bl = !bl;

			BUTTONS.add(button);
		}

		{
			JButton button = new JButton("Calculate");
			button.setSize(130, 26);
			button.addActionListener(FrameWin.onClick(() -> this.calculate(fw)));
			BUTTONS1.add(button);
		}
		{
			JButton button = this.addCmonentListiner(new JButton("Generate"), e -> this.generate());
			button.setSize(130, 26);
			button.addActionListener(FrameWin.onClick(this::generate));
			BUTTONS2.add(button);
		}
		{
			JButton button = new JButton("Browse Calc");
			button.setSize(130, 26);
			button.addActionListener(FrameWin.onClick(() -> this.browse(CALCULATED)));
			BUTTONS1.add(button);
		}
		{
			JButton button = new JButton("Browse Gen");
			button.setSize(130, 26);
			button.addActionListener(FrameWin.onClick(() -> this.browse(GENERATED)));
			BUTTONS2.add(button);
		}

		JPanel panelLeft = new JPanel();
		panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
		this.simpleColumn(panelLeft, BUTTONS1);
		fw.add(panelLeft);

		fw.add(Box.createHorizontalStrut(10));
		
		JPanel panelRight = new JPanel();
		panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
		this.simpleColumn(panelRight, BUTTONS2);
		fw.add(panelRight);

		fw.pack();
	}

	private final Map<String, List<Tf2RegistryObject>> fillMap() {
		Map<String, List<Tf2RegistryObject>> list = new LinkedHashMap<>();
		for (JCheckBox box : BUTTONS) {
			if (box.isSelected() && GROUPS.keySet().contains(box.getText())) {
				String text = box.getText();
				list.put(text, GROUPS.get(text));
			}
		}
		return list;
	}

	private final Document getDocument() {
		replaceFile("tf2/index.html");
		try {
			return Jsoup.parse(INDEX, "UTF-8", "http://example.com/");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private final void saveDocument(File path, Document document) {
		FileUtil.writeString(path, document.toString());
	}

	private final void calculate(FrameWin fw) {
		Map<String, List<Tf2RegistryObject>> map = new LinkedHashMap<>();
		for (Entry<String, List<Tf2RegistryObject>> entry : Tf2.this.fillMap().entrySet()) {
			map.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}

		List<Tf2RegistryObject> set = new ArrayList<>();

		for (List<Tf2RegistryObject> values : map.values()) {
			set.addAll(values);
		}

		int size = set.size();

		IntHolder progress = new IntHolder();
		ObjectHolder<String> holder = new ObjectHolder<>("");

		List<Tf2Price> prices = new ArraySet.ArraySetStrict<>();

		if (!set.isEmpty()) {
			new BackgroundExecutorBuilder("Calculating", fw).setExecute((oldProgress, executor) -> {
				if (executor.isStop()) {
					return oldProgress;
				}
				// первый элемент
				String next = map.keySet().iterator().next();
				if (!holder.get().equals(next)) {
					System.out.println("group " + next);
					holder.set(next);
				}
				List<Tf2RegistryObject> list = map.get(next);
				// пусто, продолжаем
				if (list.isEmpty()) {
					map.remove(next);
					return oldProgress;
				}
				// получить
				Tf2Price price = this.getPrice(list.get(0), executor);
				if (price == null) {
					for (Entry<String, List<Tf2RegistryObject>> entry : map.entrySet()) {
						for (Tf2RegistryObject reg : entry.getValue()) {
							price = this.PRICES.get(reg);
							if (price != null) {
								prices.add(price);
							}
						}
					}
					executor.stop();
					return oldProgress;
				}
				// очистить очередь
				list.remove(0);
				if (list.isEmpty()) {
					map.remove(next);
				}

				// сохранить
				progress.add(1);
				prices.add(price);

				// результ
				if (progress.getAsInt() == size)	{
					return 100;
				}

				// результат
				return progress.getAsInt() * 100 / size;
			}).setDone(exe -> {
				setCalculated(prices);
			}).run();
		}
	}

	private final void setCalculated(List<Tf2Price> prices) {
		prices = new ArrayList<>(prices.stream().filter(price -> price.profit() * this.dollar >= this.min).toList());
		Collections.sort(prices);

		Document document = this.getDocument();
		Element table = document.getElementsByTag("table").get(0);
		String value = String.format("<tr><td>%s</td></tr>", parse(prices, 9));
		value = value.substring(0, 14) + " class=\"typedTable\"" + value.substring(14);

		table.html(value);

		saveDocument(CALCULATED, document);
	}

	private final Tf2Price getPrice(Tf2RegistryObject value, IBackgroundExecutor stop) {
		Tf2Price price = this.PRICES.get(value);
		if (price == null) {
			price = Tf2Price.get(value, stop, this.attempt);
			if (price != null) {
				this.PRICES.put(value, price);
			}
		}
		return price;
	}

	private final void generate() {
		String format = "<tr><td>%s</td></tr>";
		Map<String, List<Tf2RegistryObject>> list = this.fillMap();
		Document document = getDocument();
		Element table = document.getElementsByTag("table").get(0);
		String value = "";
		for (Entry<String, List<Tf2RegistryObject>> entry : list.entrySet()) {
			String content = "<table class=\"typedTable\">";
			content += String.format(format, entry.getKey());
			content += String.format(format, parse(entry.getValue(), 15));
			content += "</table>";
			value += String.format(format, content);
		}

		table.html(value);

		saveDocument(GENERATED, document);
	}

	private final void browse(File file) {
		try {
			java.awt.Desktop.getDesktop().browse(file.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final String parse(List<? extends Tf2IconImpl> values, int max) {
		String format = "<td class=\"tdClass\">%s</td>";
		String link = "<a onclick=\"openKillstreakUrlEng('%s')\"><img alt=\"%s\" src=\"https://steamcommunity-a.akamaihd.net/economy/image/fWFc82js0fmoRAP-qOIPu5THSWqfSmTELLqcUywGkijVjZULUrsm1j-%s/62fx62f\">%s</a>";
		String content = "<table><tr>";
		int i = 0;
		for (Tf2IconImpl value : values) {
			if (i >= max) {
				i = 0;
				content += "</tr><tr>";
			}
			Tf2RegistryObject self = value.self();
			content += String.format(format, String.format(link, self.link().replaceAll("'", "\\\\'"), self.id(), self.image(), value.more(this.dollar)));
			i++;
		}
		content += "</tr></table>";
		return content;
	}

	@Override
	public final String getName() {
		return "TF2";
	}

	@Override
	public final int[] version() {
		return new int[]{1,1,0};
	}
}
