package net.w3e.app.main;

import javax.swing.JFrame;

import net.w3e.app.gui.AppJFrame.AppJFrameNamedFactory;
import net.w3e.app.main.MainFrame.Args;

public class TestFrameFactory implements AppJFrameNamedFactory {

	@Override
	public String keyName() {
		return "Test";
	}

	@Override
	public JFrame build(JFrame parent, Args args) {
		return null;
	}

	/*
		String root = "D:/sd/SD-World-Mod-skydex-server/modules/skydex-client/run_test/resourcepacks/rpSkyDex/assets/sd/textures/decor/cp";
		try {
			FileInputStream input1 = new FileInputStream(new File(root, "neutral_top.png"));
			BufferedImage png1 = ImageUtils.readPNG(input1);
			input1.close();

			Map<Integer, Integer> map = new HashMap<>();
			map.put(0, 0);
			map.put(0xFF_394047, 0xFF_544B4D);
			map.put(0xFF_2A3038, 0xFF_3A3336);
			map.put(0xFF_16151C, 0xFF_1E171B);
			map.put(0xFF_4D575E, 0xFF_72696A);

			map.put(0xFE_75FAFF, 0xFE_FF4252);
			map.put(0xFE_4FE3F7, 0xFE_EA2E47);
			map.put(0xFE_40C3EF, 0xFE_D31350);

			map.put(0xFF_EAF5FF, 0xFE_1E171B);
			map.put(0xFF_D2DFF7, 0xFE_17161E);
			map.put(0xFF_A0B3DB, 0xFE_110B10);
			map.put(0xFF_B2C4E8, 0xFE_272733);

			JImageFrame frame = new JImageFrame("root", png1.getWidth(), png1.getHeight(), 15, Color.BLACK);

			for (int x = 0; x < png1.getWidth(); x++) {
				for (int y = 0; y < png1.getHeight(); y++) {
					int rgb = png1.getRGB(x, y);
					rgb = map.getOrDefault(rgb, 0);
					frame.setColor(x, y, rgb);
				}
			}

			frame.initScreen();
			frame.setVisible(true);
			frame.updateImage();

			byte[] data = ImageUtils.writeImageToArrayPng(frame.getImage());
			FileUtils.save(new File(root, "sp_top.png"), data);
		} catch (Exception e) {
		e.printStackTrace();
		}
	 */
}
