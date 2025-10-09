package net.w3e.wlib.json.formula.nodes.reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.skds.lib2.io.codec.SosisonUtils;
import net.w3e.wlib.collection.identity.IdentityLinkedHashSet;
import net.w3e.wlib.json.formula.FormulaType;
import net.w3e.wlib.json.formula.builder.FormulaArgumentBuilder;
import net.w3e.wlib.json.formula.builder.FormulaStringBuilder;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;
import net.w3e.wlib.json.formula.nodes.FormulaMatNode;
import net.w3e.wlib.json.formula.nodes.FormulaNode;
import net.w3e.wlib.json.formula.nodes.FormulaNumberNode;

import static net.w3e.wlib.json.formula.FormulaType.NODE;

@CustomLog
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class FormulaTreeNode implements FormulaNode {
	@Getter
	private final List<FormulaNode> nodes = new ArrayList<>();

	@Override
	public FormulaType getType() {
		return NODE;
	}

	public void collect(Iterator<FormulaNode> iterator) {
		while (iterator.hasNext()) {
			FormulaNode next = iterator.next();
			FormulaType type = next.getType();
			if (type == LEFT_BRACKET) {
				FormulaTreeNode tree = new FormulaTreeNode();
				this.nodes.add(tree);
				tree.nodes.add(LEFT_BRACKET);
				tree.collect(iterator);
				continue;
			}
			if (type == RIGHT_BRACKET) {
				this.nodes.add(RIGHT_BRACKET);
				return;
			}
			this.nodes.add(next);
		}
	}

	public void printAsJson() {
		System.out.println(SosisonUtils.toJson(this.getNodes()).replace("\t\"", "\"").replace("\t{", "{"));
	}

	@Override
	public String buildString() {
		return buildStringConsole(this.nodes);
	}

	public FormulaNode tryCombine() {
		for (int i = 0; i < nodes.size(); i++) {
			FormulaNode node = this.nodes.get(i);
			FormulaType type = node.getType();
			if (type == NODE) {
				this.nodes.set(i, ((FormulaTreeNode)node).tryCombine());
				continue;
			}
		}
		boolean bl = true;
		while (bl) {
			bl = false;
			bl = removeSingleBrackets() || bl;
			bl = removeCast() || bl;
			bl = removeMath() || bl;
		}

		if (this.nodes.size() == 1) {
			return this.nodes.getFirst();
		} else {
			return this;
		}
	}

	private boolean removeSingleBrackets() {
		boolean success = false;
		if (this.nodes.size() >= 3) {
			FormulaNode a = this.nodes.get(0);
			FormulaNode b = this.nodes.get(2);
			if (a == LEFT_BRACKET && b == RIGHT_BRACKET) {
				this.nodes.remove(2);
				this.nodes.remove(0);
				success = true;
			}
		}
		return success;
	}

	private boolean removeCast() {
		boolean success = false;
		int i = 0;
		while (this.nodes.size() >= 2 + i) {
			FormulaNode a = this.nodes.get(i + 0);
			FormulaNode b = this.nodes.get(i + 1);
			if (a instanceof FormulaCastNode castNode && b instanceof FormulaNumberNode numberNode) {
				this.nodes.remove(i);
				this.nodes.set(i, new FormulaNumberNode(castNode.cast(numberNode.value())));
			} else {
				i++;
			}
		}
		return success;
	}

	private boolean removeMath() {
		boolean success = false;
		root:
		while (this.nodes.size() >= 3 && this.nodes.getFirst() == LEFT_BRACKET && this.nodes.getLast() == RIGHT_BRACKET) {
			List<FormulaOperation> operations = new ArrayList<>();
			for (int i = 0; i < this.nodes.size(); i++) {
				if (!(this.nodes.get(i) instanceof FormulaMatNode mat)) {
					continue;
				}
				operations.add(new FormulaOperation(i, mat));
			}
			operations.sort(null);
			Iterator<FormulaOperation> iterator = operations.iterator();
			Set<FormulaNode> locked = new IdentityLinkedHashSet<>();
			while (iterator.hasNext()) {
				FormulaOperation operation = iterator.next();
				final int index = operation.index();
				final int leftIndex = index - 1;
				final FormulaNode leftNode = leftIndex >= 0 ? this.nodes.get(leftIndex) : null;
				final int rightIndex = index + 1;
				final FormulaNode rightNode = rightIndex < this.nodes.size() ? this.nodes.get(rightIndex) : null;
				if (operation.getPriority() == -1) {
					if (leftNode != null) {
						locked.add(leftNode);
					}
					if (rightNode != null) {
						locked.add(rightNode);
					}
					continue;
				}
				if (locked.contains(leftNode) || locked.contains(rightNode)) {
					continue;
				}
				if (leftNode instanceof FormulaNumberNode a && rightNode instanceof FormulaNumberNode b) {
					Number number = operation.node().calculate(a.value(), b.value());
					if (number != null) {
						this.nodes.remove(rightIndex);
						this.nodes.remove(index);
						this.nodes.set(leftIndex, new FormulaNumberNode(number));
						//log.debug(a.value() + operation.node().buildString() + b.value() + "=" + number);
						success = true;
						continue root;
					}
				}
			}
			break;
		}
		return success;
	}

	private record FormulaOperation(int index, FormulaMatNode node) implements Comparable<FormulaOperation> {

		public int getPriority() {
			return this.node.getPriority();
		}

		@Override
		public int compareTo(FormulaOperation o) {
			return Integer.compare(this.getPriority(), o.getPriority());
		}
	}

	public FormulaStringBuilder createStringBuilder(FormulaArgumentBuilder arguments) {
		FormulaStringBuilder builder = new FormulaStringBuilder(arguments);

		for (FormulaNode node : this.getNodes()) {
			builder.add(node);
		}

		return builder;
	}
}

