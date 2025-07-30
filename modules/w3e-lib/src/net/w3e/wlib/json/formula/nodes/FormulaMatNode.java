package net.w3e.wlib.json.formula.nodes;

import lombok.Getter;
import net.w3e.wlib.json.formula.FormulaType;
import net.w3e.wlib.json.formula.nodes.reader.FormulaReader;

public enum FormulaMatNode implements FormulaNode {
	INCREMENT("++"),
	DECREMENT("--"),

	SUM(2, "+"),
	SUB(2, "-"),

	MUL(1, "*"),
	DIV(1, "/"),
	MOD(1, "%"),

	NEGATE("~"),

	AND(4, "&"),
	OR(4, "|"),
	XOR(4, "^"),

	OFFSET_LEFT(3, "<<"),
	OFFSET_RIGHT(3, ">>"),
	;

	@Getter
	private final int priority;
	private final String node;

	FormulaMatNode(String node) {
		this(-1, node);
	}

	FormulaMatNode(int priority, String node) {
		this.priority = priority;
		this.node = node;
		/*int i = 0;
		for (String node : nodes) {
			if (node == null || node.isBlank()) {
				throw new IllegalStateException(LogUtil.IS_EMPTY_OR_NULL.createMsg(this + "[" + i + "]"));
			}
			i++;
		}*/
	}

	@Override
	public FormulaType getType() {
		return FormulaType.MAT;
	}

	@Override
	public String buildString() {
		return this.node;
	}

	@Override
	public String command() {
		return this.node;
	}

	boolean read(String remaining, FormulaReader reader) {
		if (remaining.startsWith(node)) {
			reader.skip(node.length());
			reader.add(this);
			return true;
		}
		return false;
	}

	public static boolean tryRead(FormulaReader reader) {
		String remaining = reader.remaining();
		for (FormulaMatNode mat : FormulaMatNode.values()) {
			if (mat.read(remaining, reader)) {
				return true;
			}
		}
		return false;
	}

	public Number calculate(Number a, Number b) {
		return switch (this) {
			case SUM -> {
				if (a instanceof Double || b instanceof Double) {
					yield a.doubleValue() + b.doubleValue();
				} else if (a instanceof Float || b instanceof Float) {
					yield a.floatValue() + b.floatValue();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() + b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() + b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() + b.shortValue());
				} else {
					yield (byte)(a.byteValue() + b.byteValue());
				}
			}
			case SUB -> {
				if (a instanceof Double || b instanceof Double) {
					yield a.doubleValue() - b.doubleValue();
				} else if (a instanceof Float || b instanceof Float) {
					yield a.floatValue() - b.floatValue();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() - b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() - b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() - b.shortValue());
				} else {
					yield (byte)(a.byteValue() - b.byteValue());
				}
			}
			case MUL -> {
				if (a instanceof Double || b instanceof Double) {
					yield a.doubleValue() * b.doubleValue();
				} else if (a instanceof Float || b instanceof Float) {
					yield a.floatValue() * b.floatValue();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() * b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() * b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() * b.shortValue());
				} else {
					yield (byte)(a.byteValue() * b.byteValue());
				}
			}
			case DIV -> {
				if (a instanceof Double || b instanceof Double) {
					yield a.doubleValue() / b.doubleValue();
				} else if (a instanceof Float || b instanceof Float) {
					yield a.floatValue() / b.floatValue();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() / b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() / b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() / b.shortValue());
				} else {
					yield (byte)(a.byteValue() / b.byteValue());
				}
			}
			case MOD -> {
				if (a instanceof Double || b instanceof Double) {
					yield a.doubleValue() % b.doubleValue();
				} else if (a instanceof Float || b instanceof Float) {
					yield a.floatValue() % b.floatValue();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() % b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() % b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() % b.shortValue());
				} else {
					yield (byte)(a.byteValue() % b.byteValue());
				}
			}
			case AND -> {
				if (a instanceof Double || b instanceof Double || a instanceof Float || b instanceof Float) {
					// TODO
					throw new IllegalStateException();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() & b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() & b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() & b.shortValue());
				} else {
					yield (byte)(a.byteValue() & b.byteValue());
				}
			}
			case OR -> {
				if (a instanceof Double || b instanceof Double || a instanceof Float || b instanceof Float) {
					// TODO
					throw new IllegalStateException();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() | b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() | b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() | b.shortValue());
				} else {
					yield (byte)(a.byteValue() | b.byteValue());
				}
			}
			case XOR -> {
				if (a instanceof Double || b instanceof Double || a instanceof Float || b instanceof Float) {
					// TODO
					throw new IllegalStateException();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() ^ b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() ^ b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() ^ b.shortValue());
				} else {
					yield (byte)(a.byteValue() ^ b.byteValue());
				}
			}
			case OFFSET_LEFT -> {
				if (a instanceof Double || b instanceof Double || a instanceof Float || b instanceof Float) {
					// TODO
					throw new IllegalStateException();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() << b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() << b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() << b.shortValue());
				} else {
					yield (byte)(a.byteValue() << b.byteValue());
				}
			}
			case OFFSET_RIGHT -> {
				if (a instanceof Double || b instanceof Double || a instanceof Float || b instanceof Float) {
					// TODO
					throw new IllegalStateException();
				} else if (a instanceof Long || b instanceof Long) {
					yield a.longValue() >> b.longValue();
				} else if (a instanceof Integer || b instanceof Integer) {
					yield a.intValue() >> b.intValue();
				} else if (a instanceof Short || b instanceof Short) {
					yield (short)(a.shortValue() >> b.shortValue());
				} else {
					yield (byte)(a.byteValue() >> b.byteValue());
				}
			}
			default -> null;
		};
	}
}
