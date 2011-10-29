/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import jscl.math.Expression;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.jscl.JsclOperation;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static junit.framework.Assert.fail;

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
		CalculatorEngine.instance.setThreadKiller(new CalculatorEngine.ThreadKillerImpl());
	}

	@Test
	public void testLongExecution() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		try {
			cm.evaluate(JsclOperation.numeric, "3^10^10^10");
			Assert.fail();
		} catch (ParseException e) {
			if (e.getMessage().startsWith("Too long calculation")) {

			} else {
				System.out.print(e.getCause().getMessage());
				Assert.fail();
			}
		}

		try {
			cm.evaluate(JsclOperation.numeric, "9999999!");
			Assert.fail();
		} catch (ParseException e) {
			if (e.getMessage().startsWith("Too long calculation")) {

			} else {
				System.out.print(e.getCause().getMessage());
				Assert.fail();
			}
		}

		/*final long start = System.currentTimeMillis();
		try {
			cm.evaluate(JsclOperation.numeric, "3^10^10^10");
			Assert.fail();
		} catch (ParseException e) {
			if (e.getMessage().startsWith("Too long calculation")) {
				final long end = System.currentTimeMillis();
				Assert.assertTrue(end - start < 1000);
			} else {
				Assert.fail();
			}
		}*/

	}

	@Test
	public void testEvaluate() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "eq(0, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "eq(1, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "eq(  1,   1)").getResult());
		Assert.assertEquals("eq(1,1)", cm.evaluate(JsclOperation.simplify, "eq(  1,   1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "lg(10)").getResult());
		Assert.assertEquals("4", cm.evaluate(JsclOperation.numeric, "2+2").getResult());
		Assert.assertEquals("-0.757", cm.evaluate(JsclOperation.numeric, "sin(4)").getResult());
		Assert.assertEquals("0.524", cm.evaluate(JsclOperation.numeric, "asin(0.5)").getResult());
		Assert.assertEquals("-0.396", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)").getResult());
		Assert.assertEquals("-0.56", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)√(2)").getResult());
		Assert.assertEquals("-0.56", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)√(2)").getResult());
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "e^2").getResult());
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "exp(1)^2").getResult());
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "exp(2)").getResult());
		Assert.assertEquals("2+i", cm.evaluate(JsclOperation.numeric, "2*1+√(-1)").getResult());
		Assert.assertEquals("0.921+3.142i", cm.evaluate(JsclOperation.numeric, "ln(5cosh(38π√(2cos(2))))").getResult());
		Assert.assertEquals("7.389i", cm.evaluate(JsclOperation.numeric, "iexp(2)").getResult());
		Assert.assertEquals("2+7.389i", cm.evaluate(JsclOperation.numeric, "2+iexp(2)").getResult());
		Assert.assertEquals("2+7.389i", cm.evaluate(JsclOperation.numeric, "2+√(-1)exp(2)").getResult());
		Assert.assertEquals("2-2.5i", cm.evaluate(JsclOperation.numeric, "2-2.5i").getResult());
		Assert.assertEquals("-2-2.5i", cm.evaluate(JsclOperation.numeric, "-2-2.5i").getResult());
		Assert.assertEquals("-2+2.5i", cm.evaluate(JsclOperation.numeric, "-2+2.5i").getResult());
		Assert.assertEquals("-2+2.1i", cm.evaluate(JsclOperation.numeric, "-2+2.1i").getResult());
		Assert.assertEquals("-3.41+3.41i", cm.evaluate(JsclOperation.numeric, "(5tan(2i)+2i)/(1-i)").getResult());
		Assert.assertEquals("-0.1-0.2i", cm.evaluate(JsclOperation.numeric, "(1-i)/(2+6i)").getResult());
		
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "4!").getResult());
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "(2+2)!").getResult());
		junit.framework.Assert.assertEquals("120", cm.evaluate(JsclOperation.numeric, "(2+2+1)!").getResult());
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "(2.0+2.0)!").getResult());
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "4.0!").getResult());
		junit.framework.Assert.assertEquals("36.0", Expression.valueOf("3!^2").numeric().toString());
		junit.framework.Assert.assertEquals("3.0", Expression.valueOf("cubic(27)").numeric().toString());
		try {
			junit.framework.Assert.assertEquals("i", cm.evaluate(JsclOperation.numeric, "i!").getResult());
			fail();
		} catch (ParseException e) {
		}
		try {
			junit.framework.Assert.assertEquals("i", cm.evaluate(JsclOperation.numeric, "π/π!").getResult());
			fail();
		} catch (ParseException e) {
		}
		try {
			junit.framework.Assert.assertEquals("i", cm.evaluate(JsclOperation.numeric, "(-1)i!").getResult());
			fail();
		} catch (ParseException e) {

		}
		junit.framework.Assert.assertEquals("24i", cm.evaluate(JsclOperation.numeric, "4!i").getResult());

		CalculatorEngine.instance.getVarsRegister().add(null, new Var.Builder("si", 5d));

		Assert.assertEquals("-0.959", cm.evaluate(JsclOperation.numeric, "sin(5)").getResult());
		Assert.assertEquals("-4.795", cm.evaluate(JsclOperation.numeric, "sin(5)si").getResult());
		Assert.assertEquals("-23.973", cm.evaluate(JsclOperation.numeric, "sisin(5)si").getResult());
		Assert.assertEquals("-23.973", cm.evaluate(JsclOperation.numeric, "si*sin(5)si").getResult());
		Assert.assertEquals("-3.309", cm.evaluate(JsclOperation.numeric, "sisin(5si)si").getResult());

		CalculatorEngine.instance.getVarsRegister().add(null, new Var.Builder("s", 1d));
		Assert.assertEquals("5", cm.evaluate(JsclOperation.numeric, "si").getResult());

		CalculatorEngine.instance.getVarsRegister().add(null, new Var.Builder("k", 3.5d));
		CalculatorEngine.instance.getVarsRegister().add(null, new Var.Builder("k1", 4d));
		Assert.assertEquals("4", cm.evaluate(JsclOperation.numeric, "k11").getResult());

		CalculatorEngine.instance.getVarsRegister().add(null, new Var.Builder("t", (String) null));
		Assert.assertEquals("11t", cm.evaluate(JsclOperation.numeric, "t11").getResult());
		Assert.assertEquals("11et", cm.evaluate(JsclOperation.numeric, "t11e").getResult());
		Assert.assertEquals("11×Infinityt", cm.evaluate(JsclOperation.numeric, "t11∞").getResult());
		Assert.assertEquals("-t+t^3", cm.evaluate(JsclOperation.numeric, "t(t-1)(t+1)").getResult());


	/*	Assert.assertEquals("0.524", cm.evaluate(JsclOperation.numeric, "30°").getResult());
		Assert.assertEquals("0.524", cm.evaluate(JsclOperation.numeric, "(10+20)°").getResult());
		Assert.assertEquals("1.047", cm.evaluate(JsclOperation.numeric, "(10+20)°*2").getResult());
		try {
			Assert.assertEquals("0.278", cm.evaluate(JsclOperation.numeric, "30°^2").getResult());
			junit.framework.Assert.fail();
		} catch (ParseException e) {
			if ( !e.getMessage().equals("Power operation after postfix function is currently unsupported!") ) {
				junit.framework.Assert.fail();
			}
		}*/

/*		try {
			cm.setTimeout(5000);
			Assert.assertEquals("2", cm.evaluate(JsclOperation.numeric, "2!").getResult());
		} finally {
			cm.setTimeout(3000);
		}*/

	}

	@Test
	public void testEmptyFunction() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;
		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (ParseException e) {
		}
		Assert.assertEquals("NaN", cm.evaluate(JsclOperation.numeric, "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))").getResult());
		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (ParseException e) {
		}
		Assert.assertEquals("0.739", cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))").getResult());

		CalculatorEngine.instance.getVarsRegister().add(null, new Var.Builder("si", 5d));
		Assert.assertEquals("5", cm.evaluate(JsclOperation.numeric, "si").getResult());

		try {
			cm.evaluate(JsclOperation.numeric, "sin");
			Assert.fail();
		} catch (ParseException e) {
		}
	}

	@Test
	public void testRounding() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
		decimalGroupSymbols.setDecimalSeparator('.');
		decimalGroupSymbols.setGroupingSeparator('\'');
		cm.setDecimalGroupSymbols(decimalGroupSymbols);
		cm.setPrecision(2);
		Assert.assertEquals("12'345'678.9", cm.evaluate(JsclOperation.numeric, "1.23456789E7").getResult());
		cm.setPrecision(10);
		Assert.assertEquals("12'345'678.899999999", cm.evaluate(JsclOperation.numeric, "1.23456789E7").getResult());
		Assert.assertEquals("123'456'788.99999999", cm.evaluate(JsclOperation.numeric, "1.234567890E8").getResult());
		Assert.assertEquals("1'234'567'890.1", cm.evaluate(JsclOperation.numeric, "1.2345678901E9").getResult());
	}

	@Test
	public void testComparisonFunction() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "eq(0, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "eq(1, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "eq(1, 1.0)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "eq(1, 1.000000000000001)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "eq(1, 0)").getResult());

		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "lt(0, 1)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "lt(1, 1)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "lt(1, 0)").getResult());

		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "gt(0, 1)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "gt(1, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "gt(1, 0)").getResult());

		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "ne(0, 1)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "ne(1, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "ne(1, 0)").getResult());

		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "le(0, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "le(1, 1)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "le(1, 0)").getResult());

		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "ge(0, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "ge(1, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "ge(1, 0)").getResult());

		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "ap(0, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "ap(1, 1)").getResult());
		//Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "ap(1, 1.000000000000001)").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "ap(1, 0)").getResult());

	}

	/*	@Test
	public void testDegrees() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		cm.setPrecision(3);
		Assert.assertEquals("0.017", cm.evaluate(JsclOperation.numeric, "°"));
		Assert.assertEquals("0.017", cm.evaluate(JsclOperation.numeric, "1°"));
		Assert.assertEquals("0.349", cm.evaluate(JsclOperation.numeric, "20.0°"));
		Assert.assertEquals("0.5", cm.evaluate(JsclOperation.numeric, "sin(30°)"));
		Assert.assertEquals("0.524", cm.evaluate(JsclOperation.numeric, "asin(sin(30°))"));

	}*/
}
