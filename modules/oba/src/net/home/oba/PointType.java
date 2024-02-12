package net.home.oba;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.api.ImageUtil;
import net.home.oba.OneBitAdventure.Point;
import net.w3e.base.RGBA;
import net.w3e.base.jar.JarUtil;

public abstract class PointType {

	public static final int BACKGROUND = -14804942;
	public static final int FLOOR = -3526600;
	public static final int ALPHA = 0;
	public static final int RED_127 = 2147418112;

	public static enum Direction {
		up,
		down,
		left,
		right;
	}

	public static final PointUnsetType UNSET = new PointUnsetType();
	public static final PointUnknownType UNKNOWN = new PointUnknownType();

	public static final PointUnsetEntityType UNSET_ENTITY = new PointUnsetEntityType();
	public static final PointEnemyType ENTITY = new PointEnemyType();
	public static final PointReaperEntityType REAPER_ENTITY = new PointReaperEntityType();
	public static final PointRangeEntityType RANGE_ENTITY = new PointRangeEntityType();
	public static final PointPlayerEntityType PLAYER = new PointPlayerEntityType();

	public static final PointEmptyType EMPTY = new PointEmptyType();

	public static final PointCoinType COIN = new PointCoinType();
	public static final PointItemType ITEM = new PointItemType();
	public static final PointRareItemType RARE_ITEM = new PointRareItemType();

	public static final PointWallType WALL = new PointWallType();

	public static final PointDoorType DOOR = new PointDoorType();
	public static final PointDoorClosedType DOOR_CLOSED = new PointDoorClosedType();
	public static final PointDoorAroundType DOOR_AROUND = new PointDoorAroundType();

	public static final PointChestType CHEST = new PointChestType();
	public static final PointBoxType BOX = new PointBoxType();

	public static final PointCampfireType CAMPFIRE = new PointCampfireType();
	public static final PointTraderType TRADER = new PointTraderType();
	public static final PointBlacksmithType BLACKSMITH = new PointBlacksmithType();
	public static final PointSignType SIGN = new PointSignType();
	public static final PointDungeonType DUNGEON = new PointDungeonType();
	public static final PointPotType POT = new PointPotType();

	public boolean canMove() {
		return true;
	}
	public boolean playerCanMove() {
		return canMove();
	}
	public boolean enemyCanMove() {
		return canMove();
	}

	protected PointType() {
		if (load()) {
			this.load(getName(), this.images);
		}
	}

	public double getReward(int x, int y) {
		return 1;
	}

	public abstract String getName();

	public abstract int color();

	protected final Map<String, BufferedImage> images = new LinkedHashMap<>();
	protected abstract boolean load();
	protected final void load(String name) {
		load(name, images);
	}
	protected final void load(String name, Map<String, BufferedImage> images) {
		List<Path> list = JarUtil.getJarFolder("oba/assets/" + name);
		for (Path path : list) {
			String key = path.toString();
			images.put(key, ImageUtil.read(JarUtil.getResourceAsStream(key)));
		}
	}

	protected final boolean isForPos(int x, int y, BufferedImage image, int posX, int posY) {
		boolean found = is(x, y, image, images.values());
		if (!found && x == posX && y == posY) {
			long time = System.currentTimeMillis();
			ImageUtil.save(image, "oba/rat/" + time);
			OneBitAdventure.MSG_UTIL.warn("oba/rat/" + time);
			return true;
		}
		return found;
	}
	public abstract boolean is(int x, int y, BufferedImage point);
	protected final boolean is(int x, int y, BufferedImage point, Collection<BufferedImage> images) {
		return is(x, y, point, images, 10);
	}

	protected final boolean is(int x, int y, BufferedImage point, Collection<BufferedImage> images, double chance) {
		for (BufferedImage image : images) {
			if (ImageUtil.difImagesNoAlpha(image, point) <= chance) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static class PointUnknownType extends PointType {
		@Override
		public boolean canMove() {
			return false;
		}
		@Override
		public String getName() {
			return "unknown";
		}
		@Override
		public int color() {
			return RGBA.BLACK.packBGRA();
		}
		@Override
		protected boolean load() {
			return false;
		}
		@Override
		public boolean is(int x, int y, BufferedImage point) {
			return false;
		}
	}

	public static class PointUnsetType extends PointType {
		@Override
		public boolean canMove() {
			return false;
		}
		@Override
		public String getName() {
			return "null";
		}
		@Override
		public int color() {
			return RGBA.WHITE.packBGRA();
		}
		@Override
		protected boolean load() {
			return false;
		}
		@Override
		public boolean is(int x, int y, BufferedImage point) {
			return false;
		}
	}

	public static class PointUnsetEntityType extends PointUnsetType {
		@Override
		public String getName() {
			return "null/entity";
		}
		@Override
		public int color() {
			return RED_127;
		}
	}

	public abstract static class PointEntityType extends PointInterractiveType {
		@Override
		public boolean playerCanMove() {
			return true;
		}
		@Override
		public double getReward(int x, int y) {
			return 2;
		}
		@Override
		public int color() {
			return RGBA.RED.packBGRA();
		}
		@Override
		public boolean interract() {
			// TODO Auto-generated method stub
			ObaMove.sleep(2000);
			return true;
		}
	}

	public static class PointEnemyType extends PointEntityType {
		@Override
		public String getName() {
			return "enemy";
		}
		@Override
		protected boolean load() {
			return false;
		}
		@Override
		public boolean is(int x, int y, BufferedImage point) {
			return true;
		}
	}

	public static class PointReaperEntityType extends PointEntityType {
		@Override
		public double getReward(int x, int y) {
			return 0.1;
		}
		@Override
		public String getName() {
			return "enemy/reaper";
		}
		@Override
		public int color() {
			return RGBA.DARK_RED.packBGRA();
		}
	}

	public static class PointRangeEntityType extends PointEntityType {
		@Override
		public String getName() {
			return "enemy/range";
		}
		@Override
		public int color() {
			return RGBA.ORANGE.packBGRA();
		}
	}

	public static class PointPlayerEntityType extends PointEntityType {
		@Override
		public double getReward(int x, int y) {
			return 0;
		}
		@Override
		public String getName() {
			return "player";
		}
		@Override
		public int color() {
			return RGBA.GREEN.packBGRA();
		}
		@Override
		protected boolean load() {
			return false;
		}
		public void reload(String texture) {
			this.images.clear();
			load("player/" +  texture);
		}
		/*@Override
		public boolean is(int x, int y, BufferedImage point) {
			// TODO_ Auto-generated method stub
			return isForPos(x, y, point, 2, 8);
		}*/
	}

	public static class PointEmptyType extends PointType {
		@Override
		public double getReward(int x, int y) {
			return 0;
		}
		@Override
		public String getName() {
			return "empty";
		}
		@Override
		public int color() {
			return RGBA.LIGHT_GRAY.packBGRA();
		}
		@Override
		protected boolean load() {
			return true;
		}
		@Override
		public boolean is(int x, int y, BufferedImage point) {
			return is(x, y, point, images.values());
		}
	}

	public abstract static class PointPickUpType extends PointEmptyType {
		@Override
		public final boolean is(int x, int y, BufferedImage point) {
			return is(x, y, point, images.values(), chance());
		}
		protected double chance() {
			return 10;
		}
	}

	public static class PointCoinType extends PointPickUpType {
		@Override
		public double getReward(int x, int y) {
			return 0.4;
		}
		@Override
		public String getName() {
			return "coin";
		}
		@Override
		public int color() {
			return RGBA.LIME.packBGRA();
		}
	}

	public static class PointItemType extends PointPickUpType {
		@Override
		public boolean enemyCanMove() {
			return false;
		}
		@Override
		public double getReward(int x, int y) {
			return 1.8;
		}
		@Override
		public String getName() {
			return "item";
		}
		@Override
		public int color() {
			return RGBA.YELLOW.packBGRA();
		}
	}

	public static class PointRareItemType extends PointItemType {
		@Override
		public double getReward(int x, int y) {
			return 10;
		}
		@Override
		public String getName() {
			return "rare_item";
		}
		@Override
		public int color() {
			return RGBA.ORANGE.packBGRA();
		}
	}

	public static class PointDoorType extends PointEmptyType {
		@Override
		public boolean enemyCanMove() {
			return false;
		}
		@Override
		public String getName() {
			return "door/open";
		}
		@Override
		public int color() {
			return RGBA.GRAY.packBGRA();
		}
	}

	public static class PointDoorClosedType extends PointDoorType {
		@Override
		public boolean canMove() {
			return false;
		}
		@Override
		public String getName() {
			return "door/closed";
		}
	}

	public static class PointDoorAroundType extends PointDoorType {

		public PointDoorAroundType() {
			load("empty");
		}

		public void updateAround(Point point) {
			updateAround(point, 0, -1);
			updateAround(point, 0, 1);
			updateAround(point, -1, 0);
			updateAround(point, 1, 0);
		}

		private void updateAround(Point point, int dX, int dY) {
			Point other = PointMap.get(point.x + dX, point.y + dY);
			if (other.getType() != PointType.UNKNOWN && !other.getType().playerCanMove() && other.test(this)) {
				other.set(EMPTY);
			}
		}

		@Override
		public boolean canMove() {
			return false;
		}
		@Override
		public String getName() {
			return "door/around";
		}
		@Override
		protected boolean load() {
			return false;
		}
	}

	public abstract static class PointAbstractWallType extends PointType {
		@Override
		public boolean canMove() {
			return false;
		}
		@Override
		protected boolean load() {
			return true;
		}
		@Override
		public boolean is(int x, int y, BufferedImage point) {
			return is(x, y, point, images.values());
		}
	}

	public static class PointWallType extends PointAbstractWallType {
		@Override
		public double getReward(int x, int y) {
			return 0;
		}
		@Override
		public String getName() {
			return "wall";
		}
		@Override
		public int color() {
			return RGBA.DARK_GRAY.packBGRA();
		}
	}

	public abstract static class PointInterractiveType extends PointAbstractWallType {
		@Override
		public abstract double getReward(int x, int y);
		@Override
		public abstract String getName();
		public abstract boolean interract();
	}

	public static class PointBoxType extends PointInterractiveType {
		@Override
		public double getReward(int x, int y) {
			return 1.5;
		}
		@Override
		public String getName() {
			return "interactive/box";
		}
		@Override
		public int color() {
			return RGBA.LIME.packBGRA();
		}
		@Override
		public boolean interract() {
			ObaMove.space();
			ObaMove.sleep(2000);
			return true;
		}
	}

	public static class PointChestType extends PointInterractiveType {
		@Override
		public double getReward(int x, int y) {
			return 4;
		}
		@Override
		public String getName() {
			return "interactive/chest";
		}
		@Override
		public int color() {
			return RGBA.ORANGE.packBGRA();
		}
		@Override
		public boolean interract() {
			// TODO Auto-generated method stub
			ObaMove.space();
			ObaMove.sleep(2000);
			return true;
		}
	}

	public static class PointCampfireType extends PointInterractiveType {
		@Override
		public double getReward(int x, int y) {
			return 4 + y * 3;
		}
		@Override
		public String getName() {
			return "interactive/campfire";
		}
		@Override
		public int color() {
			return RGBA.CYAN.packBGRA();
		}
		@Override
		public boolean interract() {
			// TODO Auto-generated method stub
			ObaMove.sleep(1000);
			ObaMove.space();
			ObaMove.sleep(2000);
			return true;
		}
	}

	public static class PointTraderType extends PointInterractiveType {
		@Override
		public double getReward(int x, int y) {
			return -10;
		}
		@Override
		public String getName() {
			return "interactive/trader";
		}
		@Override
		public int color() {
			return RGBA.CYAN.packBGRA();
		}
		@Override
		public boolean interract() {
			// TODO Auto-generated method stub
			//ObaMove.space();
			//ObaMove.wait(0);
			return true;
		}
	}

	public static class PointBlacksmithType extends PointInterractiveType {
		@Override
		public double getReward(int x, int y) {
			return -10;
		}
		@Override
		public String getName() {
			return "interactive/blacksmith";
		}
		@Override
		public int color() {
			return RGBA.CYAN.packBGRA();
		}
		@Override
		public boolean interract() {
			return true;
		}

		public boolean isSign(Point point, boolean right) {
			Point other = PointMap.get(point.x + (right ? 2 : -2), point.y);
			if (other.test(SIGN)) {
				int x = point.x + (right ? 1 : -1);
				PointMap.get(x, point.y).set(PointType.EMPTY);
				PointMap.get(x, point.y - 1).set(PointType.EMPTY);
				DUNGEON.update(PointMap.get(x, point.y - 1));
				return true;
			}
			return false;
		}
	}

	public static class PointSignType extends PointWallType {
		@Override
		public String getName() {
			return "interactive/sign";
		}
	}

	public static class PointDungeonType extends PointInterractiveType {

		protected final Map<String, BufferedImage> ore = new LinkedHashMap<>();

		public PointDungeonType() {
			String name = getName() + "/enter/";
			load(name + "ore", ore);
		}

		public void update(Point point) {
			point.set(this);
			if (is(point.x, point.y, point.image, ore.values())) {
				PointMap.setDungeon(point.x, point.y, PointMap.Dungeon.ore);
				return;
			}
		}
		@Override
		public double getReward(int x, int y) {
			return -10;
		}
		@Override
		public String getName() {
			return "dungeon";
		}
		@Override
		public int color() {
			return RGBA.GRAY.packBGRA();
		}
		@Override
		protected boolean load() {
			return false;
		}
		@Override
		public boolean interract() {
			// TODO Auto-generated method stub
			ObaMove.sleep(2000);
			return true;
		}
	}

	public static class PointPotType extends PointInterractiveType {
		@Override
		public double getReward(int x, int y) {
			return -10;
		}
		@Override
		public String getName() {
			return "pot";
		}
		@Override
		public int color() {
			return RGBA.CYAN.packBGRA();
		}
		@Override
		protected boolean load() {
			return true;
		}
		@Override
		public boolean interract() {
			// TODO Auto-generated method stub
			ObaMove.sleep(2000);
			return true;
		}
	}
}
