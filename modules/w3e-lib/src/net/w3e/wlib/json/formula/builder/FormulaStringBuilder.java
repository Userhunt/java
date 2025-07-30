package net.w3e.wlib.json.formula.builder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.w3e.wlib.json.formula.nodes.FormulaArgsNode;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;
import net.w3e.wlib.json.formula.nodes.FormulaMethodNode;
import net.w3e.wlib.json.formula.nodes.FormulaNode;
import net.w3e.wlib.json.formula.nodes.reader.FormulaTreeNode;
import net.w3e.wlib.json.formula.string.FormulaString;

@NoArgsConstructor
public class FormulaStringBuilder extends FormulaArgumentBuilder {

	private final StringBuilder command = new StringBuilder();
	@Getter
	private FormulaCastNode returnType;

	public FormulaStringBuilder(FormulaArgumentBuilder arguments) {
		this.putArgumentsFrom(arguments);
	}

	public String getCommand() {
		return this.command.toString();
	}

	public FormulaStringBuilder setReturnType(FormulaCastNode returnType) {
		this.returnType = returnType;
		return this;
	}

	public void add(FormulaNode node) {
		this.command.append(createCommand(node));
	}

	private String createCommand(FormulaNode node) {
		if (node instanceof FormulaArgsNode n) {
			if (!this.hasArgument(n.argument())) {
				// TODO
				throw new IllegalStateException("has no arguments '" + n.argument() + "'");
			}
			return node.command();
		} else if (node instanceof FormulaMethodNode n) {
			FormulaMethodSignature signature = n.getMethodSignature(this);
			FormulaMethodLink method = this.getMethod(signature);
			if (method == null) {
				// TODO
				throw new IllegalStateException("has no methods " + signature);
			}
			StringBuilder arguments = new StringBuilder();
			for (FormulaNode arg : n.nodes()) {
				arguments.append(createCommand(arg));
			}
			return method.getCaller().formatted(arguments);
		} else if (node instanceof FormulaTreeNode n) {
			StringBuilder arguments = new StringBuilder();
			for (FormulaNode treeNode : n.getNodes()) {
				arguments.append(createCommand(treeNode));
			}
			return arguments.toString();
		}
		return node.command();
	}

	public FormulaString build() {
		if (this.getReturnType() == null) {
			// TODO
			throw new IllegalStateException();
		}
		return new FormulaString(this.getReturnType().command() + "(" + this.getCommand() + ")", this.getReturnType(), this);
	}
}
