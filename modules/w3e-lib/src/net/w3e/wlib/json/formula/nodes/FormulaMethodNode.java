package net.w3e.wlib.json.formula.nodes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.skds.lib2.utils.AnsiEscape;
import net.w3e.wlib.json.formula.FormulaType;
import net.w3e.wlib.json.formula.builder.FormulaArgumentBuilder;
import net.w3e.wlib.json.formula.builder.FormulaMethodLink;
import net.w3e.wlib.json.formula.builder.FormulaMethodSignature;
import net.w3e.wlib.json.formula.nodes.reader.FormulaReader;
import net.w3e.wlib.json.formula.nodes.reader.FormulaTreeNode;

public record FormulaMethodNode(String method, List<FormulaNode> nodes) implements FormulaNode {

	@Override
	public FormulaType getType() {
		return FormulaType.METHOD;
	}

	@Override
	public String buildString() {
		return AnsiEscape.BLUE.sequence + this.method + "(" + buildStringConsole(this.nodes) + AnsiEscape.BLUE.sequence + ")";
	}

	private static boolean isMethodBody(char c) {
		return isMethod(c) || Character.isDigit(c);
	}

	private static boolean isMethod(char c) {
		return Character.isLetter(c) || c == '_';
	}

	public static boolean tryRead(FormulaReader reader, char c) {
		if (!isMethod(c)) {
			return false;
		}
		StringBuilder builder = new StringBuilder();

		String name = null;

		while (reader.canRead()) {
			c = reader.read();

			if (isMethodBody(c)) {
				builder.append(c);
				reader.skip();
				continue;
			}
			if (c == '(') {
				name = builder.toString();
				reader.skip();
				builder = new StringBuilder();
				break;
			}
			break;
		}

		if (name == null) {
			reader.add(new FormulaArgsNode(builder.toString()));
			return true;
		}

		int brackets = 0;
		while (reader.canRead()) {
			c = reader.read();
			if (c == '(') {
				builder.append(c);
				reader.skip();
				brackets++;
				continue;
			}
			if (c == ')') {
				reader.skip();
				if (brackets == 0) {
					break;
				} else {
					builder.append(c);
					brackets--;
				}
				continue;
			} else {
				builder.append(c);
				reader.skip();
			}
		}
		if (brackets != 0) {
			// TODO
			throw new IllegalStateException();
		}

		List<FormulaNode> nodes = List.of();
		if (!builder.isEmpty()) {
			FormulaReader innerReader = new FormulaReader(builder.toString());
			innerReader.readAll();
			FormulaNode combine = innerReader.toTree().tryCombine();
			nodes = new ArrayList<>();
			if (combine instanceof FormulaTreeNode treeNode) {
				nodes.addAll(treeNode.getNodes());
			} else {
				nodes.add(combine);
			}
			nodes = innerReader.readAll();
		}
		//method.nodes.add(RIGHT_BRACKET);

		reader.add(new FormulaMethodNode(name, nodes));
		return true;
	}

	public FormulaMethodSignature getMethodSignature(FormulaArgumentBuilder arguments) {
		List<Type> nodes = new ArrayList<>();
		for (FormulaNode node : this.nodes) {
			if (node instanceof FormulaNumberNode n) {
				nodes.add(n.getCastType());
			} else if (node instanceof FormulaMethodNode n) {
				FormulaMethodSignature signature = n.getMethodSignature(arguments);
				FormulaMethodLink link = arguments.getMethod(n.getMethodSignature(arguments));
				// TODO
				Objects.requireNonNull(link, "cant find method " + signature);
				nodes.add(link.type());
			} else if (node instanceof FormulaArgsNode n) {
				Type cast = arguments.getArgument(n.argument());
				// TODO
				Objects.requireNonNull(cast);
				nodes.add(cast);
			} else if (node instanceof FormulaMatNode) {
				// TODO
			} else if (node != FormulaType.COMMA) {
				System.err.println("unknown " + node.buildString());
			}
		}

		return new FormulaMethodSignature(this.method, false, nodes);
	}

}
