/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.jscl;

import jscl.math.Expression;
import jscl.math.Generic;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.model.CalculatorEngine;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 10:42 PM
 */
public class FromJsclNumericTextProcessorTest {

		@BeforeClass
	public static void setUp() throws Exception {
		CalculatorEngine.instance.init(null, null);
	}

	@Test
	public void testCreateResultForComplexNumber() throws Exception {
		final FromJsclNumericTextProcessor cm = new FromJsclNumericTextProcessor();

		Assert.assertEquals("1.22133+23 123i", cm.process(Expression.valueOf("1.22133232+23123*i").numeric()));
		Assert.assertEquals("1.22133+1.2i", cm.process(Expression.valueOf("1.22133232+1.2*i").numeric()));
		Assert.assertEquals("1.22i", cm.process(Expression.valueOf("1.22*i").numeric()));
		Assert.assertEquals("i", cm.process(Expression.valueOf("i").numeric()));
		Generic numeric = Expression.valueOf("e^(Π*i)+1").numeric();
		junit.framework.Assert.assertEquals("0i", cm.process(numeric));
	}
}
