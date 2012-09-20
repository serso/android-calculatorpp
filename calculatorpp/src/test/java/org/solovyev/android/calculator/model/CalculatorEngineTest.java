/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.NumeralBase;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.CustomFunction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.CalculatorEvalException;
import org.solovyev.android.calculator.CalculatorParseException;
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
	public void testDegrees() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		final AngleUnit defaultAngleUnit = cm.getEngine().getAngleUnits();
		try {
			cm.getEngine().setAngleUnits(AngleUnit.rad);
			cm.setPrecision(3);
			try {
				Assert.assertEquals("0.017", cm.evaluate(JsclOperation.numeric, "°"));
				fail();
			} catch (CalculatorParseException e) {

			}

			Assert.assertEquals("0.017", cm.evaluate(JsclOperation.numeric, "1°").getResult());
			Assert.assertEquals("0.349", cm.evaluate(JsclOperation.numeric, "20.0°").getResult());
			Assert.assertEquals("0.5", cm.evaluate(JsclOperation.numeric, "sin(30°)").getResult());
			Assert.assertEquals("0.524", cm.evaluate(JsclOperation.numeric, "asin(sin(30°))").getResult());
			Assert.assertEquals("∂(cos(t), t, t, 1°)", cm.evaluate(JsclOperation.numeric, "∂(cos(t),t,t,1°)").getResult());

			Assert.assertEquals("∂(cos(t), t, t, 1°)", cm.evaluate(JsclOperation.simplify, "∂(cos(t),t,t,1°)").getResult());
		} finally {
			cm.getEngine().setAngleUnits(defaultAngleUnit);
		}
	}

	@Test
	public void testLongExecution() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		try {
			cm.evaluate(JsclOperation.numeric, "3^10^10^10");
			Assert.fail();
		} catch (CalculatorParseException e) {
			if (e.getMessageCode().equals(Messages.msg_3)) {

			} else {
				System.out.print(e.getCause().getMessage());
				Assert.fail();
			}
		}

		try {
			cm.evaluate(JsclOperation.numeric, "9999999!");
			Assert.fail();
		} catch (CalculatorParseException e) {
			if (e.getMessageCode().equals(Messages.msg_3)) {

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

		Assert.assertEquals("cos(t)+10%", cm.evaluate(JsclOperation.simplify, "cos(t)+10%").getResult());

		final Generic expression = cm.getEngine().simplifyGeneric("cos(t)+10%");
		expression.substitute(new Constant("t"), Expression.valueOf(100d));

		Assert.assertEquals("it", cm.evaluate(JsclOperation.simplify, "it").getResult());
		Assert.assertEquals("10%", cm.evaluate(JsclOperation.simplify, "10%").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "eq(0, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "eq(1, 1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "eq(  1,   1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.simplify, "eq(  1,   1)").getResult());
		Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "lg(10)").getResult());
		Assert.assertEquals("4", cm.evaluate(JsclOperation.numeric, "2+2").getResult());
		final AngleUnit defaultAngleUnit = cm.getEngine().getAngleUnits();
		try {
			cm.getEngine().setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("-0.757", cm.evaluate(JsclOperation.numeric, "sin(4)").getResult());
			Assert.assertEquals("0.524", cm.evaluate(JsclOperation.numeric, "asin(0.5)").getResult());
			Assert.assertEquals("-0.396", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)").getResult());
			Assert.assertEquals("-0.56", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)√(2)").getResult());
			Assert.assertEquals("-0.56", cm.evaluate(JsclOperation.numeric, "sin(4)asin(0.5)√(2)").getResult());
		} finally {
			cm.getEngine().setAngleUnits(defaultAngleUnit);
		}
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "e^2").getResult());
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "exp(1)^2").getResult());
		Assert.assertEquals("7.389", cm.evaluate(JsclOperation.numeric, "exp(2)").getResult());
		Assert.assertEquals("2+i", cm.evaluate(JsclOperation.numeric, "2*1+√(-1)").getResult());
		try {
			cm.getEngine().setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("0.921+Πi", cm.evaluate(JsclOperation.numeric, "ln(5cosh(38π√(2cos(2))))").getResult());
			Assert.assertEquals("-3.41+3.41i", cm.evaluate(JsclOperation.numeric, "(5tan(2i)+2i)/(1-i)").getResult());
		} finally {
			cm.getEngine().setAngleUnits(defaultAngleUnit);
		}
		Assert.assertEquals("7.389i", cm.evaluate(JsclOperation.numeric, "iexp(2)").getResult());
		Assert.assertEquals("2+7.389i", cm.evaluate(JsclOperation.numeric, "2+iexp(2)").getResult());
		Assert.assertEquals("2+7.389i", cm.evaluate(JsclOperation.numeric, "2+√(-1)exp(2)").getResult());
		Assert.assertEquals("2-2.5i", cm.evaluate(JsclOperation.numeric, "2-2.5i").getResult());
		Assert.assertEquals("-2-2.5i", cm.evaluate(JsclOperation.numeric, "-2-2.5i").getResult());
		Assert.assertEquals("-2+2.5i", cm.evaluate(JsclOperation.numeric, "-2+2.5i").getResult());
		Assert.assertEquals("-2+2.1i", cm.evaluate(JsclOperation.numeric, "-2+2.1i").getResult());
		Assert.assertEquals("-0.1-0.2i", cm.evaluate(JsclOperation.numeric, "(1-i)/(2+6i)").getResult());
		
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "4!").getResult());
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "(2+2)!").getResult());
		junit.framework.Assert.assertEquals("120", cm.evaluate(JsclOperation.numeric, "(2+2+1)!").getResult());
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "(2.0+2.0)!").getResult());
		junit.framework.Assert.assertEquals("24", cm.evaluate(JsclOperation.numeric, "4.0!").getResult());
		junit.framework.Assert.assertEquals("720", cm.evaluate(JsclOperation.numeric, "(3!)!").getResult());
		junit.framework.Assert.assertEquals("36", Expression.valueOf("3!^2").numeric().toString());
		junit.framework.Assert.assertEquals("3", Expression.valueOf("cubic(27)").numeric().toString());
		try {
			junit.framework.Assert.assertEquals("√(-1)!", cm.evaluate(JsclOperation.numeric, "i!").getResult());
			fail();
		} catch (CalculatorParseException e) {
		}

		junit.framework.Assert.assertEquals("1", cm.evaluate(JsclOperation.numeric, "(π/π)!").getResult());

		try {
			junit.framework.Assert.assertEquals("i", cm.evaluate(JsclOperation.numeric, "(-1)i!").getResult());
			fail();
		} catch (CalculatorParseException e) {

		}
		junit.framework.Assert.assertEquals("24i", cm.evaluate(JsclOperation.numeric, "4!i").getResult());

		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("si", 5d));

		try {
			cm.getEngine().setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("0.451", cm.evaluate(JsclOperation.numeric, "acos(0.8999999999999811)").getResult());
			Assert.assertEquals("-0.959", cm.evaluate(JsclOperation.numeric, "sin(5)").getResult());
			Assert.assertEquals("-4.795", cm.evaluate(JsclOperation.numeric, "sin(5)si").getResult());
			Assert.assertEquals("-23.973", cm.evaluate(JsclOperation.numeric, "sisin(5)si").getResult());
			Assert.assertEquals("-23.973", cm.evaluate(JsclOperation.numeric, "si*sin(5)si").getResult());
			Assert.assertEquals("-3.309", cm.evaluate(JsclOperation.numeric, "sisin(5si)si").getResult());
		} finally {
			cm.getEngine().setAngleUnits(defaultAngleUnit);
		}

		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("s", 1d));
		Assert.assertEquals("5", cm.evaluate(JsclOperation.numeric, "si").getResult());

		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("k", 3.5d));
		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("k1", 4d));
		Assert.assertEquals("4", cm.evaluate(JsclOperation.numeric, "k11").getResult());

		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("t", (String) null));
		Assert.assertEquals("11t", cm.evaluate(JsclOperation.numeric, "t11").getResult());
		Assert.assertEquals("11et", cm.evaluate(JsclOperation.numeric, "t11e").getResult());
		Assert.assertEquals("∞", cm.evaluate(JsclOperation.numeric, "∞").getResult());
		Assert.assertEquals("∞", cm.evaluate(JsclOperation.numeric, "Infinity").getResult());
		Assert.assertEquals("11∞t", cm.evaluate(JsclOperation.numeric, "t11∞").getResult());
		Assert.assertEquals("-t+t^3", cm.evaluate(JsclOperation.numeric, "t(t-1)(t+1)").getResult());

		Assert.assertEquals("100", cm.evaluate(JsclOperation.numeric, "0.1E3").getResult());
		Assert.assertEquals("3.957", cm.evaluate(JsclOperation.numeric, "ln(8)lg(8)+ln(8)").getResult());

		Assert.assertEquals("0.933", cm.evaluate(JsclOperation.numeric, "0x:E/0x:F").getResult());

		try {
		 	cm.getEngine().setNumeralBase(NumeralBase.hex);
			Assert.assertEquals("E/F", cm.evaluate(JsclOperation.numeric, "0x:E/0x:F").getResult());
			Assert.assertEquals("E/F", cm.evaluate(JsclOperation.simplify, "0x:E/0x:F").getResult());
			Assert.assertEquals("E/F", cm.evaluate(JsclOperation.numeric, "E/F").getResult());
			Assert.assertEquals("E/F", cm.evaluate(JsclOperation.simplify, "E/F").getResult());
		} finally {
			cm.getEngine().setNumeralBase(NumeralBase.dec);
		}

		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "((((((0))))))").getResult());
		Assert.assertEquals("0", cm.evaluate(JsclOperation.numeric, "((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((0))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))").getResult());


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

		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("t", (String) null));
		Assert.assertEquals("2t", cm.evaluate(JsclOperation.simplify, "∂(t^2,t)").getResult());
		Assert.assertEquals("2t", cm.evaluate(JsclOperation.numeric, "∂(t^2,t)").getResult());
		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("t", "2"));
		Assert.assertEquals("2t", cm.evaluate(JsclOperation.simplify, "∂(t^2,t)").getResult());
		Assert.assertEquals("4", cm.evaluate(JsclOperation.numeric, "∂(t^2,t)").getResult());

		Assert.assertEquals("-x+x*ln(x)", cm.getEngine().simplify("∫(ln(x), x)"));
		Assert.assertEquals("-(x-x*ln(x))/(ln(2)+ln(5))", cm.getEngine().simplify("∫(log(10, x), x)"));

		Assert.assertEquals("∫((ln(2)+ln(5))/ln(x), x)", cm.getEngine().simplify("∫(ln(10)/ln(x), x)"));
		Assert.assertEquals("∫(ln(10)/ln(x), x)", Expression.valueOf("∫(log(x, 10), x)").expand().toString());
		Assert.assertEquals("∫((ln(2)+ln(5))/ln(x), x)", cm.getEngine().simplify("∫(log(x, 10), x)"));
	}

	@Test
	public void testFormatting() throws Exception {
		final CalculatorEngine ce = CalculatorEngine.instance;

		Assert.assertEquals("12 345", ce.evaluate(JsclOperation.simplify, "12345").getResult());

	}

	@Test
	public void testI() throws CalculatorParseException, CalculatorEvalException {
		final CalculatorEngine cm = CalculatorEngine.instance;

		Assert.assertEquals("-i", cm.evaluate(JsclOperation.numeric, "i^3").getResult());
		for (int i = 0; i < 1000; i++) {
		 	double real = (Math.random()-0.5) * 1000;
		 	double imag = (Math.random()-0.5) * 1000;
		 	int exp = (int)(Math.random() * 10);

			final StringBuilder sb = new StringBuilder();
			sb.append(real);
			if ( imag > 0 ) {
				sb.append("+");
			}
			sb.append(imag);
			sb.append("^").append(exp);
			try {
				cm.evaluate(JsclOperation.numeric, sb.toString()).getResult();
			} catch (Throwable e) {
				fail(sb.toString());
			}
		}
	}

	@Test
	public void testEmptyFunction() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;
		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (CalculatorParseException e) {
		}
		Assert.assertEquals("0.34+1.382i", cm.evaluate(JsclOperation.numeric, "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))").getResult());
		try {
			cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (CalculatorParseException e) {
		}

		final AngleUnit defaultAngleUnit = cm.getEngine().getAngleUnits();
		try {
			cm.getEngine().setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("0.739", cm.evaluate(JsclOperation.numeric, "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))").getResult());
		} finally {
			cm.getEngine().setAngleUnits(defaultAngleUnit);
		}

		CalculatorEngine.instance.getVarsRegistry().add(new Var.Builder("si", 5d));
		Assert.assertEquals("5", cm.evaluate(JsclOperation.numeric, "si").getResult());

		try {
			cm.evaluate(JsclOperation.numeric, "sin");
			Assert.fail();
		} catch (CalculatorParseException e) {
		}
	}

	@Test
	public void testRounding() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		try {
			DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
			decimalGroupSymbols.setDecimalSeparator('.');
			decimalGroupSymbols.setGroupingSeparator('\'');
			cm.setDecimalGroupSymbols(decimalGroupSymbols);
			cm.setPrecision(2);
			Assert.assertEquals("12'345'678.9", cm.evaluate(JsclOperation.numeric, "1.23456789E7").getResult());
			cm.setPrecision(10);
			Assert.assertEquals("12'345'678.9", cm.evaluate(JsclOperation.numeric, "1.23456789E7").getResult());
			Assert.assertEquals("123'456'789", cm.evaluate(JsclOperation.numeric, "1.234567890E8").getResult());
			Assert.assertEquals("1'234'567'890.1", cm.evaluate(JsclOperation.numeric, "1.2345678901E9").getResult());
		} finally {
			cm.setPrecision(3);
			DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
			decimalGroupSymbols.setDecimalSeparator('.');
			decimalGroupSymbols.setGroupingSeparator(JsclMathEngine.GROUPING_SEPARATOR_DEFAULT.charAt(0));
			cm.setDecimalGroupSymbols(decimalGroupSymbols);
		}
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


	@Test
	public void testNumeralSystems() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		Assert.assertEquals("11 259 375", cm.evaluate(JsclOperation.numeric, "0x:ABCDEF").getResult());
		Assert.assertEquals("30 606 154.462", cm.evaluate(JsclOperation.numeric, "0x:ABCDEF*e").getResult());
		Assert.assertEquals("30 606 154.462", cm.evaluate(JsclOperation.numeric, "e*0x:ABCDEF").getResult());
		Assert.assertEquals("e", cm.evaluate(JsclOperation.numeric, "e*0x:ABCDEF/0x:ABCDEF").getResult());
		Assert.assertEquals("30 606 154.462", cm.evaluate(JsclOperation.numeric, "0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF").getResult());
		Assert.assertEquals("30 606 154.462", cm.evaluate(JsclOperation.numeric, "c+0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF-c+0x:C-0x:C").getResult());
		Assert.assertEquals("1 446 257 064 651.832", cm.evaluate(JsclOperation.numeric, "28*28 * sin(28) - 0b:1101 + √(28) + exp ( 28) ").getResult());
		Assert.assertEquals("13", cm.evaluate(JsclOperation.numeric, "0b:1101").getResult());

		try {
			cm.evaluate(JsclOperation.numeric, "0b:π").getResult();
			Assert.fail();
		} catch (CalculatorParseException e) {
			// ok
		}

		final NumeralBase defaultNumeralBase = cm.getEngine().getNumeralBase();
		try{
			cm.getEngine().setNumeralBase(NumeralBase.bin);
			Assert.assertEquals("101", cm.evaluate(JsclOperation.numeric, "10+11").getResult());
			Assert.assertEquals("10/11", cm.evaluate(JsclOperation.numeric, "10/11").getResult());

			cm.getEngine().setNumeralBase(NumeralBase.hex);
			Assert.assertEquals("63 7B", cm.evaluate(JsclOperation.numeric, "56CE+CAD").getResult());
			Assert.assertEquals("E", cm.evaluate(JsclOperation.numeric, "E").getResult());
		} finally {
			cm.setNumeralBase(defaultNumeralBase);
		}
	}

	@Test
	public void testLog() throws Exception {
		final CalculatorEngine cm = CalculatorEngine.instance;

		Assert.assertEquals("∞", Expression.valueOf("1/0").numeric().toString());
		Assert.assertEquals("∞", Expression.valueOf("ln(10)/ln(1)").numeric().toString());

		// logarithm
		Assert.assertEquals("ln(x)/ln(base)", ((CustomFunction) cm.getFunctionsRegistry().get("log")).getContent());
		Assert.assertEquals("∞", cm.evaluate(JsclOperation.numeric, "log(1, 10)").getResult());
		Assert.assertEquals("3.322", cm.evaluate(JsclOperation.numeric, "log(2, 10)").getResult());
		Assert.assertEquals("1.431", cm.evaluate(JsclOperation.numeric, "log(5, 10)").getResult());
		Assert.assertEquals("0.96", cm.evaluate(JsclOperation.numeric, "log(11, 10)").getResult());
		Assert.assertEquals("1/(bln(a))", cm.evaluate(JsclOperation.simplify, "∂(log(a, b), b)").getResult());
		Assert.assertEquals("-ln(b)/(aln(a)^2)", cm.evaluate(JsclOperation.simplify, "∂(log(a, b), a)").getResult());

	}
}
