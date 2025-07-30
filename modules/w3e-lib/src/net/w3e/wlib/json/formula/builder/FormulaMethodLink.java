package net.w3e.wlib.json.formula.builder;

import java.lang.reflect.Type;

public record FormulaMethodLink(Type type, String caller, Class<?> staticClazz) {

	public String getCaller() {
		if (this.staticClazz != null) {
			return staticClazz.getSimpleName() + "." + caller;
		} else {
			return caller;
		}
	}
}
