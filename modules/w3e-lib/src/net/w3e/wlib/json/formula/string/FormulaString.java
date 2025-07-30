package net.w3e.wlib.json.formula.string;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

import lombok.Getter;

import java.util.Set;

import net.w3e.wlib.json.formula.builder.FormulaArgumentBuilder;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;

public class FormulaString {

	@Getter
	private final String command;
	@Getter
	private final FormulaCastNode returnType;
	private final FormulaArgumentBuilder arguments;

	public FormulaString(String command, FormulaCastNode type, FormulaArgumentBuilder builder) {
		this.command = command;
		this.returnType = type;
		this.arguments = builder.clone();
	}

	public Set<Entry<String, Type>> getArgs() {
		return this.arguments.getArguments();
	}

	public List<String> getMethodsForImport() {
		return this.arguments.getMethods().stream().map(Entry::getValue).filter(e -> e.staticClazz() != null).map(e -> e.staticClazz().getName()).toList();
	}
}
