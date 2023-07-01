package net.home.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.w3e.base.api.window.FrameWin;
import net.w3e.base.collection.CollectionBuilder;
import net.w3e.base.json.BJsonUtil;

public class MainArgs<T extends FrameObject> {

	@SuppressWarnings({"unchecked","rawtypes"})
	public static final <T extends FrameObject> CollectionBuilder<MainArg<T>, List<MainArg<T>>> builder(Class<T> t) {
		return (CollectionBuilder<MainArg<T>, List<MainArg<T>>>)(CollectionBuilder)CollectionBuilder.list(MainArg.class);
	}

	private static final List<MainArg<FrameObject>> ARGUENETS = builder(FrameObject.class)
	.add(new MainArg<>("key") {
		@Override
		public boolean test(MainArgs<FrameObject> args, FrameObject frame, String value) {
			if (frame.fastKey().equals(value)) {
				args.init = () -> frame.run(args.main, args.args);
				return true;
			} else {
				return false;
			}
		}
	})
	.add(new MainIntArg<>("x") {
		@Override
		protected boolean test(MainArgs<FrameObject> args, FrameObject frame, int value) {
			if (value >= 0) {
				args.x = value;
			}

			return true;
		}
	})
	.add(new MainIntArg<>("y") {
		@Override
		protected boolean test(MainArgs<FrameObject> args, FrameObject frame, int value) {
			if (value >= 0) {
				args.y = value;
			}

			return true;
		}
	})
	.build();

	private final List<MainArg<T>> ARGS;
	private final List<String> args;
	public final FrameWin main;
	private final List<T> frames;
	private Supplier<FrameWin> init;
	private int x = 0;
	private int y = 0;

	public static final void main(String[] args, FrameWin frame, List<FrameObject> frames) {
		new MainArgs<>(ARGUENETS,  new ArrayList<>(Arrays.asList(args)), frame, frames);
	}

	public static final <T extends FrameObject> void main(List<MainArg<T>> ARGS, List<String> args, T frame) {
		new MainArgs<>(ARGS, args, null, Arrays.asList(frame));
		if (args.size() > 0) {
			MainFrame.LOGGER.warn(args);
		}
	}

	private MainArgs(List<MainArg<T>> ARGS, List<String> args, FrameWin main, List<T> frames) {
		this.ARGS = ARGS;
		this.args = args;
		this.main = main;
		this.frames = frames;
		init = () -> main;
		this.parse();
		this.run();
	}

	private final void parse() {
		Iterator<String> iterator = this.args.iterator();
		while(iterator.hasNext()) {
			String next = iterator.next();
			for (MainArg<T> arg : ARGS) {
				if (arg.run(this, next)) {
					iterator.remove();
				}
			}
		}
	}

	private final void run() {
		FrameWin win = init.get();
		if (this.x != 0 || this.y != 0) {
			win.setLocation(x, y);
		}
		for (MainArg<T> arg : ARGS) {
			for (T frame : frames) {
				arg.after(this, frame);
			}
		}
	}

	public abstract static class MainArg<T extends FrameObject> {

		private final String key;

		public MainArg(String key) {
			this.key = key + "=";
		}

		@SuppressWarnings("unchecked")
		private final boolean run(MainArgs<T> args, String next) {
			if (next.startsWith(this.key)) {
				next = next.substring(this.key.length());
				for (FrameObject frame : args.frames) {
					if (test(args, (T)frame, next)) {
						break;
					}
				}
				return true;
			}
			return false;
		}

		protected abstract boolean test(MainArgs<T> args, T frame, String value);

		protected void after(MainArgs<T> args, T frame) {}
	}

	public static abstract class MainIntArg<T extends FrameObject> extends MainArg<T> {

		public MainIntArg(String key) {
			super(key);
		}

		@Override
		protected final boolean test(MainArgs<T> args, T frame, String value) {
			try {
				return test(args, frame, Integer.parseInt(value));
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}

		protected abstract boolean test(MainArgs<T> args, T frame, int value);
	}

	public static abstract class MainListArg<T extends FrameObject, V> extends MainArg<T> {

		public MainListArg(String key) {
			super(key);
		}

		@Override
		protected final boolean test(MainArgs<T> args, T frame, String value) {
			try {
				JsonArray array = BJsonUtil.read(value.getBytes());
				parse(args, frame, array);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}

		protected abstract void parse(MainArgs<T> args2, T frame, JsonArray array);
	}

	public static abstract class MainListEnumArg<T extends FrameObject, V extends Enum<V>> extends MainListArg<T, V> {

		private final Class<V> v;

		public MainListEnumArg(String key, Class<V> v) {
			super(key);
			this.v = v;
		}

		@Override
		protected final void parse(MainArgs<T> args, T frame, JsonArray array) {
			List<V> list = new ArrayList<>();
			for (JsonElement element : array) {
				try {
					list.add(Enum.valueOf(this.v, element.getAsString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			apply(args, frame, list);
		}

		protected abstract void apply(MainArgs<T> args, T frame, List<V> list);
	}
}
