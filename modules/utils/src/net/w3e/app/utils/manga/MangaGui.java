package net.w3e.app.utils.manga;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lombok.CustomLog;
import net.w3e.app.gui.AppJFrame;
import net.w3e.lib.utils.FileUtils;
import net.w3e.wlib.zip.ZipFileNode;
import net.w3e.wlib.zip.ZipFolderNode;
import net.w3e.wlib.zip.ZipNode;
import net.w3e.wlib.zip.ZipUtil;

@CustomLog
public class MangaGui extends AppJFrame {
	// 2 - 14.3

	private final JTextField input = new JTextField();
	private final JTextField output = new JTextField();

	//https://morovinger.github.io/vk4y/
	public MangaGui() {
		super("Manga");

		this.input.setText("D:\\Downloads\\");
		this.output.setText("D:\\Downloads\\out\\");

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		{
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel("Input"), BorderLayout.WEST);
			panel.add(this.input, BorderLayout.CENTER);
			this.add(panel);
			this.add(Box.createVerticalStrut(5));
		}
		{
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel("Output"), BorderLayout.WEST);
			panel.add(this.output, BorderLayout.CENTER);
			this.add(panel);
			this.add(Box.createVerticalStrut(5));
		}

		JButton button = new JButton("Run");
		button.addActionListener(_ -> {
			try {
				this.run();
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		this.add(button);
	}

	private void run() throws IOException {
		/*ConsoleFrame console = new ConsoleFrame(getTitle());
		console.initScreen();
		console.enableConsole();
		console.setSize(700, 500);
		console.setLocationRelativeTo(this);
		console.setVisible(true);*/
		File inputFile = new File(this.input.getText());
		if (!inputFile.exists()) {
			throw new FileNotFoundException("input : " + inputFile.getAbsolutePath());
		}
		if (!inputFile.isDirectory()) {
			throw new IllegalStateException("input : " + inputFile.getAbsolutePath() + " is not directory");
		}
		File outputFile = new File(this.output.getText());
		if (!outputFile.exists()) {
			outputFile.mkdirs();
		}
		if (!outputFile.isDirectory()) {
			throw new IllegalStateException("output : " + outputFile.getAbsolutePath() + " is not directory");
		}

		log.info("collect");
		List<FileData> files = new ArrayList<>();
		for (File file : inputFile.listFiles()) {
			if (file.isFile()) {
				continue;
			}
			String folder = file.getName();

			File outFolder = new File(outputFile, folder);
			if (!outFolder.exists()) {
				outFolder.mkdirs();
			}
			if (!outFolder.isDirectory()) {
				throw new IllegalStateException("output : " + outFolder.getAbsolutePath() + " is not directory");
			}
			if (outFolder.listFiles().length != 0) {
				throw new IllegalStateException("Output is not empty (%s)".formatted(folder));
			}
			List<FileData> localFiles = new ArrayList<>();
			for (File sub : file.listFiles()) {
				if (sub.getName().endsWith(".zip")) {
					BasicFileAttributes attributes = Files.readAttributes(sub.toPath(), BasicFileAttributes.class);
					localFiles.add(new FileData(sub, folder, attributes.creationTime().to(TimeUnit.SECONDS)));
				}
			}
			localFiles.sort(null);
			files.addAll(localFiles);
		}

		List<FileImage> outImages = new ArrayList<>();

		String folder = null;

		for (FileData object : files) {
			if (!object.folder.equals(folder)) {
				folder = object.folder;
				log.info("read " + folder);
			}
			ZipFolderNode zip = ZipUtil.read(object.file.toPath());
			Iterator<Entry<String, ZipNode>> iterator = zip.iterator();
			List<FileImage> images = new ArrayList<>();
			while (iterator.hasNext()) {
				ZipNode node = iterator.next().getValue();
				if (node instanceof ZipFileNode file && file.isImage()) {
					images.add(new FileImage(object.folder, file.getExtension(), file.getAsData()));
				} else {
					node.close();
					iterator.remove();
				}
			}
			images = images.reversed();
			outImages.addAll(images);
		}
		files.clear();

		folder = null;

		int i = 1;
		int size = String.valueOf(outImages.size()).length();
		for (FileImage image : outImages) {
			if (!image.folder.equals(folder)) {
				folder = image.folder;
				log.info("save " + folder);
				i = 1;
			}
			String name = String.format("%s/%0" + size + "d.%s", image.folder, i, image.extension);
			File file = new File(outputFile, name);
			FileUtils.save(file, image.data());
			i++;
		}
	}

	private record FileData(File file, String folder, long time) implements Comparable<FileData> {

		@Override
		public int compareTo(FileData o) {
			return Long.compare(this.time, o.time);
		}
	}

	private record FileImage(String folder, String extension, byte[] data) {
	}
}
