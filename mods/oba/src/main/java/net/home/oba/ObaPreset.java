package net.home.oba;

import net.home.oba.OneBitAdventure.Point;
import net.home.oba.PointType.PointInterractiveType;

public class ObaPreset {

	public static double calculateReward(Point point) {
		return point.getType().getReward(point.x, point.y) / ((double)point.path) * 50;
	}

	public static boolean interract(Point player, Point step, PointInterractiveType type) {
		return type.interract();
	}

	public static void finalizeReward(Point player) {

	}

}
