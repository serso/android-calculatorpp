/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import bsh.EvalError;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.Attribute;
import org.solovyev.android.calculator.JsclOperation;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */
public class CalculatorModelTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorModel.instance.init(null);
	}

	@Test
	public void testEvaluate() throws Exception {
		final CalculatorModel cm = CalculatorModel.instance;

		Assert.assertEquals("4.0", cm.evaluate(JsclOperation.numeric, "2+2"));
		Assert.assertEquals("-0.7568", cm.evaluate(JsclOperation.numeric, "sin(4)"));
		Assert.assertEquals("0.5236", cm.evaluate(JsclOperation.numeric, "asin(0.5)"));
		Assert.assertEquals("-0.39626", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)"));
		Assert.assertEquals("-0.5604", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)sqrt(2)"));
		Assert.assertEquals("-0.5604", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)√(2)"));
		Assert.assertEquals("7.38906", cm.evaluate(JsclOperation.numeric, "e^2"));
		Assert.assertEquals("7.38906", cm.evaluate(JsclOperation.numeric, "exp(1)^2"));
		Assert.assertEquals("7.38906", cm.evaluate(JsclOperation.numeric, "exp(2)"));
		Assert.assertEquals("2.0+i", cm.evaluate(JsclOperation.numeric, "2*1+sqrt(-1)"));
		Assert.assertEquals("0.92054+3.14159i", cm.evaluate(JsclOperation.numeric, "ln(5cosh(38π√(2cos(2))))"));
		Assert.assertEquals("7.38906i", cm.evaluate(JsclOperation.numeric, "iexp(2)"));
		Assert.assertEquals("2.0+7.38906i", cm.evaluate(JsclOperation.numeric, "2+iexp(2)"));
		Assert.assertEquals("2.0+7.38906i", cm.evaluate(JsclOperation.numeric, "2+√(-1)exp(2)"));
		Assert.assertEquals("2.0-2.5i", cm.evaluate(JsclOperation.numeric, "2-2.5i"));
		Assert.assertEquals("-2.0-2.5i", cm.evaluate(JsclOperation.numeric, "-2-2.5i"));
		Assert.assertEquals("-2.0+2.5i", cm.evaluate(JsclOperation.numeric, "-2+2.5i"));
		Assert.assertEquals("-2.0+2.1i", cm.evaluate(JsclOperation.numeric, "-2+2.1i"));
		Assert.assertEquals("-3.41007+3.41007i", cm.evaluate(JsclOperation.numeric, "(5tan(2i)+2i)/(1-i)"));
		Assert.assertEquals("-0.1-0.2i", cm.evaluate(JsclOperation.numeric, "(1-i)/(2+6i)"));

		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (EvalError e){
		}
		Assert.assertEquals("NaN", cm.evaluate(JsclOperation.numeric, "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))"));
		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (EvalError e){
		}
		Assert.assertEquals("0.73909", cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))"));

		CalculatorModel.instance.getVarsRegister().addVar(null, new Var.Builder("si", 5d));
		Assert.assertEquals("5.0", cm.evaluate(JsclOperation.numeric, "si"));
		try {
			cm.evaluate(JsclOperation.numeric, "sin");
			Assert.fail();
		} catch (EvalError e) {
		}
		Assert.assertEquals("-0.95892", cm.evaluate(JsclOperation.numeric, "sin(5)"));
		Assert.assertEquals("-4.79462", cm.evaluate(JsclOperation.numeric, "sin(5)si"));
		Assert.assertEquals("-23.97311", cm.evaluate(JsclOperation.numeric, "sisin(5)si"));
		Assert.assertEquals("-23.97311", cm.evaluate(JsclOperation.numeric, "si*sin(5)si"));
		Assert.assertEquals("-3.30879", cm.evaluate(JsclOperation.numeric, "sisin(5si)si"));

		CalculatorModel.instance.getVarsRegister().addVar(null, new Var.Builder("s", 1d));
		Assert.assertEquals("5.0", cm.evaluate(JsclOperation.numeric, "si"));

		CalculatorModel.instance.getVarsRegister().addVar(null, new Var.Builder("k", 3.5d));
		CalculatorModel.instance.getVarsRegister().addVar(null, new Var.Builder("k1", 4d));
		Assert.assertEquals("4.0", cm.evaluate(JsclOperation.numeric, "k11"));
	}

	@Test
	public void testComplexNumbers() throws Exception {
		final FromJsclTextProcessor cm = new FromJsclTextProcessor();

		Assert.assertEquals("1.22133+23123.0i", cm.createResultForComplexNumber("1.22133232+23123*i"));
		Assert.assertEquals("1.22133+1.2i", cm.createResultForComplexNumber("1.22133232+1.2*i"));
		Assert.assertEquals("1.22i", cm.createResultForComplexNumber("1.22*i"));
		Assert.assertEquals("i", cm.createResultForComplexNumber("i"));

	}

	public interface TestInterface {
		Integer getField();
	}

	public class TestClass implements TestInterface{

		@Attribute(required = true)
		private Integer field;

		public TestClass() {
		}

		public TestClass(Integer field) {
			this.field = field;
		}

		public Integer getField() {
			return field;
		}

		public void setField(Integer field) {
			this.field = field;
		}
	}

	public class NewTestClass implements TestInterface{

		@Attribute
		private Integer field;

		public NewTestClass() {
		}

		public NewTestClass(Integer field) {
			this.field = field;
		}

		public Integer getField() {
			return field;
		}

		public void setField(Integer field) {
			this.field = field;
		}
	}
}
