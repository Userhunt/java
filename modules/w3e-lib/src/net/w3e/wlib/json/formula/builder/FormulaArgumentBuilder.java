package net.w3e.wlib.json.formula.builder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.skds.lib2.mat.FastMath;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;

public class FormulaArgumentBuilder implements Cloneable {

	private final Map<String, Type> arguments = new LinkedHashMap<>();
	private final Map<FormulaMethodSignature, FormulaMethodLink> methods = new LinkedHashMap<>();

	public final FormulaArgumentBuilder putArgumentsFrom(FormulaArgumentBuilder arguments) {
		for (Entry<String, Type> entry : arguments.arguments.entrySet()) {
			addArgument(entry.getKey(), entry.getValue());
		}
		for (Entry<FormulaMethodSignature, FormulaMethodLink> entry : arguments.methods.entrySet()) {
			addMethod(entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public final Type getArgument(String argument) {
		return this.arguments.get(argument);
	}
	public final boolean hasArgument(String argument) {
		return this.getArgument(argument) != null;
	}
	public void addArgument(String key, Type value) {
		this.arguments.put(key, value);
	}

	public Set<Entry<String, Type>> getArguments() {
		return this.arguments.entrySet();
	}

	public final FormulaMethodLink getMethod(FormulaMethodSignature signature) {
		FormulaMethodLink link = this.methods.get(signature);
		if (link == null) {
			link = METHODS.get(signature);
			if (link != null) {
				this.methods.put(signature.toRegistry(), link);
			}
		}
		return link;
	}
	public final boolean hasMethod(FormulaMethodSignature signature) {
		return this.getMethod(signature) != null;
	}
	public void addMethod(FormulaMethodSignature key, FormulaMethodLink returnValue) {
		this.methods.put(key, returnValue);
	}
	public void addMethod(String key, FormulaMethodLink returnValue) {
		addMethod(new FormulaMethodSignature(key), returnValue);
	}
	
	public Set<Entry<FormulaMethodSignature, FormulaMethodLink>> getMethods() {
		return this.methods.entrySet();
	}

	private static final Map<FormulaMethodSignature, FormulaMethodLink> METHODS = new LinkedHashMap<>();

	static {
		init();
	}

	public static void init() {
		METHODS.clear();
		methods(FastMath.class);

		for (FormulaCastNode cast : new FormulaCastNode[]{
			FormulaCastNode.INT,
			//FormulaCastNode.LONG,
			FormulaCastNode.FLOAT,
			//FormulaCastNode.DOUBLE,
		}) {
			String arg = cast.name().substring(0, 1) + cast.name().substring(1).toLowerCase();
			List<FormulaCastNode> args = List.of(cast);

			// random
			FormulaMethodLink linkRandom = new FormulaMethodLink(cast, "RANDOM.next" + arg + "(%s)", FastMath.class);
			METHODS.put(new FormulaMethodSignature("random" + arg), new FormulaMethodLink(cast, "RANDOM.next" + arg + "()", FastMath.class));
			METHODS.put(new FormulaMethodSignature("random" + arg, args), linkRandom);
			if (cast == FormulaCastNode.INT) {
				METHODS.put(new FormulaMethodSignature("random" + arg, List.of(FormulaCastNode.LONG)), linkRandom);
			}

			// abs
			METHODS.put(new FormulaMethodSignature("abs", args), new FormulaMethodLink(cast, "abs(%s)", Math.class));

			// min or max
			List<FormulaCastNode> minMaxArgs = List.of(cast, cast);
			METHODS.put(new FormulaMethodSignature("min", minMaxArgs), new FormulaMethodLink(cast, "min(%s)", Math.class));
			METHODS.put(new FormulaMethodSignature("max", minMaxArgs), new FormulaMethodLink(cast, "max(%s)", Math.class));
		}
	}

	private static void methods(Class<?> clazz) {
		root:
		for (Method method : clazz.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				continue;
			}
			FormulaCastNode cast = FormulaCastNode.getCastType(method.getReturnType());
			if (cast == null) {
				continue;
			}
			List<FormulaCastNode> arguments = new ArrayList<>();
			for (Class<?> arg : method.getParameterTypes()) {
				FormulaCastNode argCast = FormulaCastNode.getCastType(arg);
				if (argCast == null) {
					if (arg.isArray()) {
						// TODO
						//System.err.println("array " + method.getName());
						continue root;
					}
					System.err.println(method.getName());
					continue root;
				}
				arguments.add(argCast);
			}
			METHODS.put(new FormulaMethodSignature(method.getName(), arguments), new FormulaMethodLink(cast, method.getName() + "(%s)", clazz));
		}
	}

	@Override
	public FormulaArgumentBuilder clone() {
		return new FormulaArgumentBuilder().putArgumentsFrom(this);
	}
}
