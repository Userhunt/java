package net.w3e.wlib.json.formula.nodes;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import net.w3e.wlib.json.formula.FormulaType;
import net.w3e.wlib.json.formula.nodes.reader.FormulaReader;

public enum FormulaCastNode implements FormulaNode, Type {
	BYTE,
	SHORT,
	INT,
	LONG,
	FLOAT,
	DOUBLE
	;

	private final String node;

	private FormulaCastNode() {
		this.node = "(" + this.name().toLowerCase() + ")";
	}

	@Override
	public FormulaType getType() {
		return FormulaType.CAST;
	}

	@Override
	public String buildString() {
		return this.node;
	}

	@Override
	public String command() {
		return this.node;
	}

	@Override
	public String getTypeName() {
		return this.name().toLowerCase();
	}

	public Number cast(Number number) {
		return switch (this) {
			case BYTE -> number.byteValue();
			case SHORT -> number.shortValue();
			case INT -> number.intValue();
			case LONG -> number.longValue();
			case FLOAT -> number.floatValue();
			case DOUBLE -> number.doubleValue();
		};
	}

	public static FormulaCastNode getCastType(Class<?> clazz) {
		if (clazz == Byte.class || clazz == byte.class) {
			return FormulaCastNode.BYTE;
		} else if (clazz == Short.class|| clazz == short.class) {
			return FormulaCastNode.SHORT;
		} else if (clazz == Integer.class|| clazz == int.class) {
			return FormulaCastNode.INT;
		} else if (clazz == Long.class|| clazz == long.class) {
			return FormulaCastNode.LONG;
		} else if (clazz == Float.class|| clazz == float.class) {
			return FormulaCastNode.FLOAT;
		} else if (clazz == Double.class|| clazz == double.class) {
			return FormulaCastNode.DOUBLE;
		} else {
			return null;
		}
	}

	public Class<?> getPrimitiveClass() {
		return switch(this) {
			case BYTE -> byte.class;
			case SHORT -> short.class;
			case INT -> int.class;
			case LONG -> long.class;
			case FLOAT -> float.class;
			case DOUBLE -> double.class;
			// TODO
			default -> throw new IllegalStateException();
		};
	}

	boolean read(String remaining, FormulaReader reader) {
		if (remaining.startsWith(this.node)) {
			reader.skip(this.node.length());
			reader.add(this);
			return true;
		}
		return false;
	}

	public static boolean isCast(FormulaReader reader) {
		String remaining = reader.remaining();
		for (FormulaCastNode cast : FormulaCastNode.values()) {
			if (cast.read(remaining, reader)) {
				return true;
			}
		}
		return false;
	}

	public static final List<FormulaCastNode> REVERSED_ORDINAL;

	static {
		REVERSED_ORDINAL = List.copyOf(Arrays.asList(values()).reversed());
	}
}
