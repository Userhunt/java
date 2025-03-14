package net.w3e.wlib.dungeon;

public class DungeonException extends RuntimeException {

	public DungeonException() {
		super();
	}

	public DungeonException(String message) {
		super(message);
	}

	public DungeonException(String message, Throwable cause) {
		super(message, cause);
	}

	public DungeonException(Throwable cause) {
		super(cause);
	}
}
