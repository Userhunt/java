package net.w3e.wlib.mat;

import lombok.AllArgsConstructor;
import net.skds.lib2.mat.FastMath;

@FunctionalInterface
public interface WAlign {

	/*==================================== LEFT ====================================*/
	//top
	public static final WAlign leftTop = (a, _, _) -> {
		return new WAlignData(a.x1, a.y1);
	};

	//center
	public static final WAlign leftCenterExt = (a, _, h) -> {
		return new WAlignData(a.x1, FastMath.round((a.y1 + a.y2) / 2) - FastMath.round(h/2));
	};

	public static final WAlign leftCenter = (a, _, _) -> {
		return leftCenterExt.apply(a, 0, 0);
	};

	//bottom
	public static final WAlign leftBottom = (a, _, h) -> {
		return new WAlignData(a.x1, a.y2 - h - 1);
	};

	/*==================================== CENTER ====================================*/
	//top
	public static final WAlign centerTopExt = (a, w, _) -> {
		return new WAlignData(FastMath.round((a.x1 + a.x2 - w) / 2), a.y1);
	};

	public static final WAlign centerTop = (a, _, h) -> {
		return centerTopExt.apply(a, 0, h);
	};

	//center
	public static final WAlign centerCenterExt = (a, w, h) -> {
		return new WAlignData(FastMath.round((a.x1 + a.x2 - w) / 2), FastMath.round((a.y1 + a.y2 - h) / 2));
	};

	public static final WAlign centerCenterExtWidth = (a, w, _) -> {
		return centerCenterExt.apply(a, w, 0);
	};

	public static final WAlign centerCenterExtHeight = (a, _, h) -> {
		return centerCenterExt.apply(a, 0, h);
	};

	public static final WAlign centerCenter = (a, _, _) -> {
		return centerCenterExt.apply(a, 0, 0);
	};

	//bottom
	public static final WAlign centerBottomExt = (a, w, h) -> {
		return new WAlignData(FastMath.round((a.x1 + a.x2 - w) / 2), a.y2 - h - 1);
	};

	public static final WAlign centerBottom = (a, _, h) -> {
		return centerBottomExt.apply(a, 0, h);
	};

	/*==================================== RIGHT ====================================*/
	//top
	public static final WAlign rightTop = (a, w, _) -> {
		return new WAlignData(a.x2 - w, a.y1);
	};

	//center
	public static final WAlign rightCenterExt = (a, w, h) -> {
		return new WAlignData(a.x2 - w, FastMath.round((a.y1 + a.y2 - h) / 2));
	};

	public static final WAlign rightCenter = (a, w, _) -> {
		return rightCenterExt.apply(a, w, 0);
	};

	//bottom
	public static final WAlign rightBottom = (a, w, h) -> {
		return new WAlignData(a.x2 - w, a.y2 - h - 1);
	};

	WAlignData apply(WIntRectangle area, int width, int height);

	public static record WAlignData(int x, int y) {

		public WAlignData offset(int x, int y) {
			return new WAlignData(this.x + x, this.y + y);
		}

		@Override
		public String toString() {
			return String.format("{x:%s,y:%s}", x, y);
		}
	}

	@AllArgsConstructor
	public static enum WAlignEnum {
		LT(leftTop),
		LC(leftCenter),
		LC_E(leftCenterExt),
		LB(leftBottom),

		CT(centerTop),
		CT_E(centerTopExt),
		CC(centerCenter),
		CC_E(centerCenterExt),
		CC_EW(centerCenterExtWidth),
		CC_EH(centerCenterExtHeight),
		CB_E(centerBottomExt),
		CB(centerBottom),

		RT(rightTop),
		RC(rightCenter),
		RC_E(rightCenterExt),
		RB(rightBottom),

		;

		public final WAlign function;
	}
}
