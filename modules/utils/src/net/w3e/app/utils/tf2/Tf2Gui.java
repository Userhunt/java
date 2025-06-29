package net.w3e.app.utils.tf2;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import lombok.CustomLog;
import net.skds.lib2.utils.Holders.ObjectHolder;
import net.w3e.app.gui.AppJFrame;
import net.w3e.app.gui.frame.ProgressFrame;
import net.w3e.lib.TFNStateEnum;
import net.w3e.lib.utils.FileUtils;
import net.w3e.lib.utils.ResourceUtil;
import net.w3e.wlib.log.LogUtil;

//https://github.com/Revadike/InternalSteamWebAPI/wiki
@CustomLog
public class Tf2Gui extends AppJFrame {

	private final File INDEX = new File("tf2/index.html");
	private final File CALCULATED = new File("tf2/calculate.html");
	private final File GENERATED = new File("tf2/generate.html");

	private final Map<String, List<Tf2RegistryObject>> groups = new LinkedHashMap<>();
	private final List<JCheckBox> buttons = new ArrayList<>();
	private final Map<Tf2RegistryObject, Tf2Price> prices = new LinkedHashMap<>();

	private final Tf2Config config = new Tf2Config();

	private ProgressFrame progressFrame;

	public Tf2Gui() {
		super("Tf2");
		copyFromJar();

		this.config.reload();

		initJson();

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));

		List<Component> BUTTONS1 = new ArrayList<>();
		List<Component> BUTTONS2 = new ArrayList<>();
		List<Component> BUTTONS3 = new ArrayList<>();

		this.buttons.clear();

		boolean bl = true;

		for (Entry<String, List<Tf2RegistryObject>> entry : groups.entrySet()) {
			JCheckBox button = new JCheckBox(entry.getKey());
			button.setSize(100, 26);

			if (bl) {
				BUTTONS1.add(button);
			} else {
				BUTTONS2.add(button);
			}
			bl = !bl;

			buttons.add(button);
		}

		{
			JButton button = new JButton("Calculate");
			button.setSize(130, 26);
			button.addActionListener(_ -> {
				this.calculate();
			});
			BUTTONS3.add(button);
		}
		{
			JButton button = new JButton("Generate");
			button.setSize(130, 26);
			button.addActionListener(_ -> {
				this.generate();
			});
			BUTTONS3.add(button);
		}
		{
			JButton button = new JButton("Browse Calc");
			button.setSize(130, 26);
			button.addActionListener(_ -> {
				this.browse(CALCULATED);
			});
			BUTTONS3.add(button);
		}
		{
			JButton button = new JButton("Browse Gen");
			button.setSize(130, 26);
			button.addActionListener(_ -> {
				this.browse(GENERATED);
			});
			BUTTONS3.add(button);
		}

		JPanel panelLeft = new JPanel();
		panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
		BUTTONS1.forEach(panelLeft::add);
		this.add(panelLeft);

		JPanel panelRight = new JPanel();
		panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
		BUTTONS2.forEach(panelRight::add);
		this.add(panelRight);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		BUTTONS3.forEach(buttonPanel::add);
		this.add(buttonPanel);
	}

	private void copyFromJar() {
		replaceFile("tf2/style.css");
		replaceFile("tf2/script.js");
		replaceFile("tf2/favicon.jpg");
		replaceFile("tf2/index.html");
		File config = new File("tf2/config.json");
		if (!config.exists()) {
			copyFromJar(config, "tf2/config.json");
		}
	}

	private void replaceFile(String path) {
		File file = new File(path);
		file.delete();
		copyFromJar(file, "tf2/" + file.getName());
	}

	private void copyFromJar(File file, String resource) {
		InputStream in = ResourceUtil.getResourceAsStream(resource);
		FileUtils.createParentDirs(file);
		try {
			Files.copy(in, file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	private void initJson() {
		Map<String, Tf2RegistryObject> MAP = new LinkedHashMap<>();
		this.groups.clear();
		for (Tf2RegistryObject object : config.getItems()) {
			if (MAP.containsKey(object.id())) {
				log.warn(LogUtil.KEY_DUPLICATE.createMsg(MAP.keySet(), object.id()));
			} else {
				MAP.put(object.id(), object);
			}
		}
		for (Entry<String, Tf2RegistryObject> entry : MAP.entrySet()) {
			Tf2RegistryObject value = entry.getValue();
			for (String group : value.group()) {
				List<Tf2RegistryObject> list = this.groups.get(group);
				if (list == null) {
					list = new ArrayList<>();
					this.groups.put(group, list);
				}
				list.add(value);
			}
		}
	}

	private void calculate() {
		if (this.progressFrame != null) {
			this.progressFrame.stopAndWait();
		}

		Set<Tf2RegistryObject> set = new LinkedHashSet<>();
		for (Entry<String, List<Tf2RegistryObject>> entry : this.fillMap().entrySet()) {
			set.addAll(entry.getValue());
		}

		float size = set.size();

		set.removeAll(this.prices.values().stream().filter(Tf2Price::isReady).map(Tf2Price::getReg).toList());

		ObjectHolder<TFNStateEnum> state = new ObjectHolder<>(TFNStateEnum.NOT_STATED);

		this.progressFrame = new ProgressFrame("Prices");
		if (set.size() > 0) {
			this.progressFrame.addTask(_ -> {
				if (state.getValue() == TFNStateEnum.NOT_STATED) {
					state.setValue(TFNStateEnum.FALSE);
				} else {
					if (state.getValue() == TFNStateEnum.TRUE) {
						try {
							Thread.sleep(this.config.getAwait());
						} catch (InterruptedException e) {
							e.printStackTrace();
							return (size - set.size()) / size;
						}
					}
					if (!set.isEmpty()) {
						Iterator<Tf2RegistryObject> iterator = set.iterator();
						Tf2Price price = new Tf2Price(iterator.next());
						this.prices.put(price.getReg(), price);
						try {
							price.load();
						} catch (IOException e) {
							log.warn(price.getReg().id());
							e.printStackTrace();
							return (size - set.size()) / size;
						}
						if (price.isReady()) {
							iterator.remove();
						}
					}
					if (state.getValue() == TFNStateEnum.FALSE) {
						state.setValue(TFNStateEnum.TRUE);
					}
				}

				return (size - set.size()) / size;
			});
		}
		this.progressFrame.addTask(_ -> {
			List<Tf2Price> prices = new ArrayList<>(this.prices.values().stream().filter(price -> price.isReady() && price.getProfit() >= this.config.getMin()).toList());
			Collections.sort(prices, Comparator.reverseOrder());

			String value = String.format("<tr><td>%s</td></tr>", parse(prices, 8));
			value = value.substring(0, 14) + " class=\"typedTable\"" + value.substring(14);
			try {
				String html = Files.readString(Path.of(INDEX.getAbsolutePath()));
				html = html.replace("<table></table>", "<table>%s</table>".formatted(value));
				FileUtils.save(CALCULATED, html.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("done");
			return 1;
		});
		this.progressFrame.initScreen();
		this.progressFrame.setVisible(true);
	}

	private Map<String, List<Tf2RegistryObject>> fillMap() {
		Map<String, List<Tf2RegistryObject>> list = new LinkedHashMap<>();
		for (JCheckBox box : buttons) {
			if (box.isSelected() && groups.keySet().contains(box.getText())) {
				String text = box.getText();
				list.put(text, groups.get(text));
			}
		}
		return list;
	}

	private final String parse(List<? extends Tf2Icon> values, int max) {
		String format = "<td class=\"tdClass\">%s</td>";
		String link = "<a onclick=\"openKillstreakUrlEng('%s')\"><img alt=\"%s\" src=\"https://steamcommunity-a.akamaihd.net/economy/image/fWFc82js0fmoRAP-qOIPu5THSWqfSmTELLqcUywGkijVjZULUrsm1j-%s/62fx62f\">%s</a>";
		String content = "<table><tr>";
		int i = 0;
		for (Tf2Icon value : values) {
			if (i >= max) {
				i = 0;
				content += "</tr><tr>";
			}
			Tf2RegistryObject self = value.getReg();
			content += String.format(format, String.format(link, self.link().replaceAll("'", "\\\\'"), self.id(), self.image(), value.text()));
			i++;
		}
		content += "</tr></table>";
		return content;
	}

	private void generate() {
		// TODO
	}

	private void browse(File file) {
		try {
			java.awt.Desktop.getDesktop().browse(file.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
