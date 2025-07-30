package net.w3e.wlib.json.formula.nodes.reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.CustomLog;
import lombok.NonNull;
import net.skds.lib2.utils.AnsiEscape;
import net.w3e.wlib.json.formula.FormulaType;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;
import net.w3e.wlib.json.formula.nodes.FormulaMatNode;
import net.w3e.wlib.json.formula.nodes.FormulaMethodNode;
import net.w3e.wlib.json.formula.nodes.FormulaNode;
import net.w3e.wlib.json.formula.nodes.FormulaNumberNode;

import static net.w3e.wlib.json.formula.FormulaType.COMMA;
import static net.w3e.wlib.json.formula.FormulaType.LEFT_BRACKET;
import static net.w3e.wlib.json.formula.FormulaType.MAT;
import static net.w3e.wlib.json.formula.FormulaType.RIGHT_BRACKET;

@CustomLog
public class FormulaReader {

	private final String line;
	private final List<FormulaNode> nodes = new ArrayList<>();

	private int cursor;

	public FormulaReader(String line) {
		this.line = line;
	}

	public List<FormulaNode> readAll() {
		while (canRead()) {
			processRead();
		}

		FormulaType prev = FormulaType.ROOT;
		for (FormulaNode node : nodes) {
			FormulaType next = node.getType();
			if (!checkNext(prev, next)) {
				// TODO
				throw new IllegalStateException(prev + "->" + next);
			}
			prev = next;
		}

		return nodes;
	}

	private boolean checkNext(@NonNull FormulaType prev, FormulaType next) {
		switch (prev) {
			// IGNORE
			case ROOT, NODE -> {
				return true;
			}
			case LEFT_BRACKET -> {
				return next != RIGHT_BRACKET;
			}
			case RIGHT_BRACKET -> {
				return true;
			}
			case COMMA, MAT -> {
				if (prev == next) {
					return false;
				}
				if (prev == MAT && next == COMMA) {
					return false;
				}
			}
			case NUMBER, ARGS, METHOD -> {
				if (FormulaType.VALUE_LIST.contains(next)) {
					return false;
				}
			}
			case CAST -> {
				if (next == RIGHT_BRACKET) {
					return false;
				}
			}
		}
		return true;
	}

	private void processRead() {
		char c = this.read();

		if (c == '(') {
			if (FormulaCastNode.isCast(this)) {
				return;
			}
			this.add(LEFT_BRACKET);
			this.skip();
			return;
		}
		if (c == ')') {
			this.add(RIGHT_BRACKET);
			this.skip();
			return;
		}
		if (c == ' ') {
			this.skip();
			return;
		}
		if (c == ',') {
			this.add(FormulaType.COMMA);
			this.skip();
			return;
		}

		/*if (FormulaArgsNode.tryRead(this, c)) {
			return;
		}*/

		if (FormulaNumberNode.tryRead(this, c)) {
			return;
		}

		if (FormulaMatNode.tryRead(this)) {
			return;
		}

		if (FormulaMethodNode.tryRead(this, c)) {
			return;
		}

		this.skip();
		// TODO
		throw new IllegalStateException("skip '" + c + "'");
	}

	public void add(FormulaNode node) {
		this.nodes.add(node);
	}

	public boolean canRead() {
		return this.cursor < this.line.length();
	}

	public char read() {
		return this.line.charAt(this.cursor);
	}

	public boolean canRead(int i) {
		return this.cursor + i < this.line.length();
	}

	public char read(int i) {
		return this.line.charAt(this.cursor + i);
	}

	public String remaining() {
		return this.line.substring(this.cursor);
	}

	public void skip() {
		this.cursor++;
	}

	public void skip(int i) {
		this.cursor += i;
	}

	public void print() {
		StringBuilder builder = new StringBuilder();
		Iterator<FormulaNode> iterator = this.nodes.iterator();
		while (iterator.hasNext()) {
			FormulaNode node = iterator.next();
			builder.append(node.buildConsoleString());
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(AnsiEscape.DEFAULT.sequence);
		log.log(builder);
	}

	public FormulaTreeNode toTree() {
		FormulaTreeNode tree = new FormulaTreeNode();
		Iterator<FormulaNode> iterator = this.nodes.iterator();
		tree.collect(iterator);
		if (iterator.hasNext()) {
			// TODO
			throw new IllegalStateException();
		}

		return tree;
	}
}
