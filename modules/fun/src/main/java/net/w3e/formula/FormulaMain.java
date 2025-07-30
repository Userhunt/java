package net.w3e.formula;

import lombok.CustomLog;
import net.w3e.formula.test.TestClass;
import net.w3e.include.skds.misc.clazz.StringClassLoader;
import net.w3e.include.skds.utils.logger.SKDSLogger;
import net.w3e.sjni.SJNIClassGenerator;
import net.w3e.wlib.json.formula.builder.FormulaArgumentBuilder;
import net.w3e.wlib.json.formula.builder.FormulaMethodLink;
import net.w3e.wlib.json.formula.builder.FormulaMethodSignature;
import net.w3e.wlib.json.formula.nodes.FormulaCastNode;
import net.w3e.wlib.json.formula.nodes.reader.FormulaReader;
import net.w3e.wlib.json.formula.nodes.reader.FormulaTreeNode;
import net.w3e.wlib.json.formula.string.FormulaString;
import net.w3e.wlib.json.formula.string.FormulaStringClass;

import java.lang.classfile.MethodBuilder;
import java.util.function.Consumer;

@CustomLog
public class FormulaMain {

	public static void main(String[] args) throws InterruptedException {
		SKDSLogger.replaceOuts();

		while (true) {
			try {
				parseBytecode();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			Thread.sleep(5000);
		}
	}

	private static FormulaArgumentBuilder createArguments() {
		FormulaArgumentBuilder.init();
		FormulaArgumentBuilder arguments = new FormulaArgumentBuilder();

		arguments.addMethod(new FormulaMethodSignature("getHealth"), new FormulaMethodLink(FormulaCastNode.FLOAT, "context.entity.getHealth()", null));
		arguments.addArgument("context", FormulaTestContext.class);
		arguments.addArgument("i", FormulaCastNode.INT);
		arguments.addArgument("x", FormulaCastNode.DOUBLE);

		return arguments;
	}

	@SuppressWarnings("unused")
	private static void parseBytecode() throws Throwable {
		parseBytecode(createArguments());
	}

	private static void parseBytecode(FormulaArgumentBuilder arguments) throws Throwable {
		System.out.println();
		System.out.println();
		System.out.println();
		String line = "(int)((1 + 2.5 * 3) / (i++) * (double)5) + x + randomInt(5) - min(abs(i), getHealth()) * (-10) % 5";
		//System.out.println(line);

		FormulaReader reader = new FormulaReader(line);
		reader.readAll();
		//reader.print();

		FormulaTreeNode tree = reader.toTree();
		//tree.printAsJson();

		//log.info(tree.buildConsoleString());
		tree.tryCombine();
		log.info(tree.buildConsoleString());

		/*Consumer<MethodBuilder> calculateAnnualBonusBuilder = methodBuilder -> methodBuilder.withCode(codeBuilder -> {
			//codeBuilder.
		});*/

		SJNIClassGenerator.load(TestClass.class);

		/*FormulaString formula = tree.createStringBuilder(arguments).setReturnType(FormulaCastNode.FLOAT).build();

		FormulaStringClass formulaClass = new FormulaStringClass("TestClass", "net.w3e.test");
		formulaClass.add("testMethod", formula);
		//System.out.println(formulaClass);

		StringClassLoader classLoader = new StringClassLoader();
		formulaClass.loadTo(classLoader);*/
	}

	/*
	Consumer<MethodBuilder> calculateAnnualBonusBuilder = methodBuilder -> methodBuilder.withCode(codeBuilder -> {
			codeBuilder.loadConstant(3).ireturn();
			Label notSales = codeBuilder.newLabel();
			Label notEngineer = codeBuilder.newLabel();
			ClassDesc stringClass = ClassDesc.of("java.lang.String");

			codeBuilder.aload(3)
					.ldc("sales")
					.invokevirtual(stringClass, "equals", MethodTypeDesc.of(ClassDesc.of("Z"), stringClass))
					.ifeq(notSales)
					.dload(1)
					.ldc(0.35)
					.dmul()
					.dreturn();

			codeBuilder.labelBinding(notSales)
					.aload(3)
					.ldc("engineer")
					.invokevirtual(stringClass, "equals", MethodTypeDesc.of(CD_boolean, stringClass))
					.ifeq(notEngineer)
					.dload(1)
					.ldc(0.25)
					.dmul()
					.dreturn();

			codeBuilder.labelBinding(notSales)
					.aload(3)
					.ldc("engineer")
					.invokevirtual(stringClass, "equals", MethodTypeDesc.of(CD_boolean, stringClass))
					.ifeq(notEngineer)
					.dload(1)
					.ldc(0.25)
					.dmul()
					.dreturn();

});

		var classBuilder = ClassFile.of().build(ClassDesc.of("EmployeeSalaryCalculator"), cb -> cb.withMethod("calculateAnnualBonus", MethodTypeDesc.of(CD_int, CD_boolean),
		Modifier.PUBLIC | Modifier.STATIC,
		calculateAnnualBonusBuilder));


		Class<?> cl = SJNIClassLoader.INSTANCE.compileClass(null, classBuilder);
		Method m = cl.getDeclaredMethod("calculateAnnualBonus", boolean.class);
		System.out.println(m.invoke(null, false));
	 */
}