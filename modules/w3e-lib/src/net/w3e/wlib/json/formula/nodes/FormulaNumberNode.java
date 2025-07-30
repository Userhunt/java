package net.w3e.wlib.json.formula.nodes;

import java.util.Objects;

import net.skds.lib2.utils.Numbers;
import net.w3e.wlib.json.formula.FormulaType;
import net.w3e.wlib.json.formula.nodes.reader.FormulaReader;

public record FormulaNumberNode(Number value) implements FormulaNode {

	@Override
	public final FormulaType getType() {
		return FormulaType.NUMBER;
	}

	@Override
	public final String buildString() {
		return String.valueOf(this.value);
	}

	@Override
	public String command() {
		return String.valueOf(this.value);
	}

	public static boolean isNumber(char c) {
		return Character.isDigit(c) || c == '.';
	}

	public static boolean tryRead(FormulaReader reader, char c) {
		StringBuilder builder = null;
		if (c == '-') {
			int i = 1;
			while (reader.canRead(i)) {
				char c1 = reader.read(i);
				i++;
				if (isNumber(c1)) {
					builder = new StringBuilder();
					builder.append('-');
					builder.append(c1);
					reader.skip(i);
					if (reader.canRead()) {
						c = reader.read();
					}
					break;
				} else if (c1 == ' ') {
					continue;
				}
				break;
			}
		}
		if (isNumber(c)) {
			if (builder == null) {
				builder = new StringBuilder();
			}
			while (reader.canRead()) {
				c = reader.read();
				if (isNumber(c)) {
					reader.skip();
					builder.append(c);
				} else {
					break;
				}
			}
			reader.add(new FormulaNumberNode(Numbers.parseNumber(builder.toString())));
			return true;
		}
		return false;
	}

	public FormulaCastNode getCastType() {
		FormulaCastNode cast = FormulaCastNode.getCastType(this.value.getClass());
		// TODO
		Objects.requireNonNull(cast);
		return cast;
	}

}
