/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import bsh.EvalError;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */
public class CalculatorEngineTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorEngine.instance.init(null, null);
		CalculatorEngine.instance.setPrecision(3);
	}

	@Test
	public void testLongExecution() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		try {
			cm.evaluate(JsclOperation.numeric, "3^10^10^10");
			Assert.fail();
		} catch (EvalError evalError) {
			Assert.fail();
		} catch (ParseException e) {
			if ( e.getMessage().startsWith("Too long calculation") ) {

			} else {
				Assert.fail();
			}
		}

		final long start = System.currentTimeMillis();
		try {
			cm.evaluate(JsclOperation.numeric, "3^10^10^10");
			Assert.fail();
		} catch (EvalError evalError) {
			Assert.fail();
		} catch (ParseException e) {
			if ( e.getMessage().startsWith("Too long calculation") ) {
				final long end = System.currentTimeMillis();
				Assert.assertTrue(end - start < 1000);
			} else {
				Assert.fail();
			}
		}

	}

	@Test
	public void testEvaluate() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		Assert.assertEquals("4", cm.evaluate(JsclOperation.numeric, "2+2"));
		Assert.assertEquals("-0.757", cm.evaluate(JsclOperation.numeric, "sin(4)"));
		Assert.assertEquals("0.524", cm.evaluate(JsclOperation.numeric, "asin(0.5)"));
		Assert.assertEquals("-0.396", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)"));
		Assert.assertEquals("-0.56", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)sqrt(2)"));
		Assert.assertEquals("-0.56", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)√(2)"));
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "e^2"));
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "exp(1)^2"));
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "exp(2)"));
		Assert.assertEquals("2+i", cm.evaluate(JsclOperation.numeric, "2*1+sqrt(-1)"));
		Assert.assertEquals("0.921+3.142i", cm.evaluate(JsclOperation.numeric, "ln(5cosh(38π√(2cos(2))))"));
		Assert.assertEquals("7.389i", cm.evaluate(JsclOperation.numeric, "iexp(2)"));
		Assert.assertEquals("2+7.389i", cm.evaluate(JsclOperation.numeric, "2+iexp(2)"));
		Assert.assertEquals("2+7.389i", cm.evaluate(JsclOperation.numeric, "2+√(-1)exp(2)"));
		Assert.assertEquals("2-2.5i", cm.evaluate(JsclOperation.numeric, "2-2.5i"));
		Assert.assertEquals("-2-2.5i", cm.evaluate(JsclOperation.numeric, "-2-2.5i"));
		Assert.assertEquals("-2+2.5i", cm.evaluate(JsclOperation.numeric, "-2+2.5i"));
		Assert.assertEquals("-2+2.1i", cm.evaluate(JsclOperation.numeric, "-2+2.1i"));
		Assert.assertEquals("-3.41+3.41i", cm.evaluate(JsclOperation.numeric, "(5tan(2i)+2i)/(1-i)"));
		Assert.assertEquals("-0.1-0.2i", cm.evaluate(JsclOperation.numeric, "(1-i)/(2+6i)"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("si", 5d));

		Assert.assertEquals("-0.959", cm.evaluate(JsclOperation.numeric, "sin(5)"));
		Assert.assertEquals("-4.795", cm.evaluate(JsclOperation.numeric, "sin(5)si"));
		Assert.assertEquals("-23.973", cm.evaluate(JsclOperation.numeric, "sisin(5)si"));
		Assert.assertEquals("-23.973", cm.evaluate(JsclOperation.numeric, "si*sin(5)si"));
		Assert.assertEquals("-3.309", cm.evaluate(JsclOperation.numeric, "sisin(5si)si"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("s", 1d));
		Assert.assertEquals("5", cm.evaluate(JsclOperation.numeric, "si"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("k", 3.5d));
		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("k1", 4d));
		Assert.assertEquals("4", cm.evaluate(JsclOperation.numeric, "k11"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("t", (String)null));
		Assert.assertEquals("11×t", cm.evaluate(JsclOperation.numeric, "t11"));
		Assert.assertEquals("11×e×t", cm.evaluate(JsclOperation.numeric, "t11e"));
		Assert.assertEquals("11×Infinity×t", cm.evaluate(JsclOperation.numeric, "t11∞"));
		Assert.assertEquals("-t+t^3", cm.evaluate(JsclOperation.numeric, "t(t-1)(t+1)"));
	}

	@Test
	public void testEmptyFunction() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;
		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (ParseException e){
		}
		Assert.assertEquals("NaN", cm.evaluate(JsclOperation.numeric, "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))"));
		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (ParseException e){
		}
		Assert.assertEquals("0.739", cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("si", 5d));
		Assert.assertEquals("5", cm.evaluate(JsclOperation.numeric, "si"));
		try {
			cm.evaluate(JsclOperation.numeric, "sin");
			Assert.fail();
		} catch (EvalError e) {
		}
	}

	@Test
	public void testRounding() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		cm.setPrecision(2);
		Assert.assertEquals("12345678.9", cm.evaluate(JsclOperation.numeric, "1.23456789E7"));
		cm.setPrecision(10);
		Assert.assertEquals("12345678.899999999", cm.evaluate(JsclOperation.numeric, "1.23456789E7"));
		Assert.assertEquals("123456788.99999999", cm.evaluate(JsclOperation.numeric, "1.234567890E8"));
		Assert.assertEquals("1234567890.1", cm.evaluate(JsclOperation.numeric, "1.2345678901E9"));


	}
}
