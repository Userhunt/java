package net.w3e.formula.bytecode;

import net.w3e.wlib.json.formula.nodes.reader.FormulaTreeNode;

import java.lang.classfile.ClassFile;
import java.lang.classfile.MethodBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.constant.ConstantDescs.*;

public class FormulaModel {

	private final List<Consumer<MethodBuilder>> methods = new ArrayList<>();

	public void build() {
		byte[] classBuilder = ClassFile.of().build(ClassDesc.of("FormulaMath"), cb -> methods.forEach(e -> cb.withMethod(
				"nameTODO",
				MethodTypeDesc.of(CD_int),
				Modifier.PUBLIC | Modifier.STATIC,
				e
		)));

		/*
		cb -> cb.withMethod("calculateAnnualBonus", MethodTypeDesc.of(CD_int, CD_boolean),
		Modifier.PUBLIC | Modifier.STATIC,
		calculateAnnualBonusBuilder))
		 */
	}

	private record FormulaMethod(String name, MethodTypeDesc desc, FormulaTreeNode formula) {

		private void build() {

		}

	}
}
