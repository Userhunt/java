package net.home.tf2;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.awt.Component;
import java.io.File;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.w3e.base.api.ApiJsonHelper;
import net.w3e.base.api.window.BackGroundExecutor;
import net.w3e.base.api.window.FrameWin;
import net.w3e.base.collection.ArraySet;
import net.w3e.base.json.BJsonUtil;
import net.w3e.base.json.FileUtil;
import net.w3e.base.message.MessageUtil;
import net.w3e.base.tuple.WTuple1;
import net.w3e.base.tuple.number.WIntTuple;

public class Tf2 extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new Tf2());
		System.out.println(args);
		MainFrame.run(args);
	}

	private final File HTML = new File("tf2/index.html");

	public static final ApiJsonHelper JSON = new ApiJsonHelper("Tf2");
	public static final Gson GSON = BJsonUtil.GSON().registerTypeAdapter(Tf2RegistryObject.class, new Tf2RegistryObject.Tf2Deserializer()).create();

	private final Map<String, List<Tf2RegistryObject>> GROUPS = new LinkedHashMap<>();
	private final List<Component> BUTTONS = new ArrayList<>();
	private final Map<Tf2RegistryObject, Tf2Price> PRICES = new LinkedHashMap<>();

	private float dollar;
	private float min;

	private final HttpClient client = HttpClient.newHttpClient();

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

		this.min = JSON.readFloat(json, "min", 0.0f, false);
		if (this.min < 0) {
			this.min = 0;
		}
		this.dollar = JSON.readFloat(json, "dollar", 1.0f, false);
		System.out.println(min);

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
		fw.setSize(fw.getWidth(), GROUPS.size() / 2 * 20);

		for (Component component : BUTTONS) {
			fw.remove(component);
		}
		this.BUTTONS.clear();

		boolean bl = false;
		int y = 0;

		for (Entry<String, List<Tf2RegistryObject>> entry : GROUPS.entrySet()) {
			JCheckBox button = new JCheckBox(entry.getKey());
			button.setBounds(10 + (bl ? 135 : 0), y, 100, 26);

			if (bl) {
				y += 30;
			}
			bl = !bl;

			BUTTONS.add(button);
		}

		if (bl) {
			y += 30;
		}

		{
			JButton button = new JButton("Calculate");
			button.setBounds(10, y, 130, 26);
			button.addActionListener(FrameWin.onClick(() -> {
				this.calculate(fw);
			}));
			BUTTONS.add(button);
		}
		{
			JButton button = new JButton("Generate");
			button.setBounds(145, y, 130, 26);
			button.addActionListener(FrameWin.onClick(this::generate));
			BUTTONS.add(button);
		}

		y += 30;

		for (Component component : BUTTONS) {
			fw.add(component);
		}

		fw.setSize(300, y + 40);
	}

	private final Map<String, List<Tf2RegistryObject>> fillMap() {
		Map<String, List<Tf2RegistryObject>> list = new LinkedHashMap<>();
		for (Component component : BUTTONS) {
			if (component instanceof JCheckBox box && box.isSelected() && GROUPS.keySet().contains(box.getText())) {
				String text = box.getText();
				list.put(text, GROUPS.get(text));
			}
		}
		return list;
	}

	private final Document getDocument() {
		try {
			return Jsoup.parse(HTML, "UTF-8", "http://example.com/");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private final void saveDocument(Document document) {
		FileUtil.writeString(HTML, document.toString());
	}

	private void calculate(FrameWin fw) {
		Map<String, List<Tf2RegistryObject>> map = new LinkedHashMap<>();
		for (Entry<String, List<Tf2RegistryObject>> entry : Tf2.this.fillMap().entrySet()) {
			map.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}

		List<Tf2RegistryObject> set = new ArrayList<>();

		for (List<Tf2RegistryObject> values : map.values()) {
			set.addAll(values);
		}

		int size = set.size();

		WIntTuple progress = new WIntTuple();
		WTuple1<String> last = new WTuple1<String>("");

		List<Tf2Price> prices = new ArraySet.ArraySetStrict<>();

		if (!set.isEmpty()) {
			BackGroundExecutor.run("Calculating", fw, false, (old) -> {
				// первый элемент
				String next = map.keySet().iterator().next();
				if (!last.get().equals(next)) {
					System.out.println("group " + next);
					last.set(next);
				}
				List<Tf2RegistryObject> list = map.get(next);
				// пусто, продолжаем
				if (list.isEmpty()) {
					map.remove(next);
					return old;
				}
				// получить
				Tf2Price price = getPrice(list.get(0));
				if (price == null) {
					for (Entry<String, List<Tf2RegistryObject>> entry : map.entrySet()) {
						for (Tf2RegistryObject reg : entry.getValue()) {
							price = this.PRICES.get(reg);
							if (price != null) {
								prices.add(price);
							}
						}
					}
					setCalculated(prices);
					return 100;
				}
				// очистить очередь
				list.remove(0);
				if (list.isEmpty()) {
					map.remove(next);
				}

				// сохранить
				progress.add(1);
				prices.add(price);

				// результа
				if (progress.get() == size)	{
					setCalculated(prices);
					return 100;
				}

				// результат
				return progress.get() * 100 / size;
			});
		}

		/*Thread thread = new Thread() {
			public void run() {
				String format = "<tr><td>%s</td></tr>";
				Map<String, List<Tf2RegistryObject>> map = Tf2.this.fillMap();

				List<Tf2Price> prices = new ArraySet.ArraySetStrict<>();
		
				Tf2Price price = null;
				for (List<Tf2RegistryObject> values : map.values()) {
					for (Tf2RegistryObject value : values) {
						price = getPrice(value);
						if (price == null) {
							break;
						}
						prices.add(price);
					}
					if (price == null) {
						break;
					}
				}
				if (price == null) {
					System.out.println("not all");
				}
		
				Document document = getDocument();
				Element table = document.getElementsByTag("table").get(0);
				String value = String.format(format, parse(prices, 10));
				value = value.substring(0, 14) + " class=\"typedTable\"" + value.substring(14);
		
				table.html(value);
		
				saveDocument(document);
			}
		};
		thread.start();*/
	}

	private final void setCalculated(List<Tf2Price> prices) {
		prices = new ArrayList<>(prices.stream().filter(price -> price.profit() * this.dollar >= this.min).toList());
		Collections.sort(prices);

		Document document = getDocument();
		Element table = document.getElementsByTag("table").get(0);
		String value = String.format("<tr><td>%s</td></tr>", parse(prices, 9));
		value = value.substring(0, 14) + " class=\"typedTable\"" + value.substring(14);

		table.html(value);

		saveDocument(document);
	}

	private final Tf2Price getPrice(Tf2RegistryObject value) {
		Tf2Price price = this.PRICES.get(value);
		if (price == null) {
			price = Tf2Price.get(client, value);
			this.PRICES.put(value, price);
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

		saveDocument(document);
	}

	private String parse(List<? extends Tf2IconImpl> values, int max) {
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
	public String getName() {
		return "TF2";
	}
}
