package net.w3e.wlib.json.formula;

import net.skds.lib2.benchmark.Benchmark;
import net.skds.lib2.misc.clazz.StringClassLoader;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.wlib.json.formula.builder.FormulaArgumentBuilder;
import net.w3e.wlib.json.formula.builder.FormulaMethodLink;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;
import net.w3e.wlib.json.formula.nodes.reader.FormulaReader;
import net.w3e.wlib.json.formula.nodes.reader.FormulaTreeNode;
import net.w3e.wlib.json.formula.string.FormulaString;
import net.w3e.wlib.json.formula.string.FormulaStringClass;

public class FormulaTest {

	public static void main(String[] args) throws InterruptedException {
		SKDSLogger.replaceOuts();

		while (true) {
			try {
				benchmark();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			Thread.sleep(5000);
		}
	}

	@SuppressWarnings("unused")
	private static void benchmark() {
		FormulaArgumentBuilder arguments = createArguments();
		Benchmark benchmark = new Benchmark(10, 10) {
			@Override
			protected void prepare() {
			}

			@Override
			protected void bench() {
				try {
					parseString(arguments);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		};
		benchmark.run();

		System.out.println(benchmark.result());
	}

	private static FormulaArgumentBuilder createArguments() {
		FormulaArgumentBuilder.init();
		FormulaArgumentBuilder arguments = new FormulaArgumentBuilder();

		arguments.addMethod("getHealth", new FormulaMethodLink(FormulaCastNode.FLOAT, "context.entity.getHealth()", null));
		arguments.addArgument("context", FormulaTestContext.class);
		arguments.addArgument("i", FormulaCastNode.INT);
		arguments.addArgument("x", FormulaCastNode.DOUBLE);

		return arguments;
	}

	@SuppressWarnings("unused")
	private static void parseString() throws Throwable {
		parseString(createArguments());
	}

	private static void parseString(FormulaArgumentBuilder arguments) throws Throwable {
		//System.out.println();
		//System.out.println();
		//System.out.println();
		String line = "(int)((1 + 2.5 * 3) / (i++) * (double)5) + x + randomInt(5) - min(abs(i), getHealth()) * (-10)";
		//System.out.println(line);

		FormulaReader reader = new FormulaReader(line);
		reader.readAll();
		//reader.print();

		FormulaTreeNode tree = reader.toTree();
		//tree.printAsJson();

		//log.info(tree.buildConsoleString());
		tree.tryCombine();
		//log.info(tree.buildConsoleString());

		FormulaString formula = tree.createStringBuilder(arguments).setReturnType(FormulaCastNode.FLOAT).build();

		FormulaStringClass formulaClass = new FormulaStringClass("TestClass", "net.w3e.test");
		formulaClass.add("testMethod", formula);
		//System.out.println(formulaClass);

		StringClassLoader classLoader = new StringClassLoader();
		formulaClass.loadTo(classLoader);
	}
}
