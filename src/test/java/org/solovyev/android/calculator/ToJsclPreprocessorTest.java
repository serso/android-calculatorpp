/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 12:13 PM
 */
public class ToJsclPreprocessorTest {

	@Test
	public void testProcess() throws Exception {
		final ToJsclPreprocessor preprocessor = new ToJsclPreprocessor();

		Assert.assertEquals( "sin(4)*cos(5)", preprocessor.process("sin(4)cos(5)"));
		Assert.assertEquals( "pi*sin(4)*pi*cos(sqrt(5))", preprocessor.process("πsin(4)πcos(√(5))"));
		Assert.assertEquals( "pi*sin(4)+pi*cos(sqrt(5))", preprocessor.process("πsin(4)+πcos(√(5))"));
		Assert.assertEquals( "pi*sin(4)+pi*cos(sqrt(5+sqrt(-1)))", preprocessor.process("πsin(4)+πcos(√(5+i))"));
		Assert.assertEquals( "pi*sin(4.01)+pi*cos(sqrt(5+sqrt(-1)))", preprocessor.process("πsin(4.01)+πcos(√(5+i))"));
		Assert.assertEquals( "exp(1)^pi*sin(4.01)+pi*cos(sqrt(5+sqrt(-1)))", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))"));
	}

	@Test
	public void testPostfixFunctionsProcessing() throws Exception {
		final ToJsclPreprocessor preprocessor = new ToJsclPreprocessor();

		Assert.assertEquals(-1, preprocessor.getPostfixFunctionStart("5!", 0));
		Assert.assertEquals(0, preprocessor.getPostfixFunctionStart("!", 0));
		Assert.assertEquals(-1, preprocessor.getPostfixFunctionStart("5.4434234!", 8));
		Assert.assertEquals(1, preprocessor.getPostfixFunctionStart("2+5!", 2));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+5.4434234!", 13));
		Assert.assertEquals(14, preprocessor.getPostfixFunctionStart("2.23+5.4434234*5!", 15));
		Assert.assertEquals(14, preprocessor.getPostfixFunctionStart("2.23+5.4434234*5.1!", 17));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+(5.4434234*5.1)!", 19));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+(5.4434234*(5.1+1))!", 23));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+(5.4434234*sin(5.1+1))!", 26));
		Assert.assertEquals(0, preprocessor.getPostfixFunctionStart("sin(5)!", 5));
		Assert.assertEquals(0, preprocessor.getPostfixFunctionStart("sin(5sin(5sin(5)))!", 17));
		Assert.assertEquals(1, preprocessor.getPostfixFunctionStart("2+sin(5sin(5sin(5)))!", 19));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+sin(5.4434234*sin(5.1+1))!", 29));
	}
}
