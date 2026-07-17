package net.w3e.app;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.skds.lib2.natives.AbstractLinkedLibrary;
import net.skds.lib2.natives.LinkerUtils;
import net.skds.lib2.natives.UpcallLink;
import net.skds.lib2.natives.struct.CStructWrapper;
import net.skds.lib2.natives.struct.WrappedCStruct;
import net.skds.lib2.natives.struct.annotation.StructMember;

import java.awt.*;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static net.skds.lib2.natives.LinkerUtils.*;

@SuppressWarnings("DataFlowIssue")
public class WUser32 extends AbstractLinkedLibrary {

	private static final WUser32 INSTANCE = new WUser32();

	private final MethodHandle getTopWindow = createHandle(this.lib, "GetTopWindow", PTR, PTR);
	private final MethodHandle getWindow = createHandle(this.lib, "GetWindow", PTR, PTR, INT);
	private final MethodHandle enumWindows = createHandle(this.lib, "EnumWindows", BOOLEAN, PTR, LONG);
	private final MethodHandle getWindowRect = createHandle(this.lib, "GetWindowRect", BOOLEAN, PTR, PTR);
	private final MethodHandle getWindowTextA = createHandle(this.lib, "GetWindowTextA", INT, PTR, PTR, INT);
	private final MethodHandle findWindowA = createHandle(this.lib, "FindWindowA", PTR, PTR, PTR);
	private final MethodHandle getForegroundWindow = createHandle(this.lib, "GetForegroundWindow", PTR);
	private final MethodHandle setForegroundWindow = createHandle(this.lib, "SetForegroundWindow", BOOLEAN, PTR);
	private final MethodHandle setFocus = createHandle(this.lib, "SetFocus", PTR, PTR);

	private WUser32() {
		super("user32");
	}

	public static long getHWnd(String name) {
		try (Arena arena = Arena.ofConfined()) {
			return (long) INSTANCE.findWindowA.invokeExact(0L, arena.allocateFrom(name).address());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static RectN getRect(Arena arena, long hWnd) {
		RectN rectN = RectN.WRAPPER.alloc(arena);
		try {
			boolean _ = (boolean) INSTANCE.getWindowRect.invokeExact(hWnd, rectN.getAddress());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		rectN.take();

		return rectN;
	}

	public static long getForegroundWindow() {
		try {
			return (long) INSTANCE.getForegroundWindow.invokeExact();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void setForegroundWindow(long hWnd) {
		try {
			boolean bl = (boolean) INSTANCE.setForegroundWindow.invokeExact(hWnd);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void listAllWindows() {
		try {
			final List<WindowInfo> inflList = new ArrayList<>();
			final List<Long> order = new ArrayList<>();
			long top = (long) INSTANCE.getTopWindow.invokeExact(0L);

			while (top != 0) {
				order.add(top);
				top = (long) INSTANCE.getWindow.invokeExact(top, 2);
			}

			try (Arena arena = Arena.ofConfined()) {
				MemorySegment bind = EnumWindowsProc.LINK.bind((hWnd, _) -> {
					inflList.add(getWindowInfo(arena, hWnd));
					return true;
				}, arena);
				boolean _ = (boolean) INSTANCE.enumWindows.invokeExact(bind.address(), 0L);
			}

			inflList.sort(Comparator.comparingInt((WindowInfo o) -> order.indexOf(o.hWnd)));
			for (WindowInfo w : inflList) {
				System.out.println(w);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFocus(long hWnd) {
		try {
			long _ = (long) INSTANCE.setFocus.invokeExact(hWnd);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void setFocusAndForeground(long hWnd) {
		try {
			long _ = (long) INSTANCE.setFocus.invokeExact(hWnd);
			boolean _ = (boolean) INSTANCE.setForegroundWindow.invokeExact(hWnd);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static WindowInfo getWindowInfo(Arena arena, long hWnd) {
		RectN r = getRect(arena, hWnd);
		MemorySegment buffer = arena.allocate(1024);
		try {
			int _ = (int) INSTANCE.getWindowTextA.invokeExact(hWnd, buffer.address(), 1024);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		String title = buffer.getString(0);
		return new WindowInfo(hWnd, r, title);
	}

	@FunctionalInterface
	public interface EnumWindowsProc {
		@SuppressWarnings("unused")
		boolean onWindowFound(long hwnd, long lParam);

		UpcallLink<EnumWindowsProc> LINK = LinkerUtils.createUpcallLink(EnumWindowsProc.class);
	}

	private static MethodHandle findVirtual(Class<?> refc, String name, MethodType type) {
		try {
			return MethodHandles.lookup().findVirtual(
					refc,
					name,
					type
			);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	static void main() {
		listAllWindows();
		try (Arena arena = Arena.ofConfined()) {
			var rect = getRect(arena, getHWnd("Path of Exile 2"));
			System.out.println(rect.getRectangle());
		}
	}

	public record WindowInfo(long hWnd, RectN rect, String title) {
		public String toString() {
			return String.format("%s (%d,%d)-(%d,%d) : \"%s\"", hWnd, rect.left, rect.top, rect.right, rect.bottom, title);
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RectN extends WrappedCStruct {

		@StructMember
		public int left, top, right, bottom;

		public Rectangle getRectangle() {
			return new Rectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
		}

		private static final CStructWrapper<RectN> WRAPPER = new CStructWrapper<>(RectN.class);

		@Override
		public CStructWrapper<? extends WrappedCStruct> getWrapper() {
			return WRAPPER;
		}
	}
}
