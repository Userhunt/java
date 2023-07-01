package net.w3e.base.math;

@FunctionalInterface
public interface WAlign {

	/*==================================== LEFT ====================================*/
	//top
	public static final WAlign leftTop = (a, w, h) -> {
		return new WAlignData(a.x1, a.y1);
	};

	//center
	public static final WAlign leftCenterExt = (a, w, h) -> {
		return new WAlignData(a.x1, BMatUtil.round((a.y1 + a.y2) / 2) - BMatUtil.round(h/2));
	};

	public static final WAlign leftCenter = (a, w, h) -> {
		return leftCenterExt.apply(a, 0, 0);
	};

	//bottom
	public static final WAlign leftBottom = (a, w, h) -> {
		return new WAlignData(a.x1, a.y2 - h - 1);
	};

	/*==================================== CENTER ====================================*/
	//top
	public static final WAlign centerTopExt = (a, w, h) -> {
		return new WAlignData(BMatUtil.round((a.x1 + a.x2 - w) / 2), a.y1);
	};

	public static final WAlign centerTop = (a, w, h) -> {
		return centerTopExt.apply(a, 0, h);
	};

	//center
	public static final WAlign centerCenterExt = (a, w, h) -> {
		return new WAlignData(BMatUtil.round((a.x1 + a.x2 - w) / 2), BMatUtil.round((a.y1 + a.y2 - h) / 2));
	};

	public static final WAlign centerCenterExtWidth = (a, w, h) -> {
		return centerCenterExt.apply(a, w, 0);
	};

	public static final WAlign centerCenterExtHeight = (a, w, h) -> {
		return centerCenterExt.apply(a, 0, h);
	};

	public static final WAlign centerCenter = (a, w, h) -> {
		return centerCenterExt.apply(a, 0, 0);
	};

	//bottom
	public static final WAlign centerBottomExt = (a, w, h) -> {
		return new WAlignData(BMatUtil.round((a.x1 + a.x2 - w) / 2), a.y2 - h - 1);
	};

	public static final WAlign centerBottom = (a, w, h) -> {
		return centerBottomExt.apply(a, 0, h);
	};

	/*==================================== RIGHT ====================================*/
	//top
	public static final WAlign rightTop = (a, w, h) -> {
		return new WAlignData(a.x2 - w, a.y1);
	};

	//center
	public static final WAlign rightCenterExt = (a, w, h) -> {
		return new WAlignData(a.x2 - w, BMatUtil.round((a.y1 + a.y2 - h) / 2));
	};

	public static final WAlign rightCenter = (a, w, h) -> {
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
}
