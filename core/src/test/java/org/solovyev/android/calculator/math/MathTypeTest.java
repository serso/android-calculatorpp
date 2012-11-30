/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.AbstractCalculatorTest;
import org.solovyev.android.calculator.CalculatorTestUtils;

/**
 * User: serso
 * Date: 10/5/11
 * Time: 1:25 AM
 */
public class MathTypeTest extends AbstractCalculatorTest {

	@BeforeClass
	public static void staticSetUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
	}

	@Test
	public void testGetType() throws Exception {
		Assert.assertEquals(MathType.function, MathType.getType("sin", 0, false).getMathType());
		Assert.assertEquals(MathType.text, MathType.getType("sn", 0, false).getMathType());
		Assert.assertEquals(MathType.text, MathType.getType("s", 0, false).getMathType());
		Assert.assertEquals(MathType.text, MathType.getType("", 0, false).getMathType());

		try {
			Assert.assertEquals(MathType.text, MathType.getType("22", -1, false).getMathType());
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			Assert.assertEquals(MathType.text, MathType.getType("22", 2, false).getMathType());
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		Assert.assertEquals("atanh", MathType.getType("atanh", 0, false).getMatch());
	}

/*	@Test
	public void testPostfixFunctionsProcessing() throws Exception {

		org.junit.Assert.assertEquals(-1, MathType.getPostfixFunctionStart("5!", 1));
		org.junit.Assert.assertEquals(0, MathType.getPostfixFunctionStart("!", 1));
		org.junit.Assert.assertEquals(-1, MathType.getPostfixFunctionStart("5.4434234!", 9));
		org.junit.Assert.assertEquals(1, MathType.getPostfixFunctionStart("2+5!", 3));
		org.junit.Assert.assertEquals(4, MathType.getPostfixFunctionStart("2.23+5.4434234!", 14));
		org.junit.Assert.assertEquals(14, MathType.getPostfixFunctionStart("2.23+5.4434234*5!", 16));
		org.junit.Assert.assertEquals(14, MathType.getPostfixFunctionStart("2.23+5.4434234*5.1!", 18));
		org.junit.Assert.assertEquals(4, MathType.getPostfixFunctionStart("2.23+(5.4434234*5.1)!", 20));
		org.junit.Assert.assertEquals(4, MathType.getPostfixFunctionStart("2.23+(5.4434234*(5.1+1))!", 24));
		org.junit.Assert.assertEquals(4, MathType.getPostfixFunctionStart("2.23+(5.4434234*sin(5.1+1))!", 27));
		org.junit.Assert.assertEquals(0, MathType.getPostfixFunctionStart("sin(5)!", 6));
		org.junit.Assert.assertEquals(-1, MathType.getPostfixFunctionStart(")!", ")!".indexOf("!")));
		org.junit.Assert.assertEquals(0, MathType.getPostfixFunctionStart("sin(5sin(5sin(5)))!", 18));
		org.junit.Assert.assertEquals(2, MathType.getPostfixFunctionStart("2+sin(5sin(5sin(5)))!", 20));
		org.junit.Assert.assertEquals(5, MathType.getPostfixFunctionStart("2.23+sin(5.4434234*sin(5.1+1))!", 30));
		org.junit.Assert.assertEquals(5, MathType.getPostfixFunctionStart("2.23+sin(5.4434234*sin(5.1E2+e))!", "2.23+sin(5.4434234*sin(5.1E2+e))!".indexOf("!")));
		org.junit.Assert.assertEquals(5, MathType.getPostfixFunctionStart("2.23+sin(5.4434234*sin(5.1E2+5 555 555))!", "2.23+sin(5.4434234*sin(5.1E2+5 555 555))!".indexOf("!")));
		org.junit.Assert.assertEquals(5, MathType.getPostfixFunctionStart("2.23+sin(5.4434234^sin(5.1E2!+5'555'555))!", "2.23+sin(5.4434234^sin(5.1E2!+5'555'555))!".lastIndexOf("!")));
	}*/
}
