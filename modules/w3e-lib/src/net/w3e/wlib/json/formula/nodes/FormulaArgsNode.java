package net.w3e.wlib.json.formula.nodes;

import net.w3e.wlib.json.formula.FormulaType;

public record FormulaArgsNode(String argument) implements FormulaNode {

	@Override
	public FormulaType getType() {
		return FormulaType.ARGS;
	}

	@Override
	public String buildString() {
		return this.argument;
	}

	@Override
	public String command() {
		return this.argument;
	}

	/*public static boolean tryRead(FormulaReader reader, char c) {
		if (c != '{') {
			return false;
		}
		reader.skip();
		StringBuilder builder = new StringBuilder();
		while (reader.canRead()) {
			c = reader.read();
			if (c == ' ') {
				// TODO
				throw new IllegalStateException("argument has ' '");
			}
			if (c == '}') {
				if (builder.isEmpty()) {
					// TODO
					throw new IllegalStateException("argument is empty");
				}
				reader.skip();
				reader.add(new FormulaArgsNode(builder.toString()));
				return true;
			}
			builder.append(c);
			reader.skip();
		}
		// TODO
		throw new IllegalStateException("reach end of input while reading argument");
	}*/

}
