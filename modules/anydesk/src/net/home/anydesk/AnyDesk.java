package net.home.anydesk;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import net.api.ImageUtil;
import net.api.window.FrameWin;
import net.api.window.Inputs;
import net.api.window.WaitTimer;
import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.w3e.base.jar.JarUtil;

public class AnyDesk extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new AnyDesk());
		MainFrame.run(args);
	}

	private static final BufferedImage IMAGE = ImageUtil.read(JarUtil.getResourceAsStream("anydesk/image.png"));

	@Override
	protected void init(FrameWin fw, List<String> args) {
		fw.tick(5000, new WaitTimer.TimerExt() {
			@Override
			protected void runExt() {
				System.out.println(ImageUtil.difImagesNoAlpha(IMAGE, ImageUtil.capture(new Rectangle(0, 0, 1680 - 490, 1080 - 329))));
				if (ImageUtil.difImagesNoAlpha(IMAGE, ImageUtil.capture(new Rectangle(0, 0, 1680 - 490, 1080 - 329))) <= 1) {
					Inputs.click(580, 710);
					Inputs.click(1080, 280);
					Inputs.move(0, 0);
					sleep(1000 * 60 * 10);
				}
			}
		});
	}

	@Override
	public String getName() {
		return "AnyDesk";
	}

	@Override
	public int[] version() {
		return new int[]{1,0,0};
	}
}
