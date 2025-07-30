package net.w3e.wlib.json.formula.string;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import net.skds.lib2.misc.clazz.StringClassLoader;
import net.skds.lib2.reflection.ReflectUtils;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;

@CustomLog
@RequiredArgsConstructor
public class FormulaStringClass {

	private final String name;
	private final String folder;
	private final Map<FormulaKey, FormulaValue> formulas = new HashMap<>();

	public FormulaStringClass(String name) {
		this(name, null);
	}

	public FormulaStringClass add(String method, FormulaString formula) {
		this.formulas.computeIfAbsent(new FormulaKey(method, formula.getArgs()), k -> createSource(k, formula));
		/*for (int i = 0; i < 9; i++) {
			this.formulas.computeIfAbsent(new FormulaKey(method + "_" + i, formula.getArgs()), k -> createSource(k, formula));
		}*/
		return this;
	}

	private FormulaValue createSource(FormulaKey key, FormulaString formula) {
		StringBuilder source = new StringBuilder();
		source.append("\n\tpublic static ");
		source.append(formula.getReturnType().name().toLowerCase());
		source.append(' ');
		source.append(key.name());
		source.append('(');

		Iterator<FormulaArg> iterator = key.args.iterator();
		while (iterator.hasNext()) {
			FormulaArg next = iterator.next();
			source.append(next.type.getTypeName());
			source.append(' ');
			source.append(next.name());
			if (iterator.hasNext()) {
				source.append(", ");
			}
		}

		source.append(") {\n\t\treturn ");
		source.append(formula.getCommand());
		source.append(";\n\t}\n");

		return new FormulaValue(formula, source.toString());
	}

	@Override
	public String toString() {
		return this.formulas.toString();
	}

	public void loadTo(StringClassLoader classLoader) {
		StringBuilder source = new StringBuilder();
		if (this.folder != null) {
			source.append("package ");
			source.append(folder);
			source.append(";\n\n");
		}

		Set<String> importClasses = new TreeSet<>();

		for (FormulaValue value : this.formulas.values()) {
			for (String cl : value.formula.getMethodsForImport()) {
				importClasses.add(cl);
			}
		}
		if (!importClasses.isEmpty()) {
			for (String cl : importClasses) {
				source.append("import ");
				source.append(cl);
				source.append(";\n");
			}

			source.append("\n");
		}

		source.append("public class ");
		source.append(this.name);
		source.append(" {\n");

		for (FormulaValue entry : this.formulas.values()) {
			source.append(entry.source());
		}

		source.append("\n}");

		//log.debug("\n" + AnsiEscape.BLUE.sequence + source);

		String file = (this.folder != null ? this.folder + "." : "") + name;

		try {
			// TODO
			Class<?> cl = classLoader.load(file, source.toString());
			for (FormulaKey key : formulas.keySet()) {
				Class<?>[] classes = new Class[key.args().size()];
				for (int i = 0; i < classes.length; i++) {
					Type type = key.args().get(i).type();
					if (type instanceof Class typeClass) {
						classes[i] = typeClass;
					} else if (type instanceof FormulaCastNode castNode) {
						classes[i] = castNode.getPrimitiveClass();
					} else {
						// TODO
						throw new IllegalStateException();
					}
				}
				@SuppressWarnings("unused")
				MethodHandle method = ReflectUtils.getMethodHandle(cl, key.name(), classes);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private record FormulaValue(FormulaString formula, String source) {
	}

	private record FormulaKey(String name, List<FormulaArg> args) {
		public FormulaKey(String name, Set<Map.Entry<String, Type>> nodes) {
			this(name, nodes.stream().map(e -> new FormulaArg(e.getKey(), e.getValue())).toList());
		}
	}

	private record FormulaArg(String name, Type type) {
		@Override
		public final int hashCode() {
			return this.type.hashCode();
		}

		@Override
		public final boolean equals(Object arg0) {
			if (arg0 == this) {
				return true;
			} else if (arg0 instanceof FormulaArg arg) {
				return this.type() == arg.type();
			} else {
				return false;
			}
		}
	}

}
