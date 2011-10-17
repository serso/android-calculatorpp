/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.model.CalculatorEngine;

/**
 * User: serso
 * Date: 10/5/11
 * Time: 1:25 AM
 */
public class MathTypeTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorEngine.instance.init(null, null);
	}

	@Test
	public void testGetType() throws Exception {
		Assert.assertEquals(MathType.function, MathType.getType("sin", 0).getMathType());
		Assert.assertEquals(MathType.text, MathType.getType("sn", 0).getMathType());
		Assert.assertEquals(MathType.text, MathType.getType("s", 0).getMathType());
		Assert.assertEquals(MathType.text, MathType.getType("", 0).getMathType());

		try {
			Assert.assertEquals(MathType.text, MathType.getType("22", -1).getMathType());
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			Assert.assertEquals(MathType.text, MathType.getType("22", 2).getMathType());
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}
	}
}
