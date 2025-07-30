package net.w3e.wlib.json.formula.nodes;

import java.util.Iterator;
import java.util.List;

import net.skds.lib2.utils.AnsiEscape;
import net.w3e.wlib.json.formula.FormulaType;

public interface FormulaNode {
	FormulaType getType();

	default String buildString() {
		return toString();
	}

	default String buildConsoleString() {
		return AnsiEscape.BLUE.sequence + this.buildString() + AnsiEscape.GREEN.sequence;
	}

	default String command() {
		return this.toString();
	}

	default String buildStringConsole(List<FormulaNode> nodes) {
		if (nodes.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();

		builder.append(AnsiEscape.YELLOW.sequence).append('[');

		Iterator<FormulaNode> iterator = nodes.iterator();
		while (iterator.hasNext()) {
			FormulaNode next = iterator.next();
			builder.append(next.buildConsoleString());
			if (iterator.hasNext()) {
				//builder.append(AnsiEscape.GREEN.sequence);
				builder.append(" ");
			}
		}

		builder.append(AnsiEscape.YELLOW.sequence).append(']');

		return builder.toString();
	}

	static final FormulaType LEFT_BRACKET = FormulaType.LEFT_BRACKET;
	static final FormulaType RIGHT_BRACKET = FormulaType.RIGHT_BRACKET;
}
