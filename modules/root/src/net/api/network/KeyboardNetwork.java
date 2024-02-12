package net.api.network;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.w3e.base.PrintWrapper;

public class KeyboardNetwork extends Packet {

	private static final Int2ObjectArrayMap<Consumer<KeyboardNetwork>> HANDLERS = new Int2ObjectArrayMap<>();

	public static void register(Consumer<KeyboardNetwork> function) {
		register(0, function);
	}

	public static void register(int i, Consumer<KeyboardNetwork> function) {
		HANDLERS.put(i, function);
	}

	private static final List<KeyboardNetwork> LIST = new ArrayList<>();

	public static void init() {}

	static {
		Network.register(new KeyboardNetwork(KeyEvent.KEY_PRESSED, KeyEvent.VK_W, 0));
	}

	public final int id;
	public final int keyCode;
	public final int map;
	private boolean pressed = false;

	public KeyboardNetwork(int id, int keyCode, int map) {
		this.id = id;
		this.keyCode = keyCode;
		this.map = map;
	}

	@Override
	protected void write(PacketBuffer buffer) {
		buffer.writeInt(this.id);
		buffer.writeInt(this.keyCode);
		buffer.writeInt(this.map);
	}

	@Override
	protected Packet read(PacketBuffer buffer) {
		return new KeyboardNetwork(buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	public boolean isPressed() {
		return pressed;
	}

	@Override
	public void run() {
		Consumer<KeyboardNetwork> handler = HANDLERS.get(map);
		if (handler == null) {
			PrintWrapper.LOGGER.error("unknown key handler " + map);
		} else {
			KeyboardNetwork event = this;
			for (KeyboardNetwork saved : LIST) {
				if (saved.equals(this)) {
					if (this.id == KeyEvent.KEY_RELEASED) {
						LIST.remove(event);
					} else {
						event = saved;
						event.pressed = true;
					}
					break;
				}
			}
			if (event == this && this.id != KeyEvent.KEY_RELEASED) {
				LIST.add(this);
			}
			handler.accept(event);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof KeyboardNetwork event && (this.keyCode == event.keyCode && this.map == event.map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.keyCode, this.map);
	}

	private static final void send(KeyEvent key, int i) {
		Network.send(new KeyboardNetwork(key.getID(), key.getKeyCode(), i));
	}

	public static KeyAdapter create(int i) {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent key) {
				send(key, i);
			}

			@Override
			public void keyReleased(KeyEvent key) {
				send(key, i);
			}
        };
	}
}
