package net.w3e.wlib.json.formula.builder;

import java.lang.reflect.Type;
import java.util.List;

import lombok.CustomLog;

import net.w3e.wlib.json.formula.nodes.FormulaCastNode;

@CustomLog
public record FormulaMethodSignature(String name, boolean registry, List<? extends Type> args) {

	public FormulaMethodSignature(String name) {
		this(name, true, List.of());
	}

	public FormulaMethodSignature(String name, List<? extends Type> args) {
		this(name, true, args);
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == this) {
			return true;
		} else if (arg0 instanceof FormulaMethodSignature signature) {
			if (!this.name().equals(signature.name())) {
				return false;
			}
			if (this.args.size() != signature.args.size()) {
				return false;
			}
			if (this.registry() && signature.registry()) {
				return this.args().equals(signature.args());
			}
			if (this.registry() == signature.registry()) {
				// TODO
				throw new IllegalStateException();
			}
			for (int i = 0; i < this.args.size(); i++) {
				Type a = this.args.get(i);
				Type b = signature.args.get(i);
				if (a == b) {
					continue;
				} else if (a instanceof FormulaCastNode && b instanceof FormulaCastNode) {
					for (FormulaCastNode next : FormulaCastNode.REVERSED_ORDINAL) {
						if (b == next) {
							break;
						}
						if (a == next) {
							return false;
						}
					}
				} else {
					// TODO class extends other
					return false;
				}
			}
			//log.debug(this.args + " is " + signature.args);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.name().hashCode();
	}

	public FormulaMethodSignature toRegistry() {
		return new FormulaMethodSignature(this.name, this.args());
	}
}
