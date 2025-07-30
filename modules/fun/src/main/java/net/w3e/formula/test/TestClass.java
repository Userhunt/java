package net.w3e.formula.test;


import java.lang.Math;
import net.w3e.include.skds.mat.FastMath;

public class TestClass {

	public static float testMethod(net.w3e.formula.FormulaTestContext context, int i, double x) {
		return (float)((int)(8.5/(i++)*5.0)+x+FastMath.RANDOM.nextInt(5)-Math.min(Math.abs(i),context.entity.getHealth())*-10
			% 5
		);
	}

	public static int aaa(int i) {
		return i % 10;
	}
}