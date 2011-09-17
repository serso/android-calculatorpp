/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */
public class CalculatorModelTest {
	@Test
	public void testEvaluate() throws Exception {
		final CalculatorModel cm = new CalculatorModel();

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

	}

	@Test
	public void testComplexNumbers() throws Exception {
		final CalculatorModel cm = new CalculatorModel();

		Assert.assertEquals("1.22133+23123.0i", cm.createResultForComplexNumber("1.22133232+23123*i"));
		Assert.assertEquals("1.22133+1.2i", cm.createResultForComplexNumber("1.22133232+1.2*i"));
		Assert.assertEquals("1.22i", cm.createResultForComplexNumber("1.22*i"));
		Assert.assertEquals("i", cm.createResultForComplexNumber("i"));

	}
}
