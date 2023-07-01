package net.w3e.base;

import java.util.ArrayList;
import java.util.List;

/**
 * 15.04.23
 */
public class StringGenerator {

	private final List<String> list = new ArrayList<>();
	private String last = null;
	private String split = "";

	public final StringGenerator push(Object object) {
		return push(object, null);
	}

	public final StringGenerator push(Object object, String split) {
		if (object == null) {
			return push("null", split);
		} else {
			return push(object.toString(), split);
		}
	}

	public final StringGenerator push(String string) {
		return push(string, null);
	}

	public final StringGenerator push(String string, String split) {
		if (string != null && !string.isEmpty()) {
			this.last = null;
			if (split != null && !split.isEmpty() && !this.list.isEmpty()) {
				string = split + string;
			}
			this.list.add(string);
		}
		return this;
	}

	public final String pop() {
		if (!this.isEmpty()) {
			this.last = null;
			return this.list.remove(this.list.size() - 1);
		}
		return null;
	}

	public final boolean isEmpty() {
		return this.list.isEmpty();
	}

	public final int size() {
		return this.list.size();
	}

	public final StringGenerator set(Object string) {
		this.clear();
		return this.push(string);
	}

	public final StringGenerator clear() {
		if (!this.isEmpty()) {
			this.last = null;
			this.list.clear();
		}
		return this;
	}

	public final String generate() {
		return generate(null);
	}

	public final String generate(String split) {
		if (split == null) {
			split = "";
		}
		if (!this.split.equals(split)) {
			this.last = null;
		}
		if (this.last == null) {
			this.last = "";
			for (String string : this.list) {
				this.last += string + split;
			}
		}
		return this.last;
	}

	public final String generateWith(String with) {
		return generateWith(null, with);
	}

	public final String generateWith(String split, String with) {
		return generate(split) + with;
	}
}
