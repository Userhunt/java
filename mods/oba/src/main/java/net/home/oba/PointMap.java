package net.home.oba;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.home.oba.ObaConfig.ITexture;
import net.home.oba.OneBitAdventure.Point;
import net.home.oba.PointType.PointInterractiveType;
import net.w3e.base.math.BMatUtil;
import net.w3e.base.tuple.number.WIntTuple;

public class PointMap {
	private static final Int2ObjectArrayMap<Int2ObjectArrayMap<Point>> map = new Int2ObjectArrayMap<>();

	private static Point PLAYER = null;

	public static void init() {
		map.clear();
		for (int x = 0; x < 11; x++) {
			Int2ObjectArrayMap<Point> list = new Int2ObjectArrayMap<>();
			for (int y = 0; y < 15; y++) {
				list.put(y, emptyPoint(x, y, PointType.UNSET));
			}
			map.put(x, list);
		}
	}

	@SafeVarargs
	public static final void fill(Int2ObjectArrayMap<Int2ObjectArrayMap<Point>> map1, Int2ObjectArrayMap<Int2ObjectArrayMap<Point>>... map2) {
		map.clear();
		map.putAll(map1);
		PLAYER = null;

		// prepare
		iterate((x, y, point) -> {
			List<Point> points = new ArrayList<>();
			for (Int2ObjectArrayMap<Int2ObjectArrayMap<Point>> map3 : map2) {
				Point point2 = map3.get(x).get(y);
				if (!point.equalsPos(point2)) {
					throw new IllegalStateException("not equals pos" + point + " " + point2);
				}
				points.add(point2);
			}
			if (y == 1 || y == 14) {
				if (x >= 4 && x <= 6) {
					//надпись сверху
					point.set(PointType.UNKNOWN);
					return;
				} else if (y == 14 && x > 3 && x < 7) {
					//надпись снизу
					point.set(PointType.UNKNOWN);
					return;
				}
			}
			//моб
			for (Point point2 : points) {
				if (!point.equalsImage(point2)) {
					point.set(PointType.UNSET_ENTITY);
					break;
				}
			}
		});
		// clear background
		iterate((x, y, point) -> {
			if (point.getType() == PointType.UNKNOWN) {
				return;
			}
			WIntTuple background = new WIntTuple();
			WIntTuple floor = new WIntTuple();
			point.iterate((x1, y1, rgba) -> {
				if (rgba == PointType.BACKGROUND) {
					point.setPixel(x1, y1, PointType.ALPHA);
					background.increase();
				} else if (rgba == PointType.FLOOR) {
					floor.increase();
				}
			});
			if (background.get() + floor.get() / 1.25 >= point.sizeImage() * 0.95) {
				point.set(PointType.EMPTY);
			}
		});
		// simple objects
		iterate((x, y, point) -> {
			if (point.getType() == PointType.UNSET) {
				boolean bl = false
					|| point.test(PointType.RARE_ITEM)
					|| point.test(PointType.CAMPFIRE)
					|| point.test(PointType.POT)
					|| point.test(PointType.COIN)
					|| point.test(PointType.DOOR)
					|| point.test(PointType.DOOR_CLOSED)
					|| point.test(PointType.BOX)
					|| point.test(PointType.CHEST)
					|| point.test(PointType.WALL)
					|| point.test(PointType.TRADER)
					|| point.test(PointType.BLACKSMITH)
					|| point.test(PointType.ITEM)
				;
				if (bl) {}
			}
		});
		// compound objects and not found
		iterate((x, y, point) -> {
			PointType type = point.getType();
			if (type instanceof PointType.PointDoorType) {
				PointType.DOOR_AROUND.updateAround(point);
			}
			if (type == PointType.BLACKSMITH) {
				boolean bl = false
					|| PointType.BLACKSMITH.isSign(point, true)
					|| PointType.BLACKSMITH.isSign(point, false)
				;
				if (bl) {}
			}
			if (type == PointType.UNSET) {
				boolean bl = false
					|| point.test(PointType.EMPTY)
			;
			if (bl) {}
			}
		});
		// entity
		iterate((x, y, point) -> {
			if (point.getType() == PointType.UNSET_ENTITY) {
				boolean bl = false
					|| point.test(PointType.RANGE_ENTITY)
					|| point.test(PointType.REAPER_ENTITY)
					|| (point.test(PointType.PLAYER) && (PLAYER = point) != null)
					|| point.test(PointType.ENTITY)
				;
				if (bl) {}
			}
		});
		//refillReward(true);
		fillReward();
		//printReward();
	}

	public static final void step(boolean print, Consumer<String> consumer) {
		if (PLAYER != null) {
			if (!print) {
				ObaMove.focus();
			}
			List<Point> steps = aroundPoint(PLAYER, true, true);
			if (!steps.isEmpty()) {
				Point step = steps.get(0);
				if (PLAYER.y != step.y) {
					if (PLAYER.y > step.y) {
						if (print) {
							consumer.accept("move w");
						} else {
							ObaMove.w();
							if (PLAYER.y == 8 && step.getType().playerCanMove() && !(step.getType() instanceof PointType.PointEntityType)) {
								LAST_WASD = 1;
								ObaMove.sleep(5000);
							}
						}
					} else {
						if (print) {
							consumer.accept("move s");
						} else {
							LAST_WASD = 3;
							ObaMove.s();
						}
					}
				} else {
					if (PLAYER.x > step.x) {
						if (print) {
							consumer.accept("move a");
						} else {
							LAST_WASD = 2;
							ObaMove.a();
						}
					} else {
						if (print) {
							consumer.accept("move d");
						} else {
							LAST_WASD = 4;
							ObaMove.d();
						}
					}
				}
				if (!(step.getType() instanceof PointInterractiveType interractive && ObaPreset.interract(PLAYER, step, interractive))) {
					ObaMove.sleep(500);
				} else {
					LAST_WASD = 0;
				}
			}
			if (print) {
				ObaMove.sleep(1000);
			}
		}
	}

	public static Point get(int x, int y) {
		if (isIn(x, y)) {
			return map.get(x).get(y);
		} else {
			return emptyPoint(x, y, PointType.UNKNOWN);
		}
	}

	public static boolean isIn(int x, int y) {
		return x >= 0 && x <= 10 && y >= 0 && y <= 14;
	}

	public static void reset() {
		PointMap.setDungeon(0, 0, Dungeon.unset);
	}

	public static BufferedImage createImage() {
		BufferedImage image = new BufferedImage(11, 15, BufferedImage.TYPE_INT_ARGB);
		iterate((x, y, point) -> {
			image.setRGB(x, y, point.getType().color());
		});

		return image;
	}

	public static final void refillReward(boolean loop) {
		OneBitAdventure.MSG_UTIL.debug("refill");

		resetReward();
		fillReward();
		printReward();

		OneBitAdventure.MSG_UTIL.debug("end");
		if (loop) {
			ObaMove.sleep(5000);
			refillReward(loop);
		}
	}

	public static void printReward() {
		List<StringBuilder> builders = new ArrayList<>();
		for (int i = 0; i < 16; i++) {
			builders.add(new StringBuilder());
		}

		printReward(1, builders);
		for (StringBuilder builder : builders) {
			builder.append(" || ");
		}
		printReward(2, builders);

		formatReward(builders);

		for (StringBuilder builder : builders) {
			OneBitAdventure.MSG_UTIL.info(builder);
		}
	}

	public static void printReward(int mode, List<StringBuilder> builders) {
		String first = "0_1_2_3_4_5_6_7_8_9_10 ";
		if (mode == 1) {
			first = first.replaceAll("_", "  ");
		} else {
			first = first.replaceAll("_", "   ") + " ";
		}
		builders.get(0).append(first);
		iterate((x, y, point) -> {
			StringBuilder builder = builders.get(y + 1);
			Number number;
			if (mode == 1) {
				number = Math.min(99, point.path);
			} else {
				number = Math.min(999, BMatUtil.round((mode == 2 ? point.reward : point.base) * 10));
			}

			if (point.getType() == PointType.PLAYER) {
				if (mode == 1) {
					builder.append("--");
				} else {
					builder.append("---");
				}
			} else {
				if (number.doubleValue() > 0d) {
					int max = mode == 1 ? 10 : 100;
					if (number.doubleValue() < max) {
						builder.append("0");
						if (!(mode == 1) && number.doubleValue() < 10) {
							builder.append("0");
						}
					}
					builder.append(number);
				} else {
					if (mode == 1) {
						builder.append("  ");
					} else {
						builder.append("   ");
					}
				}
			}

			builder.append(" ");
		});
	}

	public static void formatReward(List<StringBuilder> builders) {
		List<StringBuilder> list = new ArrayList<>(builders);
		builders.clear();
		int y = -1;
		for (StringBuilder builder : list) {
			String string = builder.toString();
			builders.add(new StringBuilder().append(String.format("y(%s) %s", (y < 10 && y > -1) ? " " + y : y, string.substring(0, string.length() - 1))));
			y++;
		}
	}

	private static final void fillReward() {
		if (PLAYER == null) {
			return;
		}
		nextPath(PLAYER, 0, 0, 1);

		iterate((x, y, point) -> {
			if (point.path == -1) {
				point.base = -1;
			} else {
				point.base = ObaPreset.calculateReward(point);
			}
		});

		ObaPreset.finalizeReward(PLAYER);

		fillRewardUp(false);

		collectReward();

		int max = 10;

		while(true) {
			List<Point> steps = aroundPoint(PLAYER, true, false);
			if (steps.isEmpty()) {
				if (max == 9) {
					OneBitAdventure.MSG_UTIL.debug("cant find step");
				}
				fillRewardUp(true);
				collectReward();
			} else {
				break;
			}
			max--;
			if (max == 0) {
				OneBitAdventure.MSG_UTIL.warn("loop, cant find step");
				break;
			}
		}
	}

	private static void fillRewardUp(boolean second) {
		double d = 70;
		iterate((x, y, point) -> {
			double modify = d / (d + ((double)point.y) * (point.y > 8 ? 1.5 : 1));
			point.base *= modify;
			if (second) {
				double pos = 14 - point.y;
				point.base += 0.01 * pos * pos;
			}
		});
	}

	private static List<Point> aroundPoint(Point point, boolean reward, boolean sort) {
		List<Point> steps = new ArrayList<>();
		steps.add(get(point.x, point.y + 1));
		steps.add(get(point.x, point.y - 1));
		steps.add(get(point.x + 1, point.y));
		steps.add(get(point.x - 1, point.y));
		steps.removeIf(p -> !isIn(p.x, p.y) || p.path == -1 || (reward && p.reward <= 0.1));
		if (sort && steps.size() > 1) {
			Collections.sort(steps);
		}
		return steps;
	}

	/*private static final void negatePath() {
		WIntTuple max = new WIntTuple();
		iterate((x, y, point) -> {
			max.set(Math.max(max.get(), point.path));
		});
		iterate((x, y, point) -> {
			max.get();
			if (point.path > 0) {
				point.path = max.get() - point.path;
			}
		});
	}*/

	private static void generateTreePath(Point point, int path) {
		if (point.path == -1 || point.path > path) {
			point.path = path;
			path += 1;
			nextPath(point, 0, -1, path);
			nextPath(point, 0, 1, path);
			nextPath(point, -1, 0, path);
			nextPath(point, 1, 0, path);
		}
	}

	private static void nextPath(Point point, int dX, int dY, int path) {
		int x = point.x + dX;
		int y = point.y + dY;
		if (isIn(x, y)) {
			point = get(x, y);
			PointType type = point.getType();
			if (type.playerCanMove() || type instanceof PointInterractiveType) {
				generateTreePath(point, path);
			}
		}
	}

	private static void collectReward() {
		Int2ObjectAVLTreeMap<List<Point>> map = new Int2ObjectAVLTreeMap<>();
		iterate((x, y, point) -> {
			if (point.path > 1) {
				map.computeIfAbsent(-point.path, i -> new ArrayList<>()).add(point);
				point.reward = point.base / point.path * 0.95;
			}
		});

		for (List<Point> list : map.values()) {
			for (Point point : list) {
				collectRewardNext(point, 0, -1);
				collectRewardNext(point, 0, 1);
				collectRewardNext(point, -1, 0);
				collectRewardNext(point, 1, 0);
			}
		}
		if (LAST_WASD != 0) {
			double scale = 0.85;
			if (LAST_WASD == 1) {
				get(PLAYER.x, PLAYER.y + 1).reward *= scale;
			} else if (LAST_WASD == 2) {
				get(PLAYER.x + 1, PLAYER.y).reward *= scale;
			} else if (LAST_WASD == 3) {
				get(PLAYER.x, PLAYER.y - 1).reward *= scale;
			} else if (LAST_WASD == 4) {
				get(PLAYER.x - 1, PLAYER.y).reward *= scale;
			}
		}
	}

	private static void collectRewardNext(Point point, int dX, int dY) {
		int x = point.x + dX;
		int y = point.y + dY;
		if (isIn(x, y)) {
			Point other = get(x, y);
			if (other.path > 1 && other.path <= point.path) {
				other.reward += Math.max(0, point.reward);
				other.reward *= 0.9;
					/*(
					+ Math.max(0, point.reward))
					* 0.95*/
				;
			}
		}
	}

	private static final void resetReward() {
		iterate((x, y, point) -> {
			point.path = -1;
			point.base = 1;
			point.reward = 0;
		});
	}

	private static void iterate(IterateMap function) {
		iterate(false, function);
	}

	private static void iterate(boolean inverse, IterateMap function) {
		if (inverse) {
			for (int x = 10; x >= 0; x--) {
				for (int y = 14; y >= 0; y--) {
					try {
						function.iterate(x, y, get(x, y));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			for (int x = 0; x < 11; x++) {
				for (int y = 0; y < 15; y++) {
					try {
						function.iterate(x, y, get(x, y));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static Point emptyPoint(int x, int y, PointType type) {
		Point point = new Point(null, x, y);
		point.set(type);
		return point;
	}

	@FunctionalInterface
	private static interface IterateMap {
		void iterate(int x, int y, Point point);
	}

	/* ===================== LAST ====================== */

	private static int LAST_WASD = 0;

	/* ===================== DUNGEON ====================== */
	public static enum Dungeon implements ITexture {
		unset() {
			@Override
			public String texture() {
				return "";
			}
		},
		ore() {
			@Override
			public void update(int x, int y) {
				PointMap.get(x, y - 2).set(PointType.WALL);

				PointMap.get(x - 1, y - 1).set(PointType.WALL);
				PointMap.get(x, y - 1).set(PointType.WALL);
				PointMap.get(x + 1, y - 1).set(PointType.WALL);

				PointMap.get(x - 2, y).set(PointType.WALL);
				PointMap.get(x - 1, y).set(PointType.WALL);
				PointMap.get(x + 1, y).set(PointType.WALL);
				PointMap.get(x + 2, y).set(PointType.WALL);
			}
		};

		public void update(int x, int y) {}
	}

	private static Dungeon DUNGEON;

	public static void setDungeon(int x, int y, Dungeon dungeon) {
		if (dungeon != null) {
			PointMap.DUNGEON = dungeon;
			PointMap.DUNGEON.update(x, y);
		}
	}

	public static Dungeon getDungeon() {
		return PointMap.DUNGEON;
	}
}
