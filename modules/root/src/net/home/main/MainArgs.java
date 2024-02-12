package net.home.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.api.window.FrameWin;
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
		new MainArgs<>(ARGUENETS, new ArrayList<>(Arrays.asList(args)), frame, frames);
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

	public static abstract class MainArg<T extends FrameObject> {

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

	public static abstract class MainStringArg<T extends FrameObject> extends MainArg<T> {

		private final String def;

		public MainStringArg(String key, String def) {
			super(key);
			this.def = def;
		}

		@Override
		protected final boolean test(MainArgs<T> args, T frame, String value) {
			if (Strings.isNullOrEmpty(value)) {
				value = def;
			}
			return apply(args, frame, value);
		}

		protected abstract boolean apply(MainArgs<T> args, T frame, String value);
	}

	public static abstract class MainBoolArg<T extends FrameObject> extends MainArg<T> {

		private final Run def;

		public MainBoolArg(String key) {
			this(key, Run._true);
		}

		public MainBoolArg(String key, Run def) {
			super(key);
			this.def = def;
		}

		@Override
		@SuppressWarnings("unchecked")
		protected final boolean test(MainArgs<T> args, T frame, String value) {
			Run v = this.def;
			if (!Strings.isNullOrEmpty(value)) {
				value = value.toLowerCase();
				if (!value.startsWith("_")) {
					value = "_" + value;
				}
				try {
					v = Enum.valueOf((Class<Run>)this.def.getClass(), value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return apply(args, frame, v.mode);
		}

		protected abstract boolean apply(MainArgs<T> args, T frame, Boolean value);
	}

	public static enum Run {
		_true(true),
		_t(true),
		_y(true),
		_1(true),

		_false(false),
		_f(false),
		_n(false),
		_0(false),

		_null(null),
		_default(null),
		_def(null),
		_d(null),
		;

		private final Boolean mode;

		private Run(Boolean mode) {
			this.mode = mode;
		}
	}

	public static abstract class MainEnumArg<T extends FrameObject, V extends Enum<V>> extends MainArg<T> {

		private final V def;
		private final Class<V> clazz;

		@SuppressWarnings("unchecked")
		public MainEnumArg(String key, V def) {
			this(key, def, (Class<V>)def.getClass());
		}

		public MainEnumArg(String key, V def, Class<V> clazz) {
			super(key);
			this.def = def;
			this.clazz = clazz;
		}

		@Override
		protected final boolean test(MainArgs<T> args, T frame, String value) {
			V v = this.def;
			if (!Strings.isNullOrEmpty(value)) {
				try {
					v =  Enum.valueOf(this.clazz, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return this.test(args, frame, v);
		}

		protected abstract boolean test(MainArgs<T> args, T frame, V value);
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
