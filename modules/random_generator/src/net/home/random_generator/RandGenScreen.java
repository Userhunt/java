package net.home.random_generator;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;

import net.api.window.BackgroundExecutor;
import net.api.window.FrameWin;
import net.api.window.ImageScreen;
import net.api.window.BackgroundExecutor.BackgroundExecutorBuilder;
import net.home.main.FrameObject;
import net.home.main.MainFrame;
import net.w3e.base.dungeon.DungeonGenerator;
import net.w3e.base.dungeon.DungeonRoomInfo;
import net.w3e.base.holders.number.IntHolder;
import net.w3e.base.math.vector.WBox;
import net.w3e.base.math.vector.WDirection;
import net.w3e.base.math.vector.WVector3;

public class RandGenScreen extends FrameObject {

	public static void main(String[] args) {
		MainFrame.register(new RandGenScreen());
		MainFrame.run(args);
	}

	private BackgroundExecutor backgroundExecutor;
	private ImageScreen image;

	protected void init(FrameWin fw, List<String> args) {
		YPos y = new YPos();

		for (int i = 0; i < 5; i++) {
			long seed = i;
			addButton("" + i, btn -> exampleDungeon(btn, seed), y);
		}

		fw.setSize(300, y.get() + 41);
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
			System.out.println(String.format("[%s] rooms %s, connections:[%s,%s]", holder.getAsInt(), rooms, hard, soft));
			this.image.repaint();
			sleep(1000);

			if (holder.getAsInt() >= 16) {
				executor.stop();
			}

			return progress;
		}).setParentVisible(true).setUpdateParentPosition(false).build();
		this.backgroundExecutor.setLocation(Math.max(this.image.getX() + this.image.getWidth(), fw.getX() + fw.getWidth())  - 5, fw.getY());

		this.backgroundExecutor.run();
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
